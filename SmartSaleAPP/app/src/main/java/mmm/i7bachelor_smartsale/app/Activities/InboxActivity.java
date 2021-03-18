package mmm.i7bachelor_smartsale.app.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import mmm.i7bachelor_smartsale.app.Adapter.InboxAdapter;
import mmm.i7bachelor_smartsale.app.Models.PrivateMessage;
import mmm.i7bachelor_smartsale.app.R;
import mmm.i7bachelor_smartsale.app.ViewModels.InboxViewModel;
import mmm.i7bachelor_smartsale.app.ViewModels.InboxViewModelFactory;

public class InboxActivity extends MainActivity implements InboxAdapter.IMessageClickedListener{

    InboxActivity context;
    private InboxViewModel viewModel;
    private RecyclerView recyclerView;
    private InboxAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        context = this;

        recyclerView = findViewById(R.id.inboxMessages);

        viewModel = new ViewModelProvider(this, new InboxViewModelFactory(this.getApplicationContext()))
                .get(InboxViewModel.class);
        viewModel.getMessages().observe(this, updateObserver);
    }

    Observer<List<PrivateMessage>> updateObserver = new Observer<List<PrivateMessage>>() {
        @Override
        public void onChanged(List<PrivateMessage> UpdatedItems) {
            adapter = new InboxAdapter(context);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(adapter);
            adapter.updateMessageList(UpdatedItems);
        }
    };

    @Override
    public void onMessageClicked(int index) {

        viewModel.SetSelectedMessage(index);
        viewModel.setRead(index);
        Intent ViewMessage = new Intent(this, ViewConversationActivity.class);
        startActivity(ViewMessage);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
