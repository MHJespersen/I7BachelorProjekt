package mmm.i7bachelor_smartsale.app.Webapi;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import mmm.i7bachelor_smartsale.app.Models.ExchangeRates;

public class WebAPI {
    private static final String TAG = "WebAPI";
    RequestQueue queue;
    Context context;
    Callback callback;

    public WebAPI(Context context) {
        this.context = context;
    }

    public void loadData(Callback callback){
        this.callback = callback;
        String base = "https://api.ratesapi.io/api/latest?base=DKK";
        sendRequest(base);
    }

    private void sendRequest(String url){
        if(queue==null){
            queue = Volley.newRequestQueue(context);
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    parseJson(response);
                }, error -> Log.e(TAG, "Volley request did not work!", error));

        queue.add(stringRequest);
    }

    private void parseJson(String json){
        Gson gson = new GsonBuilder().create();
        ExchangeRates exchangeRates =  gson.fromJson(json, ExchangeRates.class);
        if(exchangeRates!=null){
            callback.OnApiCallback(exchangeRates);
        }
    }
}