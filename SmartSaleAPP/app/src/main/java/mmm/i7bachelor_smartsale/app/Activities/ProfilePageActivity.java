package mmm.i7bachelor_smartsale.app.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import mmm.i7bachelor_smartsale.app.R;

public class ProfilePageActivity extends MainActivity {

    EditText etEmail, etName, etPhone;
    ImageView userimage;
    Button btnUpdateImage, btnUpdateName, btnUpdateEmail, btnUpdatePhone, btnUpdatePwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilepage);

        etEmail = findViewById(R.id.etEmail);
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        userimage = findViewById(R.id.ivUserImage);
        btnUpdateImage = findViewById(R.id.btnUpdateImage);
        btnUpdateName = findViewById(R.id.btnUpdateName);
        btnUpdateEmail = findViewById(R.id.btnUpdateEmail);
        btnUpdatePhone = findViewById(R.id.btnUpdatePhone);
        btnUpdatePwd = findViewById(R.id.btnUpdatePwd);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                // if the user is logged in, display their info on the screen
            etEmail.setText(user.getEmail());
            etName.setText(user.getDisplayName());
            etPhone.setText(user.getPhoneNumber());
            userimage.setImageURI(user.getPhotoUrl());


            // https://heartbeat.fritz.ai/implementing-social-login-and-user-profile-management-in-android-with-firebase-de1cbd982d44
        //https://firebase.google.com/docs/auth/android/manage-users
        btnUpdateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnUpdateName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnUpdateEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnUpdatePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnUpdatePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
