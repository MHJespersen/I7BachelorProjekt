package mmm.i7bachelor_smartsale.app.Models;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Build;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import java.util.Base64;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;

import mmm.i7bachelor_smartsale.app.Utilities.Constants;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Repository {

    private static Repository INSTANCE = null;
    private final FirebaseFirestore firestore;
    FirebaseAuth auth;
    private MutableLiveData<SalesItem> SelectedItemLive;
    private MutableLiveData<List<PrivateMessage>> PrivateMessagesList;
    private MutableLiveData<List<SalesItem>> MarketsList;
    private MutableLiveData<List<Pair<String, Integer>>> ConvoAndUnread;
    private OkHttpClient client = new OkHttpClient().newBuilder().build();
    private ExecutorService executor;
    private static Context con;
    private RequestQueue queue;
    private static AccessToken MPaccessToken = new AccessToken();
    private String bearerToken = "";
    private String paymentId = "";

    public static Repository getInstance(Context context) {
        con = context;
        if (INSTANCE == null) {
            INSTANCE = new Repository();
            Log.d("Repo", "Created Instance: ");
        }
        Log.d("Repo", "Repo Already instantiated: ");
        return(INSTANCE);
    }

    Repository()
    {
        ConvoAndUnread = new MutableLiveData<>();
        PrivateMessagesList = new MutableLiveData<>();
        SelectedItemLive = new MutableLiveData<>();
        MarketsList = new MutableLiveData<>();
        executor = Executors.newSingleThreadExecutor();
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }
    public GeoPoint GeoCreater(Location l){
        GeoPoint geo = new GeoPoint(l.getLatitude(), l.getLongitude());
        return geo;
    }

    public void setSelectedItem(String ItemID)
    {
        Task<DocumentSnapshot> d = firestore.collection("SalesItems").
                document(ItemID).get();
        d.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                SelectedItemLive.postValue(SalesItem.fromSnapshot(task.getResult()));
            }
        });
    }

    public LiveData<SalesItem> getSelectedItem() {
        return SelectedItemLive;
    }

    public MutableLiveData<List<SalesItem>> getItems()
    {
        setMarketsList();
        return MarketsList;
    }

    // get data
    //https://firebase.google.com/docs/firestore/query-data/get-data
    // https://firebase.google.com/docs/firestore/query-data/listen
    private void setMarketsList()
    {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection("SalesItems")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshopValue,
                                        @Nullable FirebaseFirestoreException error) {
                        ArrayList<SalesItem> updatedListOfItems = new ArrayList<>();
                        if(snapshopValue != null && !snapshopValue.isEmpty())
                        {
                            for (DocumentSnapshot item: snapshopValue.getDocuments()) {
                                SalesItem newItem = new SalesItem(
                                        item.get("title").toString(),
                                        item.get("description").toString(),
                                        Float.parseFloat(item.get("price").toString()),
                                        item.get("user").toString(),
                                        item.get("image").toString(),
                                        SalesItem.createLocationPoint(item.get(
                                                "location", GeoPoint.class)),
                                        item.get("documentPath").toString()
                                );
                                updatedListOfItems.add(newItem);
                            }
                        }
                        MarketsList.setValue(updatedListOfItems);
                    }
                });
    }

    //Adding data
    //https://firebase.google.com/docs/firestore/manage-data/add-data
    //https://stackoverflow.com/questions/51234670/firestore-oncompletelistener
    public void sendMessage(PrivateMessage privateMessage)
    {
        Map<String, Object> mmap = new HashMap<>();
        Map<String, Object> cmap = new HashMap<>();
        cmap.put("Initialized", "");
        DocumentReference DocRef = firestore.collection("PrivateMessages").
                document(privateMessage.getReceiver()).
                collection("Conversations").
                document(auth.getCurrentUser().getEmail());
        //To avoid having virtual documents in firebase that we can only see subcollections of.
        //We add the cmap to initialize and make the possibly new document non-virtual.
        DocRef.set(cmap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                String UniqueID = DocRef.collection("Messages").document().getId();
                mmap.put("Sender", privateMessage.getSender());
                mmap.put("MessageDate", privateMessage.getMessageDate());
                mmap.put("MessageBody", privateMessage.getMessageBody());
                mmap.put("Read", false);
                mmap.put("Receiver", privateMessage.getReceiver());
                mmap.put("Path", UniqueID);
                DocRef.collection("Messages").document(UniqueID).set(mmap)
                        .addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                Log.d("PrivateMessages", "Completed");
                            }
                        });
            }
        });
    }

    public MutableLiveData<List<PrivateMessage>> getPrivateMessages(){
        return this.PrivateMessagesList;
    }
    public MutableLiveData<List<Pair<String, Integer>>> getConvosAndReadStatus()
    {
        InitInbox();
        return ConvoAndUnread;
    }

    public void InitInbox()
    {
        //Listen for new conversations
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            firestore.collection(
                    "PrivateMessages").document(auth.getCurrentUser().getEmail()).
                    collection("Conversations").
                    addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            List<Pair<String, Integer>> usersAndRead = new ArrayList();
                            if (!value.isEmpty()) {
                                for (QueryDocumentSnapshot snap : value) {
                                    snap.getReference().collection("Messages").
                                            //Listen to unread messages
                                            whereEqualTo("Read", false).
                                            addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @SuppressLint("NewApi")
                                        @Override
                                          public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                            Predicate<Pair<String,Integer>> condition = msg -> msg.first.equals(snap.getId());
                                            usersAndRead.removeIf(condition);
                                           usersAndRead.add(new Pair<>(snap.getId(),
                                                   value.getDocuments().size()));
                                            ConvoAndUnread.setValue(usersAndRead);
                                        }
                                    });
                                }
                            }
                        }
                    });
    }}


    public void initializePrivateMessages(String receiver)
    {
        //Get messages from selected conversation on both ends
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            ArrayList privateMessages = new ArrayList();
            firestore.collection(
                    "PrivateMessages").document(auth.getCurrentUser().getEmail()).
                    collection("Conversations").document(receiver).collection("Messages").
                    addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @SuppressLint("NewApi")
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (!value.isEmpty()) {
                                for (QueryDocumentSnapshot snapMsg : value) {
                                    Predicate<PrivateMessage> condition = msg -> msg.getPath().equals(snapMsg.get("Path"));
                                    privateMessages.removeIf(condition);
                                    privateMessages.add(PrivateMessage.fromSnapshot(snapMsg));
                                    PrivateMessagesList.postValue(privateMessages);
                                }
                            }
                        }
                    });
            firestore.collection(
                    "PrivateMessages").document(receiver).
                    collection("Conversations").document(auth.getCurrentUser().getEmail()).collection("Messages").
                    addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @SuppressLint("NewApi")
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (!value.isEmpty()) {
                                for (QueryDocumentSnapshot snapMsg : value) {
                                    Predicate<PrivateMessage> condition = msg -> msg.getPath().equals(snapMsg.get("Path"));
                                    privateMessages.removeIf(condition);
                                    privateMessages.add(PrivateMessage.fromSnapshot(snapMsg));
                                    PrivateMessagesList.postValue(privateMessages);
                                }
                            }
                        }
                    });
            }
    }

    public void setMessageRead(String Convo)
    {
        if(!Convo.isEmpty())
        {
            firestore.collection("PrivateMessages").
                    document(auth.getCurrentUser().getEmail()).
                    collection("Conversations").document(Convo)
                    .collection("Messages").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    List<DocumentSnapshot> docs =  task.getResult().getDocuments();
                    for(DocumentSnapshot doc : docs)
                    {
                        doc.getReference().update("Read", true);
                    }
                }
            });
        }
    }

    public void setItemTitleSold(String path)
    {
        DocumentReference docRef = firestore.collection("SalesItems").document(path);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot docs =  task.getResult();
                docs.getReference().update("title", "Sold");
                Log.d("Itemsold", "Item title set to sold in firebase");
            }
        });
    }

    public void createSale(SalesItem item)
{
        GeoPoint geo = GeoCreater(item.getLocation());
        Map<String, Object> map  = new HashMap<>();
        CollectionReference CollRef = firestore.collection("SalesItems");
        String UniqueID = CollRef.document().getId();
        map.put("description", item.getDescription());
        map.put("image", item.getImage());
        map.put("location", geo);
        map.put("price", item.getPrice());
        map.put("title", item.getTitle());
        map.put("user", item.getUser());
        map.put("documentPath", UniqueID);
        CollRef.document(UniqueID).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("Testing", "Completed");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @SuppressLint("ShowToast")
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void sendAuthenticationRequest(String url) throws JSONException {
        if (queue == null) {
            queue = Volley.newRequestQueue(con);
        }
        //encode client id + secret
        String authString = (Constants.CLIENT_ID + ":" + "hEt5IUrYrVY8pKnyp2SAOvWAqqpIzC3qqAAz9tOA3JE");
        String encodedAuth = Base64.getEncoder().encodeToString(authString.getBytes());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Gson gson = new GsonBuilder().create();
                    MPaccessToken = gson.fromJson(response, AccessToken.class);
                    bearerToken = "Bearer " + MPaccessToken.getAccess_token();
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

    public void getCheckedInUsers(String price, String title) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    while (bearerToken.isEmpty()) {
                    }
                    okhttp3.Request request = new okhttp3.Request.Builder()
                            .url("https://api.sandbox.mobilepay.dk/pos/v10/pointofsales/5e6bbcc6-154c-44bb-9a82-45acc1aaea7b/checkin")
                            .method("GET", null)
                            .addHeader("Authorization", "Bearer " + MPaccessToken.getAccess_token())
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
                            sendInitiatePaymentRequest(Constants.NEW_PAYMENT_URL, price, title);
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
    public void sendInitiatePaymentRequest(String url, String price, String title) throws JSONException {
        float priceonly = Float.parseFloat(price.split(" ")[0].trim());
        executor.execute(new Runnable() {
            @Override
            public void run() {
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, String.format("{\r\n    \"posId\":\"5e6bbcc6-154c-44bb-9a82-45acc1aaea7b\",\r\n" +
                        "    \"orderId\":\"Order - 1\",\r\n    \"amount\": %s,\r\n    \"currencyCode\":\"DKK\",\r\n    \"merchantPaymentLabel\":" +
                        " \"%s\",\r\n    \"plannedCaptureDelay\":\"None\"\r\n}", priceonly, title));
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(url) //https://api.sandbox.mobilepay.dk/pos/v10/paymentsq
                        .method("POST", body)
                        .addHeader("Accept", "application/json")
                        .addHeader("content-type", "application/json")
                        .addHeader("x-ibm-client-id", Constants.CLIENT_ID)
                        .addHeader("X-Mobilepay-Client-System-Version", "2.1.1")
                        .addHeader("X-Mobilepay-Idempotency-Key", java.util.UUID.randomUUID().toString())
                        .addHeader("Authorization", "Bearer " + MPaccessToken.getAccess_token())
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

    public void CapturePaymentRequest(String price) throws JSONException {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                MediaType mediaType = MediaType.parse("application/*+json");
                double priceonly = Double.parseDouble(price.split(" ")[0]);
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
}