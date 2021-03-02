package mmm.i7bachelor_smartsale.app.ViewModels;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class MapsViewModelFactory implements ViewModelProvider.Factory {
    private Context con;

    public MapsViewModelFactory(Context context) {
        con = context;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MapsViewModel(con);
    }
}
