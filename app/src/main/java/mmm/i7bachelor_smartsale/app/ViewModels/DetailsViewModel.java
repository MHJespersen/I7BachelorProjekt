package mmm.i7bachelor_smartsale.app.ViewModels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import mmm.i7bachelor_smartsale.app.Models.Repository;
import mmm.i7bachelor_smartsale.app.Models.SalesItem;

public class DetailsViewModel extends ViewModel {

    private Repository repo;
    public DetailsViewModel(Context context) {
        repo = Repository.getInstance(context);
    }

    public LiveData<SalesItem> returnSelected()
    {
        return repo.getSelectedItem();
    }

}