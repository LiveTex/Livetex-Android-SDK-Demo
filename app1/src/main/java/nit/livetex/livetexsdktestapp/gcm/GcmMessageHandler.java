package nit.livetex.livetexsdktestapp.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import nit.livetex.livetexsdktestapp.Const;
import nit.livetex.livetexsdktestapp.FragmentEnvironment;
import nit.livetex.livetexsdktestapp.MainApplication;
import nit.livetex.livetexsdktestapp.R;
import nit.livetex.livetexsdktestapp.utils.BusProvider;
import nit.livetex.livetexsdktestapp.utils.DataKeeper;
import nit.livetex.livetexsdktestapp.utils.DeviceUtils;

import java.util.ArrayList;

/**
 * Created by user on 11.08.15.
 */
public class GcmMessageHandler extends IntentService {

    public static ArrayList<String> messages = new ArrayList<>();

    public static final int NOTIFICATION_ID = 1;

    private String mes;
    private Handler handler;


    public GcmMessageHandler() {
        super("GcmMessageHandler");
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        handler = new Handler();
        BusProvider.register(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        mes = extras.getString("text");

        String cid = extras.getString("cid");
        String sender = extras.getString("sender");

        Log.d("double", "push mes " + mes + ", cid " + cid + ", sender " + sender);

        if(!MainApplication.IS_ACTIVE) {
            messages.add(mes);
            DataKeeper.incUnreadMessages(this);

            PowerManager.WakeLock screenOn =
                    ((PowerManager)getSystemService(POWER_SERVICE))
                    .newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "app1:example");
            screenOn.acquire();
            boolean isScreenOn = DeviceUtils.isScreenOn(this);
            Intent initIntent = new Intent(GcmMessageHandler.this, FragmentEnvironment.class);
            initIntent.setAction(Const.PUSH_ONLINE_ACTION);
            initIntent.putExtra("mes", mes);
            initIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            sendNotification(mes, initIntent);
            /*if(isScreenOn) {
                Intent i = new Intent("android.intent.action.MAIN");
                i.setClass(this, ServiceDialog.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_SINGLE_TOP |
                        Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("text", mes);
                this.startActivity(i);
            } else {
                Intent initIntent = new Intent(GcmMessageHandler.this, FragmentEnvironment.class);
                initIntent.setAction(Const.PUSH_ONLINE_ACTION);
                initIntent.putExtra("mes", mes);
                initIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                sendNotification(mes, initIntent);
            }*/

            screenOn.release();


        }

        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String text, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_app)
                .setContentTitle("Вам сообщение от техподдержки")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                .setAutoCancel(true)
                .setSound(uri)
                .setVibrate(new long[]{500, 300, 500})
                .setContentText(text);
        builder.setContentIntent(contentIntent);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    @Override
    public void onDestroy() {
        BusProvider.unregister(this);
        super.onDestroy();
    }

    public void showToast(){
        handler.post(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), mes, Toast.LENGTH_LONG).show();
            }
        });

    }

}
