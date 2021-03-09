package mmm.i7bachelor_smartsale.app.Activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.concurrent.ExecutorService;

import mmm.i7bachelor_smartsale.app.Models.SalesItem;
import mmm.i7bachelor_smartsale.app.R;
import mmm.i7bachelor_smartsale.app.Utilities.Constants;
import mmm.i7bachelor_smartsale.app.Utilities.LocationUtility;
import mmm.i7bachelor_smartsale.app.ViewModels.DetailsViewModel;
import mmm.i7bachelor_smartsale.app.ViewModels.DetailsViewModelFactory;
import mmm.i7bachelor_smartsale.app.Webapi.WebAPI;

public class DetailsActivity extends MainActivity {

    private DetailsViewModel viewModel;

    // widgets
    private TextView textTitle, textPrice, textPriceEur, textDescription, textLocation;
    private ImageView imgItem;
    private Button btnMessage;
    private ImageButton btnMap;

    private ExecutorService executor;
    private WebAPI webAPI;
    private LocationUtility locationUtility;
    private FirebaseStorage mStorageRef;
    private SalesItem selectedItem;
    private Location location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        webAPI = new WebAPI(this);
        locationUtility = new LocationUtility(this);
        mStorageRef = FirebaseStorage.getInstance();

        viewModel = new ViewModelProvider(this, new DetailsViewModelFactory(this.getApplicationContext()))
                .get(DetailsViewModel.class);

        setupUI();

        viewModel.returnSelected().observe(this, updateObserver );
    }

    Observer<SalesItem> updateObserver = new Observer<SalesItem>() {
        @Override
        public void onChanged(SalesItem Item) {

            if(Item != null)
            {
                selectedItem = Item;
                textTitle.setText(Item.getTitle());
                textDescription.setText(Item.getDescription());

                double price = Item.getPrice();
                // Check price for decimals, if zero, don't show
                String sPrice;
                if (price % 1 == 0) {
                    sPrice = String.format(java.util.Locale.getDefault(),"%.0f kr", price);
                } else {
                    sPrice = String.format(java.util.Locale.getDefault(),"%.2f kr", price);
                }
                textPrice.setText(sPrice);

                location = Item.getLocation();
                String sLocation = locationUtility.getCityName(location.getLatitude(), location.getLongitude());
                textLocation.setText(sLocation);

                if(!Item.getImage().equals(""))
                {
                    StorageReference strRef = mStorageRef.getReference().child(Item.getImage());
                    strRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageURL = uri.toString();
                        Glide.with(imgItem).load(imageURL).into(imgItem);
                    }).addOnFailureListener(exception -> Glide.with(imgItem).load(R.drawable.emptycart).into(imgItem));
                }
                else
                {
                    Glide.with(imgItem).load(R.drawable.emptycart).into(imgItem);
                }
            }
        }
    };

    private void setupUI() {

        textTitle = findViewById(R.id.detailsTextTitle);
        imgItem = findViewById(R.id.detailsImage);
        textPrice = findViewById(R.id.detailsTextPrice);
        textPriceEur = findViewById(R.id.detailsTextEur);
        textDescription = findViewById(R.id.detailsTextDesc);
        textLocation = findViewById(R.id.detailsTextLocation);
        imgItem= findViewById(R.id.detailsImage);
        btnMessage = findViewById(R.id.detailsBtnMessage);
        btnMap = findViewById(R.id.detailsBtnMap);

        textDescription.setMovementMethod(new ScrollingMovementMethod());

        btnMessage.setOnClickListener(view -> gotoSendMessage());
        btnMap.setOnClickListener(view -> gotoMap());
    }

    private void gotoSendMessage() {
        Intent intent = new Intent(this, SendMessageActivity.class);

        //Title of salesItem is used to set regarding field of message example:(Regarding: Chair)
        intent.putExtra(Constants.DETAILS_TITLE, selectedItem.getTitle());
        intent.putExtra(Constants.DETAILS_USER, selectedItem.getUser());
        startActivity(intent);
    }

    private void gotoMap() {

        double lat = location.getLatitude();
        double lng = location.getLongitude();

        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra(Constants.EXTRA_COORDS, new double[]{lat, lng});

        startActivity(intent);
    }
}
