package mmm.i7bachelor_smartsale.app.Activities;

import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import mmm.i7bachelor_smartsale.app.Models.AccessToken;
import mmm.i7bachelor_smartsale.app.Models.SalesItem;
import mmm.i7bachelor_smartsale.app.R;
import mmm.i7bachelor_smartsale.app.Utilities.Constants;
import mmm.i7bachelor_smartsale.app.Utilities.LocationUtility;
import mmm.i7bachelor_smartsale.app.ViewModels.DetailsViewModel;
import mmm.i7bachelor_smartsale.app.ViewModels.DetailsViewModelFactory;
import mmm.i7bachelor_smartsale.app.Webapi.Callback;
import mmm.i7bachelor_smartsale.app.Webapi.WebAPI;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DetailsActivity extends MainActivity {

    private DetailsViewModel viewModel;
    RequestQueue queue;
    // widgets
    private TextView textTitle, textPrice, textPriceEur, textDescription, textLocation;
    private ImageView imgItem;
    private Button btnMessage, mobilepaybtn;
    private ImageButton btnMap;
    private static AccessToken accessToken = new AccessToken();

    private ExecutorService executor;
    private WebAPI webAPI;
    private LocationUtility locationUtility;
    private FirebaseStorage mStorageRef;
    private SalesItem selectedItem;
    private Location location;
    private String bearerToken = "";
    private OkHttpClient client = new OkHttpClient().newBuilder().build();
    private String paymentId = "";
    private final int MOBILEPAY_RESULT_CODE = 100;
    boolean checkedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        executor = Executors.newSingleThreadExecutor();
        webAPI = new WebAPI(this);
        locationUtility = new LocationUtility(this);
        mStorageRef = FirebaseStorage.getInstance();

        viewModel = new ViewModelProvider(this, new DetailsViewModelFactory(this.getApplicationContext()))
                .get(DetailsViewModel.class);

        setupUI();
        viewModel.returnSelected().observe(this, updateObserver);
    }

    Observer<SalesItem> updateObserver = new Observer<SalesItem>() {
        @Override
        public void onChanged(SalesItem Item) {
            if (Item != null) {
                selectedItem = Item;
                textTitle.setText(Item.getTitle());
                textDescription.setText(Item.getDescription());

                if (Item.getTitle().equals("Sold"))
                    mobilepaybtn.setVisibility(View.GONE);
                else
                    mobilepaybtn.setVisibility(View.VISIBLE);

                double price = Item.getPrice();
                // Check price for decimals, if zero, don't show
                String sPrice;
                if (price % 1 == 0) {
                    sPrice = String.format(java.util.Locale.getDefault(), "%.0f kr", price);
                } else {
                    sPrice = String.format(java.util.Locale.getDefault(), "%.2f kr", price);
                }
                textPrice.setText(sPrice);

                location = Item.getLocation();
                String sLocation = locationUtility.getCityName(location.getLatitude(), location.getLongitude());
                textLocation.setText(sLocation);

                if (!Item.getImage().equals("")) {
                    StorageReference strRef = mStorageRef.getReference().child(Item.getImage());
                    strRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageURL = uri.toString();
                        Glide.with(imgItem).load(imageURL).into(imgItem);
                    }).addOnFailureListener(exception -> Glide.with(imgItem).load(R.drawable.emptycart).into(imgItem));
                } else {
                    Glide.with(imgItem).load(R.drawable.emptycart).into(imgItem);
                }

                getExchangeRates(price);
            }
        }
    };

    private void setupUI() {

        mobilepaybtn = findViewById(R.id.mobilepaybtn);
        textTitle = findViewById(R.id.detailsTextTitle);
        imgItem = findViewById(R.id.detailsImage);
        textPrice = findViewById(R.id.detailsTextPrice);
        textPriceEur = findViewById(R.id.detailsTextEur);
        textDescription = findViewById(R.id.detailsTextDesc);
        textLocation = findViewById(R.id.detailsTextLocation);
        imgItem = findViewById(R.id.detailsImage);
        btnMessage = findViewById(R.id.detailsBtnMessage);
        btnMap = findViewById(R.id.detailsBtnMap);
        textDescription.setMovementMethod(new ScrollingMovementMethod());
        btnMessage.setOnClickListener(view -> gotoSendMessage());
        btnMap.setOnClickListener(view -> gotoMap());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void sendMobilepayrequest(View view) throws JSONException {
        // 1. Perform a check in on a beaconId: POST /app/usersimulation/checkin
        // 2. Initiate a payment through the PoS API: POST /api/v10/payments
        //  A. Accept the payment: POST /app/usersimulation/acceptpayment (Done in mobilepay by the user)
        //  B. Cancel payment: POST /app/usersimulation/cancelpaymentbyuser (Done in mobilepay by the user)

        //Authenticate requests and get access token.
        sendAuthenticationRequest(Constants.SANDBOX_URL);

        // Check when user is checked in at mobilepay app and initiate payment af that.
        getCheckedInUsers();
        gotoMobilepayQR();

        //When user returns from mobilepay, cancel payment if the user didnt swipe to accept payment.
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendAuthenticationRequest(String url) throws JSONException {
        if (queue == null) {
            queue = Volley.newRequestQueue(this);
        }
        //encode client id + secret
        String authString = (Constants.CLIENT_ID + ":" + "hEt5IUrYrVY8pKnyp2SAOvWAqqpIzC3qqAAz9tOA3JE");
        String encodedAuth = Base64.getEncoder().encodeToString(authString.getBytes());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Gson gson = new GsonBuilder().create();
                    accessToken = gson.fromJson(response, AccessToken.class);
                    bearerToken = "Bearer " + accessToken.getAccess_token();
                },
                error -> Log.d("Mobilepay", "onError: " + error)) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                params.put("x-ibm-client-id", Constants.CLIENT_ID);
                params.put("Authorization", "Basic " + encodedAuth);

                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("grant_type", "client_credentials");
                params.put("merchant_vat", Constants.MERCHANT_VAT);
                return params;
            }
        };
        queue.add(stringRequest);
    }

    private void getCheckedInUsers() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    while (bearerToken.isEmpty()) {
                    }
                    okhttp3.Request request = new okhttp3.Request.Builder()
                            .url("https://api.sandbox.mobilepay.dk/pos/v10/pointofsales/5e6bbcc6-154c-44bb-9a82-45acc1aaea7b/checkin")
                            .method("GET", null)
                            .addHeader("Authorization", "Bearer " + accessToken.getAccess_token())
                            .addHeader("x-ibm-client-id", Constants.CLIENT_ID)
                            .addHeader("Accept", "application/json")
                            .addHeader("Content-Type", "application/json")
                            .addHeader("X-Mobilepay-Client-System-Version", "2.1.1")
                            .build();
                    try {
                        Response response = client.newCall(request).execute();
                        Log.d("Response", "" + response);
                        String jsonData = response.body().string();
                        JSONObject Jobject = new JSONObject(jsonData);
                        boolean isCheckedIn = Jobject.getBoolean("isUserCheckedIn");
                        if (isCheckedIn) {
                            Log.d("Bool", "CHECKED IN");
                            sendInitiatePaymentRequest(Constants.NEW_PAYMENT_URL);
                            break;
                        }
                        Thread.sleep(4000);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("Stacktrace", "Error parsing" + e);
                    }
                }
            }
        });
    }

    // We have to sleep this thread as
    private void sendInitiatePaymentRequest(String url) throws JSONException {
        Integer priceonly = Integer.parseInt(textPrice.getText().toString().split(" ")[0].trim());
        executor.execute(new Runnable() {
            @Override
            public void run() {
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, String.format("{\r\n    \"posId\":\"5e6bbcc6-154c-44bb-9a82-45acc1aaea7b\",\r\n    \"orderId\":\"Order - 1\",\r\n    \"amount\": %s,\r\n    \"currencyCode\":\"DKK\",\r\n    \"merchantPaymentLabel\": \"%s\",\r\n    \"plannedCaptureDelay\":\"None\"\r\n}", priceonly, textTitle.getText()));
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(url) //https://api.sandbox.mobilepay.dk/pos/v10/paymentsq
                        .method("POST", body)
                        .addHeader("Accept", "application/json")
                        .addHeader("content-type", "application/json")
                        .addHeader("x-ibm-client-id", Constants.CLIENT_ID)
                        .addHeader("X-Mobilepay-Client-System-Version", "2.1.1")
                        .addHeader("X-Mobilepay-Idempotency-Key", java.util.UUID.randomUUID().toString())
                        .addHeader("Authorization", "Bearer " + accessToken.getAccess_token())
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    Log.d("Response", "INITIATED");
                    String jsonData = response.body().string();
                    JSONObject Jobject = new JSONObject(jsonData);
                    paymentId = Jobject.getString("paymentId");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("Response", "INIT ERROR");
                }
            }
        });
    }

    private void CapturePaymentRequest() throws JSONException {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                MediaType mediaType = MediaType.parse("application/*+json");
                String price = textPrice.getText().toString().split(" ")[0].trim();
                Integer priceonly = Integer.parseInt(price);
                RequestBody body = RequestBody.create(mediaType, String.format("{\r\n  \"amount\": \"%s\"}", priceonly));
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(String.format("https://api.sandbox.mobilepay.dk/pos/v10/payments/%s/capture", paymentId))
                        .method("POST", body)
                        .addHeader("x-ibm-client-id", Constants.CLIENT_ID)
                        .addHeader("x-mobilepay-merchant-vat-number", Constants.MERCHANT_VAT)
                        .addHeader("x-mobilepay-client-system-version", "2.1.1")
                        .addHeader("Authorization", bearerToken)
                        .addHeader("content-type", "application/*+json")
                        .addHeader("accept", "application/json")
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    Log.d("Response", "" + response.toString());

                } catch (Exception e) {
                    Log.d("Exception", "" + e);
                    e.printStackTrace();
                }
            }
        });
    }

    private void gotoMobilepayQR() {
        Intent intent = new Intent(this, MobilePayActivity.class);
        startActivityForResult(intent, MOBILEPAY_RESULT_CODE);
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

    public void getExchangeRates(double price) {

        if (executor == null) {
            executor = Executors.newSingleThreadExecutor();
        }

        executor.submit(() -> {
            Callback callback = exchangeRates -> {
                // What happens on API call completion
                double eur = exchangeRates.getRates().getEUR();
                double eurPrice = price * eur;
                String sPrice = String.format(java.util.Locale.getDefault(), "%.2f \u20ac", eurPrice);
                textPriceEur.setText(sPrice);
            };
            webAPI.loadData(callback);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MOBILEPAY_RESULT_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    CapturePaymentRequest();
                    Log.d("document path", "onActivityResult: path: " + selectedItem.getPath());
                    viewModel.setItemSold(selectedItem.getPath());
                    Toast.makeText(this, ("PAYMENT ACCEPTED"), Toast.LENGTH_LONG).show();
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, ("PAYMENT CANCELED"), Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}