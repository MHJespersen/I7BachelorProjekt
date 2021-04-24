package mmm.i7bachelor_smartsale.app.Models;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

public class RepositoryTest {

    @Mock
    private Context context;

    private FirebaseFirestore firestore;
    @Mock
    Repository repository;

    @Captor
    private ArgumentCaptor<Repository> repoCaptor;
    @Captor
    private ArgumentCaptor<FirebaseFirestore> firestoreArgumentCaptor;

    public RepositoryTest(FirebaseFirestore fb) {
    this.firestore = fb;
    }

    @BeforeAll
    public void init(){
        MockitoAnnotations.initMocks(this);
        repository = new Repository();

    }

    //https://stackoverflow.com/questions/47926382/how-to-configure-shorten-command-line-method-for-whole-project-in-intellij
    //https://developer.android.com/training/testing/unit-testing/local-unit-tests

    @Test
    public void setItemTitleSold() {
        ArgumentCaptor<Repository> argument = ArgumentCaptor.forClass(Repository.class);
        //repository.setItemTitleSold("12313");
        verify(repository).setItemTitleSold("123123312");
        //assertEquals("John", argument.getValue());
        //firestoreArgumentCaptor.getValue().collection("SalesItems").document().
    }

    @Test
    public void createSale() {
        Map<String, Object> map  = new HashMap<>();
        CollectionReference CollRef = firestore.collection("SalesItems");
        String UniqueID = CollRef.document().getId();
        map.put("description", "Old sofa");
        map.put("price", "50");
        map.put("title", "TestSofa");
        map.put("user", "TestUser");
        map.put("documentPath", UniqueID);
        CollRef.document(UniqueID).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("Testing", "Completed");
            }
        });
    }
}