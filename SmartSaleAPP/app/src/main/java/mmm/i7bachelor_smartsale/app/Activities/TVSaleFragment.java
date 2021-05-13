package mmm.i7bachelor_smartsale.app.Activities;

import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import mmm.i7bachelor_smartsale.app.R;

public class TVSaleFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    private enum StandEnum {
        Stand,
        Ny,
        God,
        Brugt
    }
    //Created this way to be able to hold special characters
    private enum MærkeEnum{
        Mærke("Mærke"),
        LG("LG"),
        Samsung("Samsung"),
        Panasonic("Panasonic"),
        Philips("Philips"),
        Sony("Sony"),
        BogO("B&O");

        private String value;
        MærkeEnum(String value)
        {
            this.value = value;
        }
        public String toString()
        {
            return this.value;
        }
    }

    TextView textviewpris;
    TextView textview_pris;
    Button btn_predict;
    private String mærke;
    private String tommer;
    private String stand;
    float[][] output;


    public TVSaleFragment() {
        // Required empty public constructor
    }

    //Called before OnCreate for fragments
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_t_v_sale, container, false);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        setupUI();
    }

    /** Memory-map the model file in Assets. */
    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor = getActivity().getAssets().openFd("model.tflite");
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
        return inputVal;
    }

    private void setupUI() {
        textviewpris = getView().findViewById(R.id.textView_testml);
        textview_pris = getView().findViewById(R.id.textView_pris);
        btn_predict = getView().findViewById(R.id.btn_predict);
        btn_predict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try (
                        Interpreter interpreter = new Interpreter(loadModelFile())) {
                    float[] input = doInference(String.valueOf(MærkeEnum.valueOf(mærke).ordinal())
                            ,String.valueOf(StandEnum.valueOf(stand).ordinal()), tommer);
                    interpreter.run(input, output);
                }catch (Exception ex) {
                    ex.printStackTrace();
                }
                textview_pris.setText("" + output[0][0]);
                ((FragmentHandler) getActivity()).setPredictedPrice(output[0][0]);
            }
        });

        //get the spinner from the xml.
        Spinner maerkeDropdown = getView().findViewById(R.id.maerkeSpinner);
        Spinner tommerDropdown = getView().findViewById(R.id.tommerSpinner);
        Spinner standDropdown = getView().findViewById(R.id.standSpinner);
        //create a list of items for the spinner.
        String[] maerker = new String[]{"Mærke", "B&O", "Samsung", "Panasonic", "Philips", "Sony","LG",};
        String[] tommer = new String[]{"Tommer", "42", "46", "50", "55", "60","32"};
        String[] stand = new String[]{"Stand", "God", "Brugt", "Ny"};

        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> maerkeadapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, maerker);
        ArrayAdapter<String> tommeradapter = new ArrayAdapter< >(getContext(), android.R.layout.simple_spinner_dropdown_item, tommer);
        ArrayAdapter<String> standadapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, stand);

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
            if(position == 0)
                mærke = "";
            else
            {
                mærke = parent.getItemAtPosition(position).toString();
                if(mærke.equals("B&O"))
                    mærke = "BogO";
            }
        }
        else if(parent.getId() == R.id.tommerSpinner)
        {
            if(position == 0)
                tommer = "";
            else
                tommer = parent.getItemAtPosition(position).toString();
        }
        else if(parent.getId() == R.id.standSpinner)
        {
            if(position == 0)
                stand = "";
            else
               stand = parent.getItemAtPosition(position).toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}