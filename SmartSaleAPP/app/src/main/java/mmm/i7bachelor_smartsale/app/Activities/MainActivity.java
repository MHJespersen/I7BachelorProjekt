package mmm.i7bachelor_smartsale.app.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import mmm.i7bachelor_smartsale.app.Models.Repository;
import mmm.i7bachelor_smartsale.app.R;

public class MainActivity extends AppCompatActivity {

    //Inspired from: https://stackoverflow.com/questions/17889240/reuse-the-action-bar-in-all-the-activities-of-app
    FirebaseAuth auth;
    Repository repo;
    static Boolean MessagesOpen = false;
    int LAUNCH_INBOX_ACTIVTY = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        repo = Repository.getInstance(this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.userTxt:
                if(!MessagesOpen)
                {
                    OpenProfile();
                    MessagesOpen = true;
                }
                return true;
            case R.id.messagesTxt:
                if(!MessagesOpen)
                {
                    OpenMessages();
                    MessagesOpen = true;
                }
                return true;
            case R.id.logoutTxt:
                LogOut();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem logoutItem = menu.findItem(R.id.logoutTxt);
        MenuItem messagesItem = menu.findItem(R.id.messagesTxt);
        MenuItem userItem = menu.findItem(R.id.userTxt);

        //If user is logged in, display user specific information.
        if(auth.getCurrentUser() != null)
        {
            logoutItem.setVisible(true);
            messagesItem.setVisible(true);
            userItem.setVisible(true);
            userItem.setTitle( getString(R.string.menu_user) + ": " + auth.getCurrentUser().getEmail());
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private void OpenProfile() {
        Intent intent = new Intent(this, InboxActivity.class);
        startActivity(intent);
    }

    private void OpenMessages() {
        Intent i = new Intent(this, InboxActivity.class);
        startActivityForResult(i, LAUNCH_INBOX_ACTIVTY);
    }

    //When the Inbox is closed, allow it to open again. Added to avoid having multiple Inbox
    //activities on top of each other.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LAUNCH_INBOX_ACTIVTY)
            MessagesOpen = false;
    }

    public void LogOut() {
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, getString(R.string.logged_out), Toast.LENGTH_SHORT).show();
            invalidateOptionsMenu();

            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }
}