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

import mmm.i7bachelor_smartsale.app.R;

public class MobilePayActivity extends MainActivity {

    private Object BarcodeFormat;
    private TextView mobilepaytext;
    private ImageView qrcode;
    private Uri imageUri = null;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobilepay);

        mobilepaytext = findViewById(R.id.textmobilepay);
        qrcode = findViewById(R.id.qrmobilepay);

        qrcode.setOnClickListener(view -> scanBarcode(view));
    }

    private void scanBarcode(View view){
        Uri uri = Uri.parse("mobilepaypos://pos?id=9522134410&source=qr");
        Intent pickIntent = new Intent(Intent.ACTION_VIEW, uri);

        startActivity(pickIntent);
        Toast toast = Toast.makeText(this, "Opening MobilePay App", Toast.LENGTH_SHORT);
        toast.show();
    }

    //Links: https://mobilepaydev.github.io/MobilePay-PoS-v10/index
    //https://developer.mobilepay.dk/products/point-of-sale/test
    //https://sandbox-developer.mobilepay.dk/node/3140
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111) {
            if (data == null || data.getData() == null) {
                Log.e("TAG", "The uri is null, probably the user cancelled the image selection process using the back button.");
                return;
            }
            if (resultCode == RESULT_OK) {
            } if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, getString(R.string.canceled), Toast.LENGTH_LONG).show();
            }
        }
    }

}
