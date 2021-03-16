package mmm.i7bachelor_smartsale.app.ViewModels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import mmm.i7bachelor_smartsale.app.Models.PrivateMessage;
import mmm.i7bachelor_smartsale.app.Models.Repository;

public class InboxViewModel extends ViewModel {

    private LiveData<List<PrivateMessage>> privateMessagelist;
    private final Repository repository;

    public InboxViewModel(Context context) {
        repository = Repository.getInstance(context);
        privateMessagelist = new MutableLiveData<>();
    }

    public LiveData<List<PrivateMessage>> getMessages()
    {
        UpdateList();
        return privateMessagelist;
    }


    private void UpdateList()
    {
        privateMessagelist = repository.getPrivateMessages();
    }

    public void setRead(int index) {
        repository.setMessageRead(privateMessagelist.getValue().get(index));
    }


    public void SetSelectedMessage(int index) {
        //repository.setSelectedMessage(privateMessagelist.getValue().get(index));
    }
}
