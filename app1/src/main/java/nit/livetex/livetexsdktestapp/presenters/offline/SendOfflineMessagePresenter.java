package nit.livetex.livetexsdktestapp.presenters.offline;

import android.util.Log;

import nit.livetex.livetexsdktestapp.MainApplication;
import nit.livetex.livetexsdktestapp.presenters.BasePresenter;
import nit.livetex.livetexsdktestapp.ui.callbacks.SendOfflineMessageCallback;
import nit.livetex.livetexsdktestapp.utils.DataKeeper;

import sdk.handler.AHandler;

/**
 * Created by user on 28.07.15.
 */
public class SendOfflineMessagePresenter extends BasePresenter<SendOfflineMessageCallback> {


    public SendOfflineMessagePresenter(SendOfflineMessageCallback callback) {
        super(callback);
    }

    public void createConversation(String name, String email, String phone, final String message) {
        MainApplication.createOfflineConversation(name, email, phone, MainApplication.OFFLINE_DEPARTMENT_ID, new AHandler<Integer>() {
            @Override
            public void onError(String errMsg) {
            }

            @Override
            public void onResultRecieved(Integer result) {
                final int conversationId = result;

                MainApplication.sendOfflineMessage(message, conversationId, new AHandler<Boolean>() {
                    @Override
                    public void onError(String errMsg) {

                    }

                    @Override
                    public void onResultRecieved(Boolean result) {
                        DataKeeper.saveLastMessage(getContext(), message);
                        Log.d("offline_result", "Result " + result);
                        getCallback().onMessageSended();
                    }
                });
            }
        });
    }
}
