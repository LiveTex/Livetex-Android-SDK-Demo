package nit.livetex.livetexsdktestapp.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import nit.livetex.livetexsdktestapp.FragmentEnvironment;
import nit.livetex.livetexsdktestapp.R;
import nit.livetex.livetexsdktestapp.utils.DataKeeper;

public final class FirebaseMessageReceiver extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("fb messaging", remoteMessage.getFrom());
        sendNotification(remoteMessage);
    }

    @Override
    public void onNewToken(@NonNull String newToken) {
        // This token will start working on next Livetex init (app restart for now)
        DataKeeper.saveRegId(this, newToken);
    }

    private void sendNotification(RemoteMessage remoteMessage) {
        Intent intent = new Intent(this, FragmentEnvironment.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String messageTitle = remoteMessage.getNotification().getTitle();
        String messageText = remoteMessage.getNotification().getBody();

        String channelId = "Chat notifications";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.default_icon)
                        .setContentTitle(messageTitle)
                        .setContentText(messageText)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Chat notifications",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

}
