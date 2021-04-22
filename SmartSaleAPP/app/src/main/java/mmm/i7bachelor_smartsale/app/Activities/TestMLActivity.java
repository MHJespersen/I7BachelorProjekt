package mmm.i7bachelor_smartsale.app.Activities;

import android.content.res.AssetFileDescriptor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.tensorflow.lite.Interpreter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import mmm.i7bachelor_smartsale.app.R;

//inspiration til image labeling: https://www.youtube.com/watch?v=lL9t9-tiMlE&ab_channel=EDMTDev
public class TestMLActivity extends MainActivity {

    EditText edit_mærke, edit_stand, edit_tommer;
    TextView textviewpris;
    TextView textview_pris;
    Button btn_publish;
    Interpreter tflite;

    int model_output;

    Uri f_uri = Uri.fromFile(new File("//assets/model.tflite"));

    File f = new File(f_uri.getPath());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testml);

        setupUI();

        try (Interpreter interpreter = new Interpreter(f)) {
            interpreter.run(doInference(edit_mærke.getText().toString(),edit_stand.getText().toString(),edit_tommer.getText().toString()), model_output);
        }

        /*
        try{
            tflite = new Interpreter(f);
        }catch (Exception ex) {
            ex.printStackTrace();
        }

         */

        btn_publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float prediction = doInference(edit_mærke.getText().toString(), edit_tommer.getText().toString(), edit_stand.getText().toString());
                System.out.println(prediction);
                textview_pris.setText(Float.toString(prediction));
            }
        });
    }

    private void setupUI() {
        edit_mærke = findViewById(R.id.edit_mærke);
        edit_stand = findViewById(R.id.edit_stand);
        textviewpris = findViewById(R.id.textView_testml);
        edit_tommer = findViewById(R.id.edit_tommer);
        textview_pris = findViewById(R.id.textView_pris);
        //textviewpris.setText(String.valueOf(prediction));

        btn_publish = findViewById(R.id.testml_publish);
    }

    private MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor = this.getAssets().openFd("model.tflite");
        FileInputStream inputStream=new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel=inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declareLenght=fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY,startOffset,declareLenght);
    }

    private float doInference(String inputString0, String inputString1, String inputString2) {
        float[] inputVal = new float[3];
        inputVal[0]=Float.parseFloat(inputString0);
        inputVal[1]=Float.parseFloat(inputString1);
        inputVal[2]=Float.parseFloat(inputString2);
        float[][] output= new float[3][3];
        tflite.run(inputVal, output);
        return output[0][0];
    }
}
