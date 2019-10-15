package nit.livetex.livetexsdktestapp.fragments.presenters;

import android.util.Log;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import nit.livetex.livetexsdktestapp.MainApplication;
import nit.livetex.livetexsdktestapp.fragments.callbacks.InitCallback;
import nit.livetex.livetexsdktestapp.utils.DataKeeper;


/**
 * Created by user on 28.07.15.
 */
public class InitPresenter extends BasePresenter<InitCallback> {

    public InitPresenter(InitCallback callback) {
        super(callback);
    }

    public void init(final String id) {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("fblog", "getInstanceId failed", task.getException());
                            return;
                        }
                        String token = task.getResult().getToken();
                        DataKeeper.saveRegId(getContext(), token);
                        DataKeeper.saveAppId(getContext(), id);
                        MainApplication.initLivetex(id, token);                        
                        Log.d("fblog", token);
                    }
                });
    }

}
