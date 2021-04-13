package mmm.i7bachelor_smartsale.app.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.Observer;

import java.util.List;

import mmm.i7bachelor_smartsale.app.Activities.InboxActivity;
import mmm.i7bachelor_smartsale.app.Models.PrivateMessage;
import mmm.i7bachelor_smartsale.app.Models.Repository;
import mmm.i7bachelor_smartsale.app.R;
import mmm.i7bachelor_smartsale.app.Utilities.Constants;

public class ForegroundService extends LifecycleService {

    private static final String TAG = "ForegroundService";
    private static final int SERVICE_NOTIFICATION_ID = 40;
    private static final int NOTIFICATION_INTENT = 3;
    private final Repository repo =  Repository.getInstance(this);
    private NotificationChannel serviceNotificationChannel;
    private NotificationManager serviceNotificationManager;
    private NotificationChannel notificationChannel;
    private NotificationManager notificationManager;
    private boolean UnreadMessage = false;
    private String UnreadMessageSender;
    public ForegroundService() {
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        repo.getConvosAndReadStatus().observe(this ,InboxObserver);
    }

    Observer<List<Pair<String, Integer>>> InboxObserver = new Observer<List<Pair<String, Integer>>>() {
        @Override
        public void onChanged(List<Pair<String, Integer>> UpdatedItems) {
            List<Pair<String, Integer>> list = UpdatedItems;
            for(int i = 0; i < list.size(); i+=1) {
                if(list.get(i).second!=0)
                {
                    UnreadMessage = true;
                    UnreadMessageSender = list.get(i).first;
                }
            }
            if (UnreadMessage) {
                updateNotification(UnreadMessageSender);
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "onStartCommand: ");

        createNotificationChannels();

        // create service notification
        Notification notification = new NotificationCompat.Builder(this, Constants.SERVICE_NOTIFICATION_CHANNEL)
                .setContentTitle(getString(R.string.service_notification_string))
                .setSmallIcon(R.drawable.ic_service_draw)
                .build();

        startForeground(SERVICE_NOTIFICATION_ID, notification);

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        super.onBind(intent);
        return null;
    }

    private void createNotificationChannels() {
        // create service notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            serviceNotificationChannel = new NotificationChannel(Constants.SERVICE_NOTIFICATION_CHANNEL, "Foreground Service", NotificationManager.IMPORTANCE_DEFAULT);
            serviceNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            serviceNotificationManager.createNotificationChannel(serviceNotificationChannel);
        }
        // create regular notification channel
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(Constants.NOTIFICATION_CHANNEL, "Private Message Notification", NotificationManager.IMPORTANCE_LOW);
            notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private void updateNotification(String sender)
    {
        Intent resultIntent = new Intent(this, InboxActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(NOTIFICATION_INTENT, PendingIntent.FLAG_UPDATE_CURRENT);

        if(notificationManager != null)
        {
            String notificationUpdate = getString(R.string.notification_update_string) + " " + sender;
            Notification notification = new NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL)
                    .setContentTitle(notificationUpdate)
                    .setSmallIcon(R.drawable.ic_service_draw)
                    .setContentIntent(resultPendingIntent)
                    .build();
            notificationManager.notify(Constants.NOTIFICATION_ID, notification);
        }
    }
}
