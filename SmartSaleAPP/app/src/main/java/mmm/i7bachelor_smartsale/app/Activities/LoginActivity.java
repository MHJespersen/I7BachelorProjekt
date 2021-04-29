package mmm.i7bachelor_smartsale.app.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.List;

import mmm.i7bachelor_smartsale.app.R;
import mmm.i7bachelor_smartsale.app.Services.ForegroundService;
import mmm.i7bachelor_smartsale.app.Utilities.Constants;
import mmm.i7bachelor_smartsale.app.ViewModels.LoginViewModel;
import mmm.i7bachelor_smartsale.app.ViewModels.LoginViewModelFactory;

public class LoginActivity extends MainActivity {

    private LoginViewModel viewModel;
    FirebaseAuth auth;
    LoginActivity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;
        // Calling / creating ViewModel with the factory pattern is inspired from: https://stackoverflow.com/questions/46283981/android-viewmodel-additional-arguments
        viewModel = new ViewModelProvider(this,
                new LoginViewModelFactory(this.getApplicationContext())).get(LoginViewModel.class);
        auth = FirebaseAuth.getInstance();

        // Start service
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        startService(serviceIntent);
    }


    public void SignIn(View view) {
        if (auth == null) {
            auth = FirebaseAuth.getInstance();
        }
        if (auth.getCurrentUser() != null) {
            Toast.makeText(context, getString(R.string.logged_in_already), Toast.LENGTH_SHORT).show();
        }
        else {
            //Set up suported builders
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.EmailBuilder().build(),
                    new AuthUI.IdpConfig.GoogleBuilder().build(),
                    new AuthUI.IdpConfig.FacebookBuilder().build(),
                    new AuthUI.IdpConfig.GitHubBuilder().build(),
                    new AuthUI.IdpConfig.MicrosoftBuilder().build()

            );
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setTheme(R.style.Theme_AppCompat_DayNight_DarkActionBar)
                            .setLogo(R.mipmap.ic_launcher_foreground)
                            .setAvailableProviders(providers)
                            .setIsSmartLockEnabled(false)
                            .build(), Constants.REQUEST_LOGIN
            );
        }
    }

    //Callback from Login with Firebase
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Constants.REQUEST_LOGIN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(context, getString(R.string.logged_in_as) + " " + auth.getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
                //Invalidate menu bar to load buttons and user Email
                viewModel.InitMessages();
                invalidateOptionsMenu();
            }
        }
    }

    // Back press closes app instead of going back to previous activity
    // https://stackoverflow.com/questions/21253303/exit-android-app-on-back-pressed
    @Override
    public void onBackPressed() {
        finishAffinity();
    }


    public void LogOut(View view) {
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(this, getString(R.string.logged_out), Toast.LENGTH_SHORT).show();
            invalidateOptionsMenu();
        } else {
            Toast.makeText(this, getString(R.string.logged_out_already), Toast.LENGTH_SHORT).show();

        }
    }

    public void OpenMarket(View view) {
            Intent Markets = new Intent(this, MarketsActivity.class);
            startActivity(Markets);
    }

    public void MakeASale(View view) {
        if(auth.getCurrentUser() != null)
        {
            Intent Markets = new Intent(this, CreateSaleActivity.class);
            startActivity(Markets);
        }
        else
        {
            view.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
            Toast.makeText(this, getString(R.string.log_in_first), Toast.LENGTH_SHORT).show();
        }
    }

    public void GotoFragmentView(View view) {
        Intent Fragments = new Intent(this, FragmentHandler.class);
        startActivity(Fragments);
    }
}