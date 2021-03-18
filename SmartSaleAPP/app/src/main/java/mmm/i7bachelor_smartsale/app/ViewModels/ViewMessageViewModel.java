package mmm.i7bachelor_smartsale.app.ViewModels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import mmm.i7bachelor_smartsale.app.Models.PrivateMessage;
import mmm.i7bachelor_smartsale.app.Models.Repository;

public class ViewMessageViewModel extends ViewModel {

    private Repository repo;
    public ViewMessageViewModel(Context context) {
        repo = Repository.getInstance(context);
    }
    public LiveData<List<PrivateMessage>> getMessages()
    {
        return repo.getPrivateMessages();
    }


    public void reply(PrivateMessage privateMessage) {
        repo.sendMessage(privateMessage);
    }
}
