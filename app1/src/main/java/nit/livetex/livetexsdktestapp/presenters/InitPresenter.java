package nit.livetex.livetexsdktestapp.presenters;

import nit.livetex.livetexsdktestapp.MainApplication;
import nit.livetex.livetexsdktestapp.providers.Dao;
import nit.livetex.livetexsdktestapp.ui.callbacks.InitCallback;
import nit.livetex.livetexsdktestapp.utils.CommonUtils;
import nit.livetex.livetexsdktestapp.utils.DataKeeper;
import nit.livetex.livetexsdktestapp.utils.GcmUtils;

/**
 * Created by user on 28.07.15.
 */
public class InitPresenter extends BasePresenter<InitCallback> {

    public InitPresenter(InitCallback callback) {
        super(callback);
    }

    public void init(final String id) {

        GcmUtils.startGCM(getCallback().getFragmentEnvironment(), new GcmUtils.Callback() {
            @Override
            public void onResult(boolean status, String msg) {
                if (!status) {
                    CommonUtils.showToast(getContext(), "GCM error: " + msg);
                    return;
                }
                if(!id.equals(DataKeeper.restoreAppId(getContext()))) {
                    Dao.getInstance(getContext()).clearAll();
                }
                DataKeeper.saveRegId(getContext(), msg);
                DataKeeper.saveAppId(getContext(), id);
                MainApplication.initLivetex(id, msg);

            }
        });

    }
}
