package mmm.i7bachelor_smartsale.app.ViewModels;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import org.json.JSONException;

import mmm.i7bachelor_smartsale.app.Models.Repository;
import mmm.i7bachelor_smartsale.app.Models.SalesItem;
import mmm.i7bachelor_smartsale.app.Utilities.Constants;

public class DetailsViewModel extends ViewModel {

    private Repository repo;
    public DetailsViewModel(Context context) {
        repo = Repository.getInstance(context);
    }

    public LiveData<SalesItem> returnSelected()
    {
        return repo.getSelectedItem();
    }

    public void setItemSold(String path) {
        repo.setItemTitleSold(path);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void sendMPAuth() throws JSONException
    {
        repo.sendAuthenticationRequest(Constants.SANDBOX_URL);
    }
    public void CapturePayment(String price) throws JSONException
    {
        repo.CapturePaymentRequest(price);
    };

    // Check when user is checked in at mobilepay app and initiate payment af that.
    public void listenForUsers(String price, String title){repo.getCheckedInUsers(price, title);}
}