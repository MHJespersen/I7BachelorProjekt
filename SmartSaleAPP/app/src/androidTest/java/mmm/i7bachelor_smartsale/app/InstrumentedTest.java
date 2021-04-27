package mmm.i7bachelor_smartsale.app;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import static com.google.android.gms.tasks.Tasks.await;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
// Unit testing firebase notes:
//https://firebase.google.com/docs/functions/unit-testing
public class InstrumentedTest {

    private final FirebaseFirestore firestore;
    FirebaseAuth auth;
    private static final String FAKE_STRING = "HELLO_WORLD";
    private Context context;
    static boolean done = false;
    private static String ouch_document_path = "64QKH1wmXIVSSqGvrtIO";

    public InstrumentedTest() {
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    @Test
    public void Test_correct_context_is_used() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("mmm.i7bachelor_smartsale.app", appContext.getPackageName());
    }

    public static class RepositoryTest {
        //https://stackoverflow.com/questions/47926382/how-to-configure-shorten-command-line-method-for-whole-project-in-intellij
        //https://developer.android.com/training/testing/unit-testing/local-unit-tests
        //https://stackoverflow.com/questions/2321829/android-asynctask-testing-with-android-test-framework

        @Test
        public void testConnection() throws InterruptedException {
            final CountDownLatch signal = new CountDownLatch(1);
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            //Clear local cache of firebase elements.
            firestore.clearPersistence();
            firestore.collection("SalesItems").document(ouch_document_path).get()
            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                //Use onSuccess instead of OnComplete to ensure we hit onFailure, if there is no internet
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    assertNotNull(documentSnapshot);
                    Log.d("Test", "In onComplete");
                    signal.countDown();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    fail();
                    Log.d("Test", "In OnFailure");
                    signal.countDown();
                }
            });
            signal.await();
        }

        @Test
        public void test_hardcoded_path_title_is_unchanged() throws InterruptedException {
            final CountDownLatch signal = new CountDownLatch(1);
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            database.collection("SalesItems").document(ouch_document_path).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    Assert.assertEquals("Sofa", task.getResult().get("title"));
                    signal.countDown();
                }
            });
            signal.await();
        }
    }
}