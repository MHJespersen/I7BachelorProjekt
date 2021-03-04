package mmm.i7bachelor_smartsale.app.ViewModels;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import mmm.i7bachelor_smartsale.app.Models.Repository;

public class ProfilePageViewModel extends ViewModel {
    private final Repository repository;

    public ProfilePageViewModel(Context context) {
        repository = Repository.getInstance(context);
    }
}
