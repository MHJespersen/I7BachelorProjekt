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
import com.android.volley.VolleyError;
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

import java.io.IOException;
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
    private Button btnMessage;
    private ImageButton btnMap;
    private static AccessToken accessToken = new AccessToken();

    private ExecutorService executor;
    private WebAPI webAPI;
    private LocationUtility locationUtility;
    private FirebaseStorage mStorageRef;
    private SalesItem selectedItem;
    private Location location;
    private String bearerToken = null;
    private OkHttpClient client = new OkHttpClient().newBuilder().build();

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
        sendInitiatePaymentRequest(Constants.NEW_PAYMENT_URL);

        //Initiate payment through the PoS API
        AcceptPaymentRequest(Constants.ACCEPT_PAYMENT_URL);

        //gotoMobilepayQR();
    }

    private void sendInitiatePaymentRequest(String url) throws JSONException {
        String price = textPrice.getText().toString().split(" ")[0].trim();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\r\n    \"posId\":\"5e6bbcc6-154c-44bb-9a82-45acc1aaea7b\",\r\n    \"orderId\":\"Order - 1\",\r\n    \"amount\": 50,\r\n    \"currencyCode\":\"DKK\",\r\n    \"merchantPaymentLabel\": \"TestUserName\",\r\n    \"plannedCaptureDelay\":\"None\"\r\n}");
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url("https://api.sandbox.mobilepay.dk/pos/v10/payments")
                        .method("POST", body)
                        .addHeader("Accept", "application/json")
                        .addHeader("content-type", "application/json")
                        .addHeader("x-ibm-client-id", "1170825e-c923-47c2-bdb7-ef35c7967efc")
                        .addHeader("X-Mobilepay-Client-System-Version", "2.1.1")
                        .addHeader("X-Mobilepay-Idempotency-Key", java.util.UUID.randomUUID().toString() )
                        .addHeader("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6IkE5QTdBQ0NGMTg4NEQwMUQ0QUIwRkZEMTA0OTEyNEI3NEIxRThCQUQiLCJ0eXAiOiJhdCtqd3QiLCJ4NXQiOiJxYWVzenhpRTBCMUtzUF9SQkpFa3Qwc2VpNjAifQ.eyJuYmYiOjE2MTgzMjEwNjUsImV4cCI6MTYxODMyNDY2NSwiaXNzIjoiaHR0cHM6Ly9hcGkubW9iaWxlcGF5LmRrL2ludGVncmF0b3ItYXV0aGVudGljYXRpb24iLCJhdWQiOiJodHRwczovL2FwaS5tb2JpbGVwYXkuZGsvaW50ZWdyYXRvci1hdXRoZW50aWNhdGlvbi9yZXNvdXJjZXMiLCJjbGllbnRfaWQiOiIxMTcwODI1ZS1jOTIzLTQ3YzItYmRiNy1lZjM1Yzc5NjdlZmMiLCJpbnRlZ3JhdG9yX2lkIjoiYmQzMjlhNDEtN2U2YS00ODZiLTljZDEtMzc3M2FhY2I3MGM3IiwiaW50ZWdyYXRvcl9uYW1lIjoiU21hcnRTYWxlIFN0dWRlbnQgUHJvamVjdCIsImludGVncmF0b3JjbGllbnRfbmFtZSI6IlNtYXJ0U2FsZSIsIm1lcmNoYW50X3ZhdCI6IkRLOTAwMDAwOTMiLCJqdGkiOiI2MDVCMjYyRTQ2MEY5NTBFNDc3MjdFQ0YxQzkwNUJCOSIsImlhdCI6MTYxODMyMTA2NSwic2NvcGUiOlsiaW50ZWdyYXRvcl9zY29wZSJdfQ.ZQli_vNPxGqXNs4sQC1jBwXOeR-cEImLYgIsRapysnSwyHEISgRuvM5bl3x2vhO3xkcpPsJTKbzrELkljCz7G0Dd_jyhkeNQfbEDUuUXFG0LQZz1MteIAECwwdMujkjsaaSS_W6wVKeN0YSvevalR0-VlStIcyHnhASug1oLqrkob9a6vjvNzGlX8Hndf_2J3q8zjvpsZv3uLeKKYe5IQM-EenegqslKAjLIR7Lvb8PY0DCbutgMNuo7S7z215YK3T0oXSMxDy7x7zRkjjlHdd-JGDZLxlKnAoPyOqXFxZ2agSZXHhf5HCGbBNht3nI527ajiBk7Pc3zSXtaiBJJqw")
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    Log.d("Response", "" + response.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void AcceptPaymentRequest(String url) throws JSONException {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\r\n  \"beaconId\": \"147025836912345\",\r\n  \"phoneNumber\": \"+4520031801\"\r\n}");
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url("https://api.sandbox.mobilepay.dk/pos/app/usersimulation/acceptpayment")
                        .method("POST", body)
                        .addHeader("Accept", "application/json")
                        .addHeader("x-ibm-client-id", "1170825e-c923-47c2-bdb7-ef35c7967efc")
                        .addHeader("x-ibm-client-secret", "sE5wD8qP1lQ8uM5wJ0uO0nE3kR8aU5iA2oI5iK0eQ6tB1kN0uL")
                        .addHeader("Authorization", "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6IkE5QTdBQ0NGMTg4NEQwMUQ0QUIwRkZEMTA0OTEyNEI3NEIxRThCQUQiLCJ0eXAiOiJhdCtqd3QiLCJ4NXQiOiJxYWVzenhpRTBCMUtzUF9SQkpFa3Qwc2VpNjAifQ.eyJuYmYiOjE2MTgzMDI1NDIsImV4cCI6MTYxODMwNjE0MiwiaXNzIjoiaHR0cHM6Ly9hcGkubW9iaWxlcGF5LmRrL2ludGVncmF0b3ItYXV0aGVudGljYXRpb24iLCJhdWQiOiJodHRwczovL2FwaS5tb2JpbGVwYXkuZGsvaW50ZWdyYXRvci1hdXRoZW50aWNhdGlvbi9yZXNvdXJjZXMiLCJjbGllbnRfaWQiOiIxMTcwODI1ZS1jOTIzLTQ3YzItYmRiNy1lZjM1Yzc5NjdlZmMiLCJpbnRlZ3JhdG9yX2lkIjoiYmQzMjlhNDEtN2U2YS00ODZiLTljZDEtMzc3M2FhY2I3MGM3IiwiaW50ZWdyYXRvcl9uYW1lIjoiU21hcnRTYWxlIFN0dWRlbnQgUHJvamVjdCIsImludGVncmF0b3JjbGllbnRfbmFtZSI6IlNtYXJ0U2FsZSIsIm1lcmNoYW50X3ZhdCI6IkRLOTAwMDAwOTMiLCJqdGkiOiIxQjc0MDhFNzdGNUQxMzRDOTQzRjgwQUUzQzZEQzQ0OCIsImlhdCI6MTYxODMwMjU0Miwic2NvcGUiOlsiaW50ZWdyYXRvcl9zY29wZSJdfQ.Kc0PbceKTqAA5zjJbX_64dyIY7ZtTeqFYM7H7MZFE3X9iT8WuAlzCxIIoDe2qpFNm7uqCaMO9DQC_I6AbpfqZMhDZ4c1H486WMJtXhP9_jYN1OY5T6bD3YmuYIoVs494nwWj6TNjdvGL6YY9ElYtrU62KS4zoK-e0KJfJzg1vO077EXsnDlJxevBbAx-8sl2un079b3cz2tWVTaZvyOdnVqGSecs7jFl-KKc1HkLaJr3whYXUw2S5sPgSO2J57p4XzVQ8MGYNsjrA-Mcij2ubgLoaR0MuNnqOGvYzKE1TXgdeXSHkOoDorowpqemfOP-ITxLKi1SW_Jt11MtHKEklQ")
                        .addHeader("Content-Type", "application/json")
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                } catch (Exception e) {
                    Log.d("Exception","" + e);
                    e.printStackTrace();
                }
            }
        });
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
                    Gson gson = new GsonBuilder().create();
                    accessToken =  gson.fromJson(response, AccessToken.class);
                    Log.d("Mobilepay", "Access token set");
                    Log.d("Mobilepay", accessToken.getAccess_token());

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

    private void sendPoSCheckinRequests(String url){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, "{\r\n  \"beaconId\": \"147025836912345\",\r\n  \"phoneNumber\": \"+4520031801\"\r\n}");
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url("https://api.sandbox.mobilepay.dk/pos/app/usersimulation/checkin")
                        .method("POST", body)
                        .addHeader("Content-Length", "70")
                        .addHeader("x-ibm-client-id", "1170825e-c923-47c2-bdb7-ef35c7967efc")
                        .addHeader("x-ibm-client-secret", "sE5wD8qP1lQ8uM5wJ0uO0nE3kR8aU5iA2oI5iK0eQ6tB1kN0uL")
                        .addHeader("Accept", "application/json")
                        .addHeader("Content-Type", "application/json")
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    Log.d("Response", "" + response.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
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
