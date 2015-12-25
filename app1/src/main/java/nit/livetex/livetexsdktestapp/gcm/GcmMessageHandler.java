package nit.livetex.livetexsdktestapp.gcm;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import nit.livetex.livetexsdktestapp.Const;
import nit.livetex.livetexsdktestapp.FragmentEnvironment;
import nit.livetex.livetexsdktestapp.MainApplication;
import nit.livetex.livetexsdktestapp.R;
import nit.livetex.livetexsdktestapp.models.BaseMessage;
import nit.livetex.livetexsdktestapp.models.EventMessage;
import nit.livetex.livetexsdktestapp.models.OnlineOperator;
import nit.livetex.livetexsdktestapp.providers.ConversationsProvider;
import nit.livetex.livetexsdktestapp.providers.Dao;
import nit.livetex.livetexsdktestapp.services.DownloadService;
import nit.livetex.livetexsdktestapp.utils.BusProvider;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by user on 11.08.15.
 */
public class GcmMessageHandler extends IntentService {

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
            Log.d("double", "!is_active");
            if(cid != null) {
                Dao.getInstance(this).saveMessage(mes, String.valueOf(System.currentTimeMillis()), Integer.parseInt(cid), "0".equals(sender));
                Intent initIntent = new Intent(this, FragmentEnvironment.class);
                initIntent.setAction(Const.PUSH_OFFLINE_ACTION);
                initIntent.putExtra(ConversationsProvider.CONVERSATION_ID, cid);
                initIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                sendNotification(mes, initIntent);
            } else {

                if(mes.startsWith("http")) {
                    File path = new File(Environment.getExternalStorageDirectory(), "Downloadzz");
                    if(!path.exists()) {
                        path.mkdirs();
                    }
                    Log.d("downloadzz", path.getAbsolutePath());
                    String[] parts = mes.split("/");
                    try {
                        String fileName = URLDecoder.decode(parts[parts.length - 1], "UTF-8");
                        File outFile = new File(path, fileName);
                        Intent i = new Intent(this, DownloadService.class);
                        i.putExtra("url", mes);
                        i.putExtra("outFile", outFile);
                        startService(i);
                        mes = outFile.getAbsolutePath();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                }

                OnlineOperator onlineOperator = new OnlineOperator(this);
                Dao.getInstance(this).saveMessage(mes, String.valueOf(System.currentTimeMillis()), Integer.parseInt(onlineOperator.getId()), false);

                Intent initIntent = new Intent(GcmMessageHandler.this, FragmentEnvironment.class);
                initIntent.setAction(Const.PUSH_ONLINE_ACTION);
                initIntent.putExtra("mes", mes);
                initIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                sendNotification(mes, initIntent);

            }
        } else {
            if(cid != null) {
                final EventMessage eventMessage = new EventMessage(BaseMessage.TYPE.OFFLINE_MSG_RECEIVED);
                eventMessage.putSerializable(mes);

                Dao.getInstance(this).saveMessage(mes, String .valueOf(System.currentTimeMillis()), Integer.parseInt(cid), "0".equals(sender));
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        MainApplication.postMessage(eventMessage);
                    }
                });

            }
        }

        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String text, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_app)
                .setContentTitle("Вам сообщение")
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
