package mmm.i7bachelor_smartsale.app.Models;

import android.content.Context;

import com.google.firebase.FirebaseApp;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RepositoryTest {

    //https://stackoverflow.com/questions/47926382/how-to-configure-shorten-command-line-method-for-whole-project-in-intellij
    //https://developer.android.com/training/testing/unit-testing/local-unit-tests
    @org.junit.jupiter.api.Test
    void getInstance() {
        Context context = null;
        FirebaseApp.initializeApp(context.getApplicationContext()).getName();
        Repository repo = new Repository();
        assertEquals(repo, repo);
    }

    @org.junit.jupiter.api.Test
    void setItemTitleSold() {

    }

    @org.junit.jupiter.api.Test
    void createSale() {

    }
}