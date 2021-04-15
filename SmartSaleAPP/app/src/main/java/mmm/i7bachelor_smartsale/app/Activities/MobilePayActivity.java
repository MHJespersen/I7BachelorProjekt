package mmm.i7bachelor_smartsale.app.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import mmm.i7bachelor_smartsale.app.R;
import mmm.i7bachelor_smartsale.app.Utilities.Constants;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MobilePayActivity extends MainActivity {

    private TextView mobilepaytext;
    private ImageView qrcode;
    private Uri imageUri = null;
    private final int MOBILEPAY_REQUEST_PAYMENT_CODE = 101;
    private ExecutorService executor;
    private OkHttpClient client = new OkHttpClient().newBuilder().build();
    private String paymentId;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        executor = Executors.newSingleThreadExecutor();

        setContentView(R.layout.activity_mobilepay);

        mobilepaytext = findViewById(R.id.textmobilepay);
        qrcode = findViewById(R.id.qrmobilepay);
        qrcode.setOnClickListener(view -> {
            try {
                scanBarcode(view);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    private void scanBarcode(View view) throws JSONException {
        Uri uri = Uri.parse("mobilepaypos://pos?id=147025836912345&source=qr");
        Intent pickIntent = new Intent(Intent.ACTION_VIEW, uri);
        startActivityForResult(pickIntent, MOBILEPAY_REQUEST_PAYMENT_CODE);
        Toast.makeText(this, "Opening MobilePay App", Toast.LENGTH_LONG).show();
    }

    //Links: https://mobilepaydev.github.io/MobilePay-PoS-v10/index
    //https://developer.mobilepay.dk/products/point-of-sale/test
    //https://sandbox-developer.mobilepay.dk/node/3140
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MOBILEPAY_REQUEST_PAYMENT_CODE) {
            if (resultCode == RESULT_OK) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
            if (resultCode == RESULT_CANCELED) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        }
    }
}
