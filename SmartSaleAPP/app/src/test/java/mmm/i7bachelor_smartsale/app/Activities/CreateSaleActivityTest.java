package mmm.i7bachelor_smartsale.app.Activities;

import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;

import org.junit.jupiter.api.Test;

import java.util.function.BooleanSupplier;

import mmm.i7bachelor_smartsale.app.R;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CreateSaleActivityTest {

    //https://developer.android.com/training/testing/ui-testing/espresso-testing#java
    @Test
    void onCreate() {
        FirebaseApp.getInstance();
        //FirebaseApp.initializeApp();
        FirebaseStorage fb = FirebaseStorage.getInstance();
        assertTrue((BooleanSupplier) fb);
    }

    @Test
    public void changeText_sameActivity() {
        // Type text and then press the button.
    }
}