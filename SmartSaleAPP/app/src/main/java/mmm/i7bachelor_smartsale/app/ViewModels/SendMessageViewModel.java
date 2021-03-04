package mmm.i7bachelor_smartsale.app.ViewModels;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import mmm.i7bachelor_smartsale.app.Models.PrivateMessage;
import mmm.i7bachelor_smartsale.app.Models.Repository;

public class SendMessageViewModel extends ViewModel {
    private final Repository repository;

    public SendMessageViewModel(Context context) {
        repository = Repository.getInstance(context);
    }

    public void sendMessage(PrivateMessage privateMessage) {
        repository.sendMessage(privateMessage);
    }
}
