package mmm.i7bachelor_smartsale.app.ViewModels;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import mmm.i7bachelor_smartsale.app.Models.Repository;

//The ViewModel is inspired by https://medium.com/@taman.neupane/basic-example-of-livedata-and-viewmodel-14d5af922d0
// And https://medium.com/@atifmukhtar/recycler-view-with-mvvm-livedata-a1fd062d2280
public class LoginViewModel extends ViewModel {
    private Repository repo;
    public LoginViewModel(Context context) {
        repo = Repository.getInstance(context);
    }

    public void InitMessages() {
       repo.initializePrivateMessages();
    }
}
