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
    private TextView textReply, fromtxt;
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
        adapter = new ConversationAdapter(context);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

        //Hande re-use of viewholders properly so text and img positions don't swap
        recyclerView.getRecycledViewPool().setMaxRecycledViews(1,0);

        viewModel = new ViewModelProvider(this, new ViewMessageViewModelFactory(this.getApplicationContext()))
                .get(ViewMessageViewModel.class);
        viewModel.getMessages().observe(this, updateObserver );
    }
    Observer<List<PrivateMessage>> updateObserver = new Observer<List<PrivateMessage>>() {
        @Override
        public void onChanged(List<PrivateMessage> messages) {
            _privatemessage = messages.get(0);
            fromtxt.setText(_privatemessage.getSender());
            adapter.updateMessageList(messages);
        }};

    private void setupUI() {
        btnReply = findViewById(R.id.viewMessageBtnReply);
        fromtxt = findViewById(R.id.viewMessageLabelFrom);
        textReply = findViewById(R.id.viewMessageReply);
        btnReply.setOnClickListener(view -> reply());
    }

    private void reply() {
        String replyMessage = textReply.getText().toString();
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss", Locale.getDefault()).format(new Date());
        PrivateMessage privateMessage = new PrivateMessage();
        privateMessage.setMessageDate(timeStamp);
        privateMessage.setSender(auth.getCurrentUser().getEmail());
        privateMessage.setReceiver(_privatemessage.getSender());
        privateMessage.setMessageBody(replyMessage);

        viewModel.reply(privateMessage);
        Toast.makeText(this, getString(R.string.reply_sent), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMessageClicked(int index) {

    }
}
