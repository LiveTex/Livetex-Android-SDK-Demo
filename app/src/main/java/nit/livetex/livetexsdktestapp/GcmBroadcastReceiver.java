package nit.livetex.livetexsdktestapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    public GcmBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("mytag", "onReceive");
        ComponentName comp = new ComponentName(context.getPackageName(),
                GcmIntentService.class.getName());
        context.startService(intent.setComponent(comp));
        setResultCode(Activity.RESULT_OK);
    }
}
