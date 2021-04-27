package mmm.i7bachelor_smartsale.app;

import android.content.Context;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Test;
import org.junit.runner.RunWith;

import mmm.i7bachelor_smartsale.app.Models.Repository;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    private static final String FAKE_STRING = "HELLO_WORLD";
    private Context context;

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("mmm.i7bachelor_smartsale.app", appContext.getPackageName());
    }

    // Unit testing firebase notes:
    //https://firebase.google.com/docs/functions/unit-testing

    @Test
    public void emailValidator_CorrectEmailSimple_ReturnsTrue() {
        //assertThat(TestMLActivity.getprediction());
        //assertThat(EmailValidator.isValidEmail("name@email.com")).isTrue();
    }

    public static class RepositoryTest {

        private Context context;

        private FirebaseFirestore firestore;
        Repository repository;

        //https://stackoverflow.com/questions/47926382/how-to-configure-shorten-command-line-method-for-whole-project-in-intellij
        //https://developer.android.com/training/testing/unit-testing/local-unit-tests

        @Test
        public void setItemTitleSold() {
            repository = Repository.getInstance(context);
            String d = repository.getString();
            assertEquals(d, "test", "test");
        }
    }
}