package mmm.i7bachelor_smartsale.app.Utilities;

import android.os.AsyncTask;

import java.net.InetSocketAddress;
import java.net.Socket;

//https://www.youtube.com/watch?v=lL9t9-tiMlE&ab_channel=EDMTDev
public class InternetCheck extends AsyncTask<Void,Void, Boolean> {

    public interface Consumer {
        void accept(boolean internet);
    }

    Consumer consumer;

    public InternetCheck(Consumer consumer){
        this.consumer = consumer;
        execute();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress("google.com", 80), 1500);
            socket.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean aboolean){
        super.onPostExecute(aboolean);
        consumer.accept(aboolean);
    }
}
