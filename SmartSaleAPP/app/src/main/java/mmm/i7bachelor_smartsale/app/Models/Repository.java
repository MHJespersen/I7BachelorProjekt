package mmm.i7bachelor_smartsale.app.Models;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.firestore.model.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;

public class Repository {

    private static Repository INSTANCE = null;
    private final FirebaseFirestore firestore;
    FirebaseAuth auth;
    private MutableLiveData<SalesItem> SelectedItemLive;
    private MutableLiveData<PrivateMessage> SelectedMessageLive;
    private MutableLiveData<List<PrivateMessage>> PrivateMessagesList;
    private MutableLiveData<List<SalesItem>> MarketsList;
    private MutableLiveData<List<Pair<String, Integer>>> ConvoAndUnread;

    private ExecutorService executor;
    private static Context con;

    public static Repository getInstance(Context context) {
        if (INSTANCE == null) {
            con = context;
            INSTANCE = new Repository();
            Log.d("Repo", "Created Instance: ");
        }
        Log.d("Repo", "Repo Already instantiated: ");
        return(INSTANCE);
    }

    private Repository()
    {
        ConvoAndUnread = new MutableLiveData<>();
        SelectedMessageLive = new MutableLiveData<>();
        PrivateMessagesList = new MutableLiveData<>();
        SelectedItemLive = new MutableLiveData<>();
        MarketsList = new MutableLiveData<>();
        firestore = FirebaseFirestore.getInstance();
        executor = Executors.newSingleThreadExecutor();
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

    public MutableLiveData<PrivateMessage> getSelectedMessage() {
        return SelectedMessageLive;
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
        executor.execute(new Runnable() {
            @Override
            public void run() {
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
        });
    }

    //Adding data
    //https://firebase.google.com/docs/firestore/manage-data/add-data
    //https://stackoverflow.com/questions/51234670/firestore-oncompletelistener
    public void sendMessage(PrivateMessage privateMessage)
    {
        executor.execute(new Runnable() {
            @Override
            public void run() {
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
                        mmap.put("Receiver", privateMessage.getReceiver());
                        mmap.put("Sender", privateMessage.getSender());
                        mmap.put("MessageDate", privateMessage.getMessageDate());
                        mmap.put("MessageBody", privateMessage.getMessageBody());
                        mmap.put("Read", false);
                        mmap.put("Regarding", "N/A");
                        mmap.put("Path", UniqueID);
                        DocRef.collection("Messages").document(UniqueID).set(mmap)
                                .addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        Log.d("PrivateMessages", "Completed");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("PrivateMessages", "Failed");
                                    }
                                });
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
            executor.execute(new Runnable() {
                @Override
                public void run() {
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
            });
        }

    }

    public void createSale(SalesItem item){
        executor.execute(new Runnable() {
            @Override
            public void run() {
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
                                Toast.makeText(con,"Creating sale Failed", Toast.LENGTH_SHORT);
                            }
                        });
            }
        });
    }
}