package nit.livetex.livetexsdktestapp.utils;


import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

public class BusProvider {
    private static Bus bus = new Bus();

    public static Bus getInstance()
    {

        return bus;
    }

    public static void register(Object object) {
        try {
           bus.register(object);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void unregister(Object object) {
        try {
            bus.unregister(object);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static class MainThreadBus extends Bus {
        private final Handler mHandler = new Handler(Looper.getMainLooper());

        @Override
        public void post(final Object event) {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                super.post(event);
            } else {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        MainThreadBus.super.post(event);
                    }
                });
            }
        }
    }
}