package mmm.i7bachelor_smartsale.app;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
// Unit testing firebase notes:
//https://firebase.google.com/docs/functions/unit-testing
public class InstrumentedTest {

    private static final String document_path_sale = "64QKH1wmXIVSSqGvrtIO";
    private static final String document_path_message = "PBDuLvcz7rNPyeBp9WtP";
    private static final String logged_in_as = "mathiasholsko@hotmail.com";
    private static final String message_receiver = "testuser@hotmail.com";

    public InstrumentedTest() {
    }

    /**
     * The context is used to instantiate Firestore.
     * We test the package name in the context to ensure our tests instantiate Firestore like our application does
     */
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

        /**
         * testConnection clears the local firestore cache, to ensure that we get elements over the network
         * We get a specific element from firestore that we know exsits and tests if the result is null or not
         * We do not care what is in the result in this test, only that we received something.
         * We Use onSuccess instead of OnComplete to ensure we hit onFailure, if there is no internet
         */
        @Test
        public void testConnection() throws InterruptedException {
            final CountDownLatch signal = new CountDownLatch(1);
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            //Clear local cache of firebase elements.
            firestore.clearPersistence();
            firestore.collection("SalesItems").document(document_path_sale).get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
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

        /**
         *  Test field types in sales items fetches one of the documents from Firestore.
         *  We then assert on each of the fields, to test if each fields have the expected values.
         *  It's important that we're aware of the class types as they're casted differently throughout the app
         *   - especially when casting between a number such as the price to a textview in the app which needs a string.
         *  If this test fails, the application would crash when the markets place is opened in the app.
         *  This also ensures that the tested fields are present in the fetched document.
         */
        @Test
        public void test_field_types_in_sales_items() throws InterruptedException {
            final CountDownLatch signal = new CountDownLatch(1);
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            database.collection("SalesItems").document(document_path_sale).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            Assert.assertSame(task.getResult().get("title").getClass(), String.class);
                            Assert.assertSame(task.getResult().get("description").getClass(), String.class);
                            Assert.assertSame(task.getResult().get("documentPath").getClass(), String.class);
                            Assert.assertSame(task.getResult().get("image").getClass(), String.class);
                            Assert.assertSame(task.getResult().get("location").getClass(), GeoPoint.class);
                            Assert.assertSame(task.getResult().get("price").getClass(), Double.class);
                            Assert.assertSame(task.getResult().get("user").getClass(), String.class);
                            signal.countDown();
                        }
                    });
            signal.await();
        }

        /**
         *  We fetch the first element from Firestore Salesitems, which is the hardcoded document path
         *  in onComplete, we should get a DocumentSnapShot, where we can get the result and a specific field
         *  the same way it's done in the actual repository.
         *  We test that the approach used in the repo, will get us the actual value of a given field.
         */
        @Test
        public void test_hardcoded_path_title_is_unchanged() throws InterruptedException {
            final CountDownLatch signal = new CountDownLatch(1);
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            database.collection("SalesItems").document(document_path_sale).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    Assert.assertEquals("Sofa", task.getResult().get("title"));
                    signal.countDown();
                }
            });
            signal.await();
        }

        /**
         * Same approach as the test for field types in Sales Items, but for a different entity
         */
        @Test
        public void test_field_types_in_private_messages() throws InterruptedException {
            final CountDownLatch signal = new CountDownLatch(1);
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            database.collection("PrivateMessages").
                    document(logged_in_as).collection("Conversations")
                    .document(message_receiver).collection("Messages")
                    .document(document_path_message).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            Assert.assertSame(task.getResult().get("MessageBody").getClass(), String.class);
                            Assert.assertSame(task.getResult().get("MessageDate").getClass(), String.class);
                            Assert.assertSame(task.getResult().get("Path").getClass(), String.class);
                            Assert.assertSame(task.getResult().get("Read").getClass(), Boolean.class);
                            Assert.assertSame(task.getResult().get("Receiver").getClass(), String.class);
                            Assert.assertSame(task.getResult().get("Sender").getClass(), String.class);
                            signal.countDown();
                        }
                    });
            signal.await();
        }
    }
}