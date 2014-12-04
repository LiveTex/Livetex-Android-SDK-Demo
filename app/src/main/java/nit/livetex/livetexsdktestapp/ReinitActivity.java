package nit.livetex.livetexsdktestapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;

import livetex.sdk.models.DialogState;

/**
 * Created by sergey.so on 02.12.2014.
 *
 */
public class ReinitActivity extends BaseActivity {

    private boolean isFirstRun = false;
    private boolean isReInit = false;
    private boolean isStopLock = false;
    private BroadcastReceiver mInternetStateReciever;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFirstRun = true;
        mInternetStateReciever = new InternetStateReciever();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(mInternetStateReciever, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        if (!isFirstRun)
            checkState();
        isFirstRun = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(mInternetStateReciever);
        if (!isStopLock)
            MainApplication.stopLivetex();
    }

    protected void lockStop(){
        isStopLock = true;
    }

    private void checkState() {
        showProgressDialog("Восстановление соединения");
        String appId = DataKeeper.restoreAppId(this);
        if (appId == null) {
            showInitActivity();
        } else {
            isReInit = true;
            MainApplication.initLivetex(appId);
        }
    }

    private void showInitActivity(){
        unregister();
        InitActivity.show(this);
        finish();
    }

    protected void unregister() {
        try {
            unregisterReceiver(mReciever);
        } catch (IllegalArgumentException ignored) {
        }
    }

    @Override
    protected void initComplete() {
        getDialogState();
        showProgressDialog("инициализация");
    }

    protected void getDialogState() {
//        showProgressDialog("Получение состояния диалога");
        MainApplication.getDialogState();
    }

    @Override
    protected void onDialogStateGetted(DialogState state) {
        if (state == null) return;
        if (!isReInit) return;

        isReInit = false;
        if (state.conversation == null && (this instanceof ChatActivity)) {
            showWelcomeActivity();
        } else if (state.conversation != null && (this instanceof WelcomeActivity)) {
            unregister();
            lockStop();
            ChatActivity.show(this);
            finish();
        }
    }

    protected void showWelcomeActivity(){
        unregister();
        lockStop();
        WelcomeActivity.show(this);
        finish();
    }

    @Override
    protected void onError(String request) {
        if (!request.equals(MainApplication.REQUEST_INIT)) return;
        showInitActivity();
    }

    private class InternetStateReciever extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                NetworkInfo networkInfo =
                        intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if(networkInfo.isConnected()) {
                    // Wifi is connected
                    Log.d("Inetify", "Wifi is connected: " + String.valueOf(networkInfo));
                }
            } else if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                NetworkInfo networkInfo =
                        intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
                if(networkInfo.getType() == ConnectivityManager.TYPE_WIFI &&
                        ! networkInfo.isConnected()) {
                    // Wifi is disconnected
                    Log.d("Inetify", "Wifi is disconnected: " + String.valueOf(networkInfo));
                }
            }
        }
    }
}
