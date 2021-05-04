package mmm.i7bachelor_smartsale.app.ViewModels;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import mmm.i7bachelor_smartsale.app.Models.Repository;
import mmm.i7bachelor_smartsale.app.Models.SalesItem;

public class MarketsViewModel extends ViewModel {

    private MutableLiveData<List<SalesItem>> salesitemLiveData;
    private final Repository repository;

    public MarketsViewModel(Context context)
    {
        salesitemLiveData = new MutableLiveData<>();
        repository = Repository.getInstance(context);
    }

    //Lyt efter om der tilføjes noget til listen, f.eks fra CreateSaleViewet
    //Hvis noget tilføjes (fra en anden bruger) skal dette opdateres i vores liste, hvilket gøres her.
    public LiveData<List<SalesItem>> getItems()
    {
        salesitemLiveData = repository.getItems();
        return salesitemLiveData;
    }

    public void SetSelected(int index) {
        repository.setSelectedItem(salesitemLiveData.getValue().get(index).getPath());
    }
}
