package mmm.i7bachelor_smartsale.app.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import mmm.i7bachelor_smartsale.app.Models.PrivateMessage;
import mmm.i7bachelor_smartsale.app.R;
import mmm.i7bachelor_smartsale.app.Utilities.Constants;
import mmm.i7bachelor_smartsale.app.ViewModels.SendMessageViewModel;
import mmm.i7bachelor_smartsale.app.ViewModels.SendMessageViewModelFactory;

public class SendMessageActivity extends MainActivity {
    private SendMessageViewModel viewModel;
    FirebaseAuth auth;

    // widgets
    private TextView textRecipient, textItem;
    private EditText inputMessage;
    private Button btnCancel, btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        viewModel = new ViewModelProvider(this, new SendMessageViewModelFactory(this.getApplicationContext()))
                .get(SendMessageViewModel.class);

        int itemId = getIntent().getIntExtra(Constants.EXTRA_ITEM_ID, 42);

        setupUI();
    }

    private void setupUI() {
        textRecipient = findViewById(R.id.sendMessageTextRecipient);
        Intent intent = getIntent();
        String user = intent.getStringExtra(Constants.DETAILS_USER);
        textRecipient.setText(String.valueOf(user));

        textItem = findViewById(R.id.sendMessageTextItem);
        String title = intent.getStringExtra(Constants.DETAILS_TITLE);
        textItem.setText(title);

        inputMessage = findViewById(R.id.sendMessageInputMessage);
        inputMessage.requestFocus();

        btnCancel = findViewById(R.id.sendMessageBtnCancel);
        btnCancel.setOnClickListener(view -> finish());

        btnSend = findViewById(R.id.sendMessageBtnSend);
        btnSend.setOnClickListener(view -> sendMessage());
    }

    //guide: https://www.youtube.com/watch?v=f1HKTg2hyf0&ab_channel=KODDev
    private void sendMessage() {
        auth = FirebaseAuth.getInstance();
        //input to message
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss", Locale.getDefault()).format(new Date());
        String message = inputMessage.getText().toString();
        String sender = auth.getCurrentUser().getEmail();

        //With Message Object.
        Intent intent = getIntent();
        String user = intent.getStringExtra(Constants.DETAILS_USER);
        String title = intent.getStringExtra(Constants.DETAILS_TITLE);
        PrivateMessage privateMessage = new PrivateMessage();
        privateMessage.setMessageDate(timeStamp);
        privateMessage.setSender(sender);
        privateMessage.setReceiver(user);
        privateMessage.setMessageBody(message);
        privateMessage.setMessageRead(false);

        // with message object to viewmodel.
        viewModel.sendMessage(privateMessage);
        Toast.makeText(this, getString(R.string.message_sent), Toast.LENGTH_SHORT).show();
        finish();
    }
}
