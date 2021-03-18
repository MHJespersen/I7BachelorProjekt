package mmm.i7bachelor_smartsale.app.Activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import mmm.i7bachelor_smartsale.app.Adapter.ConversationAdapter;
import mmm.i7bachelor_smartsale.app.Adapter.InboxAdapter;
import mmm.i7bachelor_smartsale.app.Models.PrivateMessage;
import mmm.i7bachelor_smartsale.app.R;
import mmm.i7bachelor_smartsale.app.ViewModels.InboxViewModel;
import mmm.i7bachelor_smartsale.app.ViewModels.ViewMessageViewModel;
import mmm.i7bachelor_smartsale.app.ViewModels.ViewMessageViewModelFactory;

public class ViewConversationActivity extends MainActivity implements ConversationAdapter.IMessageClickedListener {
    private ViewMessageViewModel viewModel;
    private PrivateMessage _privatemessage;

    // widgets
    private TextView textSender, textRegarding, textMessage, textReply;
    private Button btnReply;
    private RecyclerView recyclerView;
    private ConversationAdapter adapter;
    ViewConversationActivity context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_view_message);
        setupUI();
        recyclerView = findViewById(R.id.RCVMessages);

        viewModel = new ViewModelProvider(this, new ViewMessageViewModelFactory(this.getApplicationContext()))
                .get(ViewMessageViewModel.class);
        viewModel.getMessages().observe(this, updateObserver );
    }
    Observer<List<PrivateMessage>> updateObserver = new Observer<List<PrivateMessage>>() {
        @Override
        public void onChanged(List<PrivateMessage> messages) {
            adapter = new ConversationAdapter(context);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(adapter);
            adapter.updateMessageList(messages);
        }};

    private void setupUI() {
        textSender = findViewById(R.id.viewMessageTextFrom);
        btnReply = findViewById(R.id.viewMessageBtnReply);
        textReply = findViewById(R.id.viewMessageReply);
        btnReply.setOnClickListener(view -> reply());
    }

    private void reply() {
        String replyMessage = textReply.getText().toString();
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss", Locale.getDefault()).format(new Date());

        PrivateMessage privateMessage = new PrivateMessage();
        privateMessage.setMessageDate(timeStamp);
        privateMessage.setSender(_privatemessage.getReceiver());
        privateMessage.setReceiver(_privatemessage.getSender());
        privateMessage.setMessageBody(replyMessage);
        privateMessage.setRegarding(_privatemessage.getRegarding());

        viewModel.reply(privateMessage);
        Toast.makeText(this, getString(R.string.reply_sent), Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onMessageClicked(int index) {

    }
}
