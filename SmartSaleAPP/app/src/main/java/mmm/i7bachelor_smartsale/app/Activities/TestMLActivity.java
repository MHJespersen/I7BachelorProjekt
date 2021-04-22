package mmm.i7bachelor_smartsale.app.Activities;

import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.tensorflow.lite.Interpreter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import mmm.i7bachelor_smartsale.app.R;

//inspiration til image labeling: https://www.youtube.com/watch?v=lL9t9-tiMlE&ab_channel=EDMTDev
public class TestMLActivity extends MainActivity implements AdapterView.OnItemSelectedListener {

    EditText edit_mærke, edit_stand, edit_tommer;
    TextView textviewpris;
    TextView textview_pris;
    Button btn_publish;
    Interpreter tflite;
    MappedByteBuffer tflite_model;

    float[] model_output;
    float[][] output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testml);

        setupUI();

        btn_publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try (Interpreter interpreter = new Interpreter(loadModelFile2())) {
                    float[] input = doInference(edit_mærke.getText().toString(),edit_stand.getText().toString(),edit_tommer.getText().toString());
                    interpreter.run(input, output);
                }catch (Exception ex) {
                    ex.printStackTrace();
                }
                //textview_pris.setText(model_output);
                //float prediction = doInference(edit_mærke.getText().toString(), edit_tommer.getText().toString(), edit_stand.getText().toString());
                //System.out.println(prediction);
                //textview_pris.setText(Float.toString(prediction));
            }
        });
    }

    /** Memory-map the model file in Assets. */
    private MappedByteBuffer loadModelFile2()
            throws IOException {
        AssetFileDescriptor fileDescriptor = getAssets().openFd("model.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private float[] doInference(String inputString0, String inputString1, String inputString2) {
        float[] inputVal = new float[3];
        inputVal[0]=Float.parseFloat(inputString0);
        inputVal[1]=Float.parseFloat(inputString1);
        inputVal[2]=Float.parseFloat(inputString2);
        output = new float[1][1];
        //tflite.run(inputVal, output);
        return inputVal;
    }

    private void setupUI() {
        edit_mærke = findViewById(R.id.edit_mærke);
        edit_stand = findViewById(R.id.edit_stand);
        textviewpris = findViewById(R.id.textView_testml);
        edit_tommer = findViewById(R.id.edit_tommer);
        textview_pris = findViewById(R.id.textView_pris);
        //textviewpris.setText(String.valueOf(prediction));

        btn_publish = findViewById(R.id.testml_publish);

        //get the spinner from the xml.
        Spinner maerkeDropdown = findViewById(R.id.maerkeSpinner);
        Spinner tommerDropdown = findViewById(R.id.tommerSpinner);
        Spinner standDropdown = findViewById(R.id.standSpinner);
        //create a list of items for the spinner.
        String[] maerker = new String[]{"", "B&O", "Samsung", "Panasonic", "Philips", "Sony","LG",};
        String[] tommer = new String[]{"", "42", "46", "50", "55", "60","32"};
        String[] stand = new String[]{"", "God stand", "Brugt", "Ny"};

        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> maerkeadapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, maerker);
        ArrayAdapter<String> tommeradapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, tommer);
        ArrayAdapter<String> standadapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, stand);

        //set the spinners adapter to the previously created one.
        maerkeDropdown.setAdapter(maerkeadapter);
        tommerDropdown.setAdapter(tommeradapter);
        standDropdown.setAdapter(standadapter);

        //Set click listeners
        maerkeDropdown.setOnItemSelectedListener(this);
        tommerDropdown.setOnItemSelectedListener(this);
        standDropdown.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        if(parent.getId() == R.id.maerkeSpinner)
        {
            switch (position) {
                case 0:
                    edit_mærke.setText("");
                    break;
                case 1:
                    edit_mærke.setText("6");
                    break;
                case 2:
                    edit_mærke.setText("2");
                    break;
                case 3:
                    edit_mærke.setText("3");
                    break;
                case 4:
                    edit_mærke.setText("4");
                    break;
                case 5:
                    edit_mærke.setText("5");
                    break;
                case 6:
                    edit_mærke.setText("1");
                    break;
            }
        }
        else if(parent.getId() == R.id.tommerSpinner)
        {
            if(position == 0){
                edit_tommer.setText("");
            }
            edit_tommer.setText(parent.getItemAtPosition(position).toString());
        }
        else if(parent.getId() == R.id.standSpinner)
        {
            switch (position) {
                case 0:
                    edit_stand.setText("");
                    break;
                case 1:
                    edit_stand.setText("2");
                    break;
                case 2:
                    edit_stand.setText("3");
                    break;
                case 3:
                    edit_stand.setText("1");
                    break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //Auto Generated stub
    }
}