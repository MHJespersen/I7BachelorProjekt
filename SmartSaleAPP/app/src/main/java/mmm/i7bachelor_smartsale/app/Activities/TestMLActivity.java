package mmm.i7bachelor_smartsale.app.Activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import mmm.i7bachelor_smartsale.app.R;

//inspiration til image labeling: https://www.youtube.com/watch?v=lL9t9-tiMlE&ab_channel=EDMTDev
public class TestMLActivity extends MainActivity {

    EditText edit_overskrift, edit_model, edit_mærke, edit_stand, edit_type, edit_tommer, edit_pris;
    TextView textviewpris;
    Button btn_publish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testml);

        setupUI();
    }

    private void setupUI() {
        edit_model = findViewById(R.id.edit_model);
        edit_mærke = findViewById(R.id.edit_mærke);
        edit_stand = findViewById(R.id.edit_stand);
        edit_type = findViewById(R.id.edit_type);
        edit_pris = findViewById(R.id.edit_pris);
        textviewpris = findViewById(R.id.textView_testml);
        edit_tommer = findViewById(R.id.edit_tommer);
        edit_overskrift = findViewById(R.id.edit_overskrift);
        //textviewpris.setText(String.valueOf(prediction));

        btn_publish = findViewById(R.id.testml_publish);
        btn_publish.setOnClickListener(view -> getprediction());
    }

    public void getprediction(){
        String overskrift = edit_overskrift.getText().toString();
        String mærke = edit_mærke.getText().toString();
        String type = edit_type.getText().toString();
        String model = edit_model.getText().toString();
        String tommer = edit_tommer.getText().toString();
        String stand = edit_stand.getText().toString();
        String pris = edit_pris.getText().toString();
    }
}
