package nit.livetex.livetexsdktestapp.fragments.presenters;

import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import java.util.UUID;

import nit.livetex.livetexsdktestapp.Const;
import nit.livetex.livetexsdktestapp.MainApplication;
import nit.livetex.livetexsdktestapp.fragments.callbacks.InitCallback;
import nit.livetex.livetexsdktestapp.utils.DataKeeper;


/**
 * Created by user on 28.07.15.
 */
public class InitPresenter extends BasePresenter<InitCallback> {

    // Unique device token. In real app use Firebase token id if you need push notifications. Otherwise generate random UUID once.
    private String regId = null;

    public InitPresenter(InitCallback callback) {
        super(callback);
    }

    public void init(final String appId) {

        // for debug only
        if (!TextUtils.isEmpty(Const.FORCED_DEVICE_ID)) {
            Log.v("Firebase", "Init with debug regId = " + regId);
            MainApplication.initLivetex(appId, Const.FORCED_DEVICE_ID);
            return;
        }

        // Don't load Firebase token every time, use saved value
        String savedRegId = DataKeeper.restoreRegId(getContext());
        if (!TextUtils.isEmpty(savedRegId)) {
            Log.v("Firebase", "Init with previous regId = " + regId);
            MainApplication.initLivetex(appId, regId);
            return;
        }

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("Firebase", "getInstanceId failed", task.getException());
                        return;
                    }
                    regId = task.getResult().getToken();
                    Log.v("Firebase", "firebase auth success, regId = " + regId);

                    DataKeeper.saveRegId(getContext(), regId);
                    DataKeeper.saveAppId(getContext(), appId);
                    MainApplication.initLivetex(appId, regId);
                })
                // Только для демо приложения т.к. google-service.json не содержит реальные данные
                .addOnFailureListener(e -> {
                    regId = UUID.randomUUID().toString();
                    Log.w("Firebase", "firebase auth failed, regId = " + regId, e);

                    DataKeeper.saveRegId(getContext(), regId);
                    DataKeeper.saveAppId(getContext(), appId);
                    MainApplication.initLivetex(appId, regId);
                });
    }

}
