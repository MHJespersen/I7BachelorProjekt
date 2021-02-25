package mmm.i7bachelor_smartsale.app.ViewModels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import mmm.i7bachelor_smartsale.app.Models.PrivateMessage;
import mmm.i7bachelor_smartsale.app.Models.Repository;

public class ViewMessageViewModel extends ViewModel {

    private Repository repo;
    public ViewMessageViewModel(Context context) {
        repo = Repository.getInstance(context);
    }
    public LiveData<PrivateMessage> returnSelected()
    {
        return repo.getSelectedMessage();
    }

    public void reply(PrivateMessage privateMessage) {
        repo.sendMessage(privateMessage);
    }
}
