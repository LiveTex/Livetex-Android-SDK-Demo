package nit.livetex.livetexsdktestapp.presenters;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;

import nit.livetex.livetexsdktestapp.ui.callbacks.BaseCallback;

/**
 * Created by user on 14/5/2015.
 */
public abstract class BasePresenter<T extends BaseCallback> {

    private Context context;
    private T callback;
    private Handler handler;
    private Handler threadHandler;

    public BasePresenter(T callback) {
        this.callback = callback;
        this.context = callback.getContext();
        this.handler = new Handler();
        HandlerThread handlerThread = new HandlerThread("");
        handlerThread.start();
        threadHandler = new Handler(handlerThread.getLooper());
    }

    protected Context getContext() {
        return this.context;
    }

    protected T getCallback() {
        return this.callback;
    }

    protected void runOnUiThread(Runnable runnable) {
        handler.post(runnable);
    }

    protected void runOnUiThreadDelayed(Runnable runnable, long delay) {
        handler.postDelayed(runnable, delay);
    }

    public Handler getHandler() {
        return handler;
    }

    public Handler getThreadHandler() {
        return threadHandler;
    }
}
