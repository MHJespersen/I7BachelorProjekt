package mmm.i7bachelor_smartsale.app.Models;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Repository {


    private static Repository INSTANCE = null;
    private final FirebaseFirestore firestore;
    FirebaseAuth auth;

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
        firestore = FirebaseFirestore.getInstance();
        executor = Executors.newSingleThreadExecutor();
        auth = FirebaseAuth.getInstance();
    }

    /*
    public GeoPoint GeoCreater(Location l){
        GeoPoint geo = new GeoPoint(l.getLatitude(), l.getLongitude());
        return geo;
    }
    */

    //Adding data
    //https://firebase.google.com/docs/firestore/manage-data/add-data
    //https://stackoverflow.com/questions/51234670/firestore-oncompletelistener


    /*
    public void sendMessage(PrivateMessage privateMessage)
    {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Map<String, Object> map = new HashMap<>();
                CollectionReference CollRef = firestore.collection("PrivateMessages").
                        document(privateMessage.getReceiver()).collection("Messages");
                String UniqueID = CollRef.document().getId();
                map.put("Receiver", privateMessage.getReceiver());
                map.put("Sender", privateMessage.getSender());
                map.put("MessageDate", privateMessage.getMessageDate());
                map.put("MessageBody", privateMessage.getMessageBody());
                map.put("Read", false);
                map.put("Regarding", privateMessage.getRegarding());
                map.put("Path", UniqueID);
                firestore.collection("PrivateMessages").document(privateMessage.getReceiver())
                        .collection("Messages").document(UniqueID).set(map)
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

     */

    /*
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
     */


}
