package mmm.i7bachelor_smartsale.app.ViewModels;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
//Inspired from: https://stackoverflow.com/questions/46283981/android-viewmodel-additional-arguments
public class MainActivityViewModelFactory implements ViewModelProvider.Factory {
    private Context con;

    public MainActivityViewModelFactory(Context context) {
        con = context;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MainActivityViewModel(con);
    }
}
