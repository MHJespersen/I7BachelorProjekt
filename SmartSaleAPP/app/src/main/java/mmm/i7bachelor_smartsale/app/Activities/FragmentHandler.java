package mmm.i7bachelor_smartsale.app.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import mmm.i7bachelor_smartsale.app.R;

//https://stackoverflow.com/questions/5159982/how-do-i-add-a-fragment-to-an-activity-with-a-programmatically-created-content-v
public class FragmentHandler extends MainActivity {

    Switch toggleSaleType;
    TVSaleFragment TVFragment = new TVSaleFragment();
    CreateSaleActivity GeneralFragment = new CreateSaleActivity();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_handler);
        toggleSaleType = findViewById(R.id.typeSwitch);
        toggleSaleType.setOnCheckedChangeListener(this::onCheckedChanged);
        setGeneralFragment();

    }
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked)
            setTVFragment();
         else
            setGeneralFragment();
    }

    private void setTVFragment()
    {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.FLFragment, TVFragment)
                .commit();
    }
    private void setGeneralFragment()
    {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.FLFragment, GeneralFragment)
                .commit();
    }
}