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
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
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

public class DetailsActivity extends MainActivity {

    private DetailsViewModel viewModel;
    RequestQueue queue;
    // widgets
    private TextView textTitle, textPrice, textPriceEur, textDescription, textLocation;
    private ImageView imgItem;
    private Button btnMessage;
    private ImageButton btnMap;
    AccessToken accessToken; //= new AccessToken();

    private ExecutorService executor;
    private WebAPI webAPI;
    private LocationUtility locationUtility;
    private FirebaseStorage mStorageRef;
    private SalesItem selectedItem;
    private Location location;
    private String bearerToken = null;

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

                getExchangeRates(price);
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void sendMobilepayrequest(View view) throws JSONException {
        // 1. Perform a check in on a beaconId: POST /app/usersimulation/checkin
        // 2. Initiate a payment through the PoS API: POST /api/v10/payments
        //  A. Accept the payment: POST /app/usersimulation/acceptpayment
        //  B. Cancel payment: POST /app/usersimulation/cancelpaymentbyuser

        //Send authentication request to get access token for the PoS API
        sendAuthenticationRequest(Constants.SANDBOX_URL);

        // Send User Simulation Checkin.
        sendPoSCheckinRequests(Constants.PoS_CHECKIN_URL);

        //Create PoS, iniate payment
        //sendInitiatePaymentRequest(Constants.NEW_PAYMENT_URL);

        //Initiate payment through the PoS API
        //sendPaymentRequest(Constants.ACCEPT_PAYMENT_URL);

        //sendPoSRequest(accessToken);
        gotoMobilepayQR();
    }

    private void sendInitiatePaymentRequest(String url) throws JSONException {
        if(queue==null){
            queue = Volley.newRequestQueue(this);
        }
        JSONObject jsonBody = new JSONObject();
        String price = textPrice.getText().toString();
        String priceonly = price.split(" ")[0].trim();
        jsonBody.put("amount", Integer.parseInt(priceonly));
        final String mRequestBody = jsonBody.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("Mobilepay", "onResponse: " + response);
                },
                error -> Log.d("Mobilepay", "onError: " + error)) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("accept", "application/json");
                params.put("authorization", bearerToken);
                params.put("content-type", "application/*+json");
                params.put("correlationid", Constants.CORRELATION_ID);
                params.put("x-ibm-client-id", Constants.CLIENT_ID);
                params.put("x-mobilepay-client-system-version", "2.1.1");
                params.put("x-mobilepay-idempotency-key", "key1");
                params.put("x-mobilepay-merchant-vat-number", Constants.MERCHANT_VAT);

                return params;
            }
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<>();
                params.put("currencyCode", "DKK");
                params.put("orderId", "Order - 1");
                params.put("plannedCaptureDelay", "None");
                params.put("posId", Constants.POS_ID);
                params.put("merchantPaymentLabel", selectedItem.getUser());
                return params;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return mRequestBody == null ? null : mRequestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    Log.d("InitiatePayment body", "getBody: " + e);
                    return null;
                }
            }
        };
        queue.add(stringRequest);
    }

    private void sendPaymentRequest(String url) throws JSONException {
        if(queue==null){
            queue = Volley.newRequestQueue(this);
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("Mobilepay", "onResponse: " + response);
                },
                error -> Log.d("Mobilepay", "onError: " + error)) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Accept", "application/json");
                params.put("Content-Type", "application/json");
                params.put("x-ibm-client-id", "sE5wD8qP1lQ8uM5wJ0uO0nE3kR8aU5iA2oI5iK0eQ6tB1kN0uL"); //sE5wD8qP1lQ8uM5wJ0uO0nE3kR8aU5iA2oI5iK0eQ6tB1kN0uL
                params.put("X-IBM-Client-Secret", Constants.CLIENT_CREDENTIALS_SECRET);

                return params;
            }
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<>();
                params.put("beaconId", "147025836912345");
                params.put("phoneNumber", "+4522134410");
                return params;
            }
        };
        queue.add(stringRequest);
    }

    private void sendPoSCheckinRequests(String url) throws JSONException {
        if(queue==null){
            queue = Volley.newRequestQueue(this);
        }
        bearerToken = "Bearer " + accessToken.getAccess_token();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("Mobilepay Checkin", "onResponse: " + response);
                },
                error -> Log.d("Mobilepay", "onError: " + error)) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Accept", "application/json");
                params.put("Content-Type", "application/json");
                //params.put("Content-Length", "70");
                //params.put("Host", "");
                params.put("Authorization", bearerToken);
                params.put("x-ibm-client-id", Constants.CLIENT_ID); //sE5wD8qP1lQ8uM5wJ0uO0nE3kR8aU5iA2oI5iK0eQ6tB1kN0uL
                params.put("X-IBM-Client-Secret", Constants.CLIENT_CREDENTIALS_SECRET);

                return params;
            }
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<>();
                params.put("beaconId", Constants.BEACON_ID);
                params.put("phoneNumber", "+4520031801"); //  +4520031801
                return params;
            }
        };
        queue.add(stringRequest);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void sendAuthenticationRequest(String url) throws JSONException {
        if(queue==null){
            queue = Volley.newRequestQueue(this);
        }
        //encode client id + secret
        String authString = (Constants.CLIENT_ID + ":" + "hEt5IUrYrVY8pKnyp2SAOvWAqqpIzC3qqAAz9tOA3JE");
        String encodedAuth = Base64.getEncoder().encodeToString(authString.getBytes());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("Mobilepay", "onResponse: " + response);
                    Gson gson = new GsonBuilder().create();
                    accessToken =  gson.fromJson(response, AccessToken.class);
                    Log.d("token", "accesstoken value: " + accessToken.getAccess_token());
                },
                error -> Log.d("Mobilepay", "onError: " + error)) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                params.put("x-ibm-client-id", Constants.CLIENT_ID);
                params.put("Authorization", "Basic " + encodedAuth);

                return params;
            }
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<>();
                params.put("grant_type", "client_credentials");
                params.put("merchant_vat", Constants.MERCHANT_VAT);
                return params;
            }
        };
        queue.add(stringRequest);
    }

    private void gotoMobilepayQR() {
        Intent intent = new Intent(this, MobilePayActivity.class);
        startActivity(intent);
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
                double eurPrice = price*eur;
                String sPrice = String.format(java.util.Locale.getDefault(),"%.2f \u20ac", eurPrice);
                textPriceEur.setText(sPrice);
            };
            webAPI.loadData(callback);
        });
    }

}
