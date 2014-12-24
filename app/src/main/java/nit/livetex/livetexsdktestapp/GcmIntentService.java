package nit.livetex.livetexsdktestapp;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.logging.Level;
import java.util.logging.Logger;

import nit.livetex.livetexsdktestapp.ChatActivity;
import nit.livetex.livetexsdktestapp.R;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class GcmIntentService extends IntentService {

    public static final int NOTIFICATION_ID = 1;
    private NotificationManager myNotificationManager;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if (!extras.isEmpty() && MainApplication.isPushActive()) {
            Log.e("mytag", "onHandleIntent process");
            Intent startIntent = new Intent(getApplicationContext(), ChatActivity.class);
            startIntent.putExtra(ChatActivity.IS_FROM_NOTIF, true);
            startIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
                    Intent.FLAG_ACTIVITY_SINGLE_TOP);
            sendNotification(extras.getString("title"), startIntent);
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String text, Intent intent) {
        myNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Livetex")
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                        .setAutoCancel(true)
                        .setSound(uri)
                        .setVibrate(new long[]{500, 300, 500})
                        .setContentText(text);
        mBuilder.setContentIntent(contentIntent);
        myNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
