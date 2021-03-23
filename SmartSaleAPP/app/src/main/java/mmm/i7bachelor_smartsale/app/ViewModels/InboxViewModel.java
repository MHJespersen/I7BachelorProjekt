package mmm.i7bachelor_smartsale.app.ViewModels;

import android.content.Context;
import android.util.Pair;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import mmm.i7bachelor_smartsale.app.Models.PrivateMessage;
import mmm.i7bachelor_smartsale.app.Models.Repository;

public class InboxViewModel extends ViewModel {

    private LiveData<List<PrivateMessage>> privateMessagelist;
    private MutableLiveData<List<Pair<String, Integer>>> ConvoAndStatus;
    private final Repository repository;

    public InboxViewModel(Context context) {
        repository = Repository.getInstance(context);
        privateMessagelist = new MutableLiveData<>();
    }

    public MutableLiveData<List<Pair<String, Integer>>> getMessages()
    {
        UpdateList();
        return ConvoAndStatus;
    }


    private void UpdateList()
    {
        ConvoAndStatus = repository.getConvosAndReadStatus();
    }

    public void setRead(int index) {
        repository.setMessageRead(ConvoAndStatus.getValue().get(index).first);
    }


    public void SetSelectedMessage(int index) {
        repository.initializePrivateMessages(ConvoAndStatus.getValue().get(index).first);
    }
}
