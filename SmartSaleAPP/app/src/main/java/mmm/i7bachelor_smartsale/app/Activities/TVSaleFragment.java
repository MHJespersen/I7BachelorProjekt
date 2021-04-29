package mmm.i7bachelor_smartsale.app.Activities;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mmm.i7bachelor_smartsale.app.R;

public class TVSaleFragment extends Fragment {

    public TVSaleFragment() {
        // Required empty public constructor
    }

    //Called before OnCreate for fragments
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_t_v_sale, container, false);
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
}