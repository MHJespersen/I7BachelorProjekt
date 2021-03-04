package mmm.i7bachelor_smartsale.app.Activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import mmm.i7bachelor_smartsale.app.Models.SalesItem;
import mmm.i7bachelor_smartsale.app.R;
import mmm.i7bachelor_smartsale.app.Utilities.Constants;
import mmm.i7bachelor_smartsale.app.Utilities.LocationUtility;
import mmm.i7bachelor_smartsale.app.ViewModels.CreateSaleViewModel;
import mmm.i7bachelor_smartsale.app.ViewModels.CreateSaleViewModelFactory;

public class CreateSaleActivity extends MainActivity {

    //upload
    private FirebaseStorage firebaseStorage;

    public static CreateSaleActivity context;
    private CreateSaleViewModel viewModel;
    private SalesItem salesItem;
    private LocationUtility locationUtility;

    final String APP_TAG = "SmartSale";
    private static final String TAG = "CreateSaleActivity";

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final long MIN_TIME_BETWEEN_LOCATION_UPDATES = 5 * 1000;    // milliseconds
    private static final float MIN_DISTANCE_MOVED_BETWEEN_LOCATION_UPDATES = 1;  // meters
    private static final int PERMISSIONS_REQUEST_LOCATION = 789;
    private static final String KEY_PHOTO = "photo";
    private static boolean locationPermissionGranted = false;


    private LocationManager locationManager;
    private Location lastLocation = null;
    private Boolean isTrackingLocation;
    public File photoFile;
    public String photoFileName;

    //widgets
    private TextView saleHeader;
    private EditText title, price, description, location;
    private ImageView itemImage;
    private Button btnCapture, btnGetLocation, btnCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createsale);
        firebaseStorage = FirebaseStorage.getInstance();
        locationUtility = new LocationUtility(this);
        photoFileName = createFileName();

        // Calling / creating ViewModel with the factory pattern is inspired from:
        // https://stackoverflow.com/questions/46283981/android-viewmodel-additional-arguments
        viewModel = new ViewModelProvider(this, new CreateSaleViewModelFactory(this.getApplicationContext()))
                .get(CreateSaleViewModel.class);

        setupUI();

        startTrackingLocation();

        if (savedInstanceState != null && !savedInstanceState.isEmpty()) {
            if (!savedInstanceState.getString(KEY_PHOTO).isEmpty()) {
                photoFile = new File(savedInstanceState.getString(KEY_PHOTO));
                Bitmap bp = BitmapFactory.decodeFile(savedInstanceState.getString(KEY_PHOTO));
                itemImage.setImageBitmap(bp);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!isTrackingLocation) {
            startTrackingLocation();
        }
    }

    @Override
    protected void onPause() {
        stopTrackingLocation();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (isTrackingLocation) {
            stopTrackingLocation();
        }
        super.onDestroy();
    }

    private void setupUI() {
        btnCreate = findViewById(R.id.btnPublish);
        btnCreate.setOnClickListener(view -> {
            //Save file:
            if (photoFile != null) {

                Uri file = Uri.fromFile(photoFile);
                StorageReference imgRef = firebaseStorage.getReference().child(photoFileName);
                imgRef.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Save();
                        Log.d("Successfull upload!", APP_TAG);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Unsuccesfull upload!", APP_TAG);
                    }
                });
            } else {
                Save();
            }

        });

        btnCapture = findViewById(R.id.btnTakePhoto);
        btnCapture.setOnClickListener(view -> buttonCapture());

        btnGetLocation = findViewById(R.id.createSaleBtnGetLocation);
        btnGetLocation.setOnClickListener(view -> {
            checkPermissions();
            getDeviceLocation();
        });

        price = findViewById(R.id.createSaleTextPrice);
        title = findViewById(R.id.createSaleTextTitle);
        itemImage = findViewById(R.id.imgTaken);
        saleHeader = findViewById(R.id.txtCreateSaleHeader);
        description = findViewById(R.id.editTxtEnterDescription);
        location = findViewById(R.id.createSaleTextLocation);
    }

    //Camera Code inspired by:
    //https://developer.android.com/training/camera/photobasics
    //https://www.tutlane.com/tutorial/android/android-camera-app-with-examples
    //https://guides.codepath.com/android/Accessing-the-Camera-and-Stored-Media#using-capture-intent
    private void buttonCapture() {

        //Create intent to take picture and return control to the calling application
        Intent cInt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //Create a File reference for future access
        createFileName();
        photoFile = getPhotoFileUri(photoFileName);

        //Wrap file object into a content provider, Required for API >= 24
        //See  https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(CreateSaleActivity.this, "mmm.fileprovider", photoFile);
        cInt.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (cInt.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cInt, REQUEST_IMAGE_CAPTURE);
        }
    }

    //Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);
        //Create the storage dir if it doesn't exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(APP_TAG, "failed to create directory");
        }
        //Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);
        return file;
    }

    //Get the thumbnail
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                Bitmap bp = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                itemImage.setImageBitmap(bp);

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, getString(R.string.canceled), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if (photoFile != null) {
            outState.putString(KEY_PHOTO, photoFile.getAbsolutePath());
            super.onSaveInstanceState(outState);
        }
    }

    private String createFileName() {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        return "JPEG_" + timeStamp + ".jpg";
    }

    public void Save() {
        salesItem = new SalesItem();
        //set imageName
        if (photoFileName != null) {
            salesItem.setImage(photoFileName);
        } else {
            salesItem.setImage("emptycart.png");
        }
        //set Text
        if (title.getText().toString() != null) {
            salesItem.setTitle(title.getText().toString());
        }
        //set price
        if (price.getText().toString() != null) {
            salesItem.setPrice(Double.parseDouble(price.getText().toString()));
        }
        //set location
        if (location.getText().toString() != null) {
            if (lastLocation == null) {
                Location loc = locationUtility.getLocationFromString(location.getText().toString());
                salesItem.setLocation(loc);
            } else {
                salesItem.setLocation(lastLocation);
            }
        }
        if (description.getText().toString() != null) {
            salesItem.setDescription(description.getText().toString());
        }
        salesItem.setUser(auth.getCurrentUser().getEmail());
        viewModel.updateSalesItem(salesItem);
        finish();
    }

    // Location permissions - should apparently not be in view model?
    // https://developers.google.com/maps/documentation/android-sdk/current-place-tutorial
    // Also from L8 demo 2/3

    private void getDeviceLocation() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationClient.getLastLocation();
                locationResult.addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        lastLocation = task.getResult();
                        if (lastLocation != null) {
                            double lat = lastLocation.getLatitude();
                            double lon = lastLocation.getLongitude();
                            String s = locationUtility.getCityName(lat, lon);
                            Log.d(Constants.CREATE_SALE_ACTIVITY, "getDeviceLocation: " + lat + ", " + lon);
                            Log.d(Constants.CREATE_SALE_ACTIVITY, "getDeviceLocation: " + s);
                            location.setText(s);
                        } else {
                            Toast.makeText(this, getString(R.string.location_not_found), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d(Constants.CREATE_SALE_ACTIVITY, "Current location is null. Using defaults.");
                        Log.e(Constants.CREATE_SALE_ACTIVITY, "Exception: %s", task.getException());
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    private void checkPermissions() {
        try {
            if (locationPermissionGranted) {
                // something
            } else {
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
    }

    private void startTrackingLocation() {
        try {
            if (locationManager == null) {
                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            }

            long minTime = MIN_TIME_BETWEEN_LOCATION_UPDATES;
            float minDistance = MIN_DISTANCE_MOVED_BETWEEN_LOCATION_UPDATES;
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setPowerRequirement(Criteria.POWER_MEDIUM);

            if (locationManager != null) {
                try {
                    //Use criteria to chose best provider
                    locationManager.requestLocationUpdates(minTime, minDistance, criteria, locationListener, null);
                } catch (SecurityException ex) {
                    // user has disabled location permission - need to validate this permission for newer versions?
                    Log.d(TAG, "startTrackingLocation: User has disabled location services");
                }
            }

            isTrackingLocation = true;
            Log.d(TAG, "startTrackingLocation");

        } catch (Exception ex) {
            Log.e("TRACKER", "Error starting location tracking", ex);
        }
    }

    private void stopTrackingLocation() {
        try {
            try {
                locationManager.removeUpdates(locationListener);
                isTrackingLocation = false;
                Log.d(TAG, "stopTrackingLocation");

            } catch (SecurityException ex) {
                // user has disabled location permission - need to validate this permission for newer versions?
                Log.d(TAG, "stopTrackingLocation: User has disabled location services");
            }

        } catch (Exception ex) {
            // if listener is null
            Log.e("TRACKER", "Error stopping location tracking", ex);
        }
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d(Constants.CREATE_SALE_ACTIVITY, "onLocationChanged: " + location.getLatitude() + ", " + location.getLongitude());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };
}
