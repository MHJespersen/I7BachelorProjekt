package mmm.i7bachelor_smartsale.app.Activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.modeldownloader.CustomModel;
import com.google.firebase.ml.modeldownloader.CustomModelDownloadConditions;
import com.google.firebase.ml.modeldownloader.DownloadType;
import com.google.firebase.ml.modeldownloader.FirebaseModelDownloader;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import mmm.i7bachelor_smartsale.app.R;

//inspiration til image labeling: https://www.youtube.com/watch?v=lL9t9-tiMlE&ab_channel=EDMTDev
public class TestMLActivity extends MainActivity {

    EditText edit_overskrift, edit_model, edit_mærke, edit_stand, edit_type, edit_tommer, edit_pris;
    TextView textviewpris;
    Button btn_publish;
    private Interpreter interpreter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testml);

        setupUI();


    }

    private

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

        // https://firebase.google.com/docs/ml/android/use-custom-models
        CustomModelDownloadConditions conditions = new CustomModelDownloadConditions.Builder()
                .requireWifi()  // Also possible: .requireCharging() and .requireDeviceIdl"e()
                .build();
        FirebaseModelDownloader.getInstance()
                .getModel("ml_test", DownloadType.LOCAL_MODEL_UPDATE_IN_BACKGROUND, conditions)
                .addOnSuccessListener(new OnSuccessListener<CustomModel>() {
                    @Override
                    public void onSuccess(CustomModel model) {
                        // Download complete. Depending on your app, you could enable the ML
                        // feature, or switch from the local model to the remote model, etc.

                        // The CustomModel object contains the local path of the model file,
                        // which you can use to instantiate a TensorFlow Lite interpreter.
                        File modelFile = model.getFile();
                        if (modelFile != null) {
                            interpreter = new Interpreter(modelFile);
                        }
                    }
                });
    }

    public void getprediction(){
        String overskrift = edit_overskrift.getText().toString();
        String mærke = edit_mærke.getText().toString();
        String type = edit_type.getText().toString();
        String model = edit_model.getText().toString();
        String tommer = edit_tommer.getText().toString();
        String stand = edit_stand.getText().toString();
        String pris = edit_pris.getText().toString();


        //https://www.tensorflow.org/lite/guide/inference
        // https://www.tensorflow.org/lite/api_docs/java/org/tensorflow/lite/Interpreter
        ByteBuffer input = ByteBuffer.allocateDirect(224 * 224 * 3 * 4).order(ByteOrder.nativeOrder());

        int bufferSize = 1000 * java.lang.Float.SIZE / java.lang.Byte.SIZE;
        ByteBuffer modelOutput = ByteBuffer.allocateDirect(bufferSize).order(ByteOrder.nativeOrder());
        interpreter.run(input, modelOutput);

        modelOutput.rewind();
        FloatBuffer probabilities = modelOutput.asFloatBuffer();
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(getAssets().open("custom_labels.txt")));
            for (int i = 0; i < probabilities.capacity(); i++) {
                String label = reader.readLine();
                float probability = probabilities.get(i);
                Log.i("Mltest", String.format("%s: %1.4f", label, probability));
            }
        } catch (IOException e) {
            // File not found?
        }
        interpreter.close();

        try {
            Map<String, Object> inputs = new HashMap<>();
            inputs.put("overskrift", overskrift);
            inputs.put("mærke", mærke);
            inputs.put("type", type);
            inputs.put("model", model);
            inputs.put("tommer", tommer);
            inputs.put("stand", stand);
            inputs.put("pris", pris);
            Map<String, Object> outputs = new HashMap<>();
            //outputs.put("output_1", output1);
            //interpreter.runForMultipleInputsOutputs(inputs, map_of_indices_to_outputs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
