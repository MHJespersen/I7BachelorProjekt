package mmm.i7bachelor_smartsale.app.Activities;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.function.BooleanSupplier;

import mmm.i7bachelor_smartsale.app.Models.Repository;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

class CreateSaleActivityTest {

    @Mock
    CreateSaleActivity createSale;

    @Captor
    private ArgumentCaptor<CreateSaleActivity> repoCaptor;
    @Captor
    private ArgumentCaptor<CreateSaleActivity> firestoreArgumentCaptor;

    public CreateSaleActivityTest() {
    }

    @BeforeAll
    public void init(){
        MockitoAnnotations.initMocks(this);

    }

    @org.junit.Test
    public void setItemTitleSold() {
        ArgumentCaptor<CreateSaleActivity> argument = ArgumentCaptor.forClass(CreateSaleActivity.class);
        //repository.setItemTitleSold("12313");
        verify(createSale).Save();
        //assertEquals("John", argument.getValue());
    }

    //https://developer.android.com/training/testing/ui-testing/espresso-testing#java
    @Test
    void onCreate() {
    }

    @Test
    public void changeText_sameActivity() {
        // Type text and then press the button.
    }
}