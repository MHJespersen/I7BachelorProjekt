package mmm.i7bachelor_smartsale.app.ViewModels;
import android.content.Context;
import androidx.lifecycle.ViewModel;
import com.google.firebase.auth.FirebaseAuth;
import mmm.i7bachelor_smartsale.app.Models.Repository;

public class MainActivityViewModel extends ViewModel {
    private Repository repo;
    MainActivityViewModel(Context con){repo = Repository.getInstance(con);}

    public FirebaseAuth getFirebaseAuth(){
        return repo.getFirebaseAuth();
    };
}
