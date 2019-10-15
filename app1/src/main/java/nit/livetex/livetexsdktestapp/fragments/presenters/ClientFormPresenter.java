package nit.livetex.livetexsdktestapp.fragments.presenters;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;

import nit.livetex.livetexsdktestapp.MainApplication;
import nit.livetex.livetexsdktestapp.fragments.callbacks.ClientFormCallback;
import nit.livetex.livetexsdktestapp.utils.DataKeeper;
import nit.livetex.livetexsdktestapp.utils.DeviceUtils;
import nit.livetex.livetexsdktestapp.utils.UserData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import livetex.queue_service.Destination;
import sdk.handler.AHandler;
import sdk.models.LTDialogAttributes;

/**
 * Created by user on 29.07.15.
 */
public class ClientFormPresenter extends BasePresenter<ClientFormCallback> {

    public ClientFormPresenter(ClientFormCallback callback) {
        super(callback);

    }

    public void process() {
        final Handler handler = new Handler();
        HandlerThread thread = new HandlerThread("");
        thread.start();
        Handler backgroundHandler = new Handler(thread.getLooper());
        backgroundHandler.post(new Runnable() {
            @Override
            public void run() {

                MainApplication.getDestinations(new AHandler<ArrayList<Destination>>() {
                    @Override
                    public void onError(String errMsg) {

                    }

                    @Override
                    public void onResultRecieved(final ArrayList<Destination> destinations) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if(destinations != null && destinations.size() != 0) {
                                    getCallback().onDestinationsReceived(destinations);
                                } else {
                                    getCallback().onDepartmentsEmpty();
                                }
                            }
                        });

                    }
                });

            }
        });

    }

    public static void sendToDestination(Context context, final Destination destination, final String name, ClientFormCallback callback) {
        

        final long mem = DeviceUtils.getFreeMemoryCount(context);
        int systemVersion = DeviceUtils.getAndroidVersion();
        final String device = DeviceUtils.getDevice();
        final String version = DeviceUtils.getLibVersion(context);
        final float batteryLevel = DeviceUtils.getBatteryLevel(context);
        final boolean isWifiConnected = DeviceUtils.isWifiConnected(context);
        final String email = "joe@gmail.com";
        new UserData.UserDataBuilder(context).addBatteryLevelInfo(batteryLevel)
                .addDeviceInfo(device).addEmail(email).addMemoryInfo(mem).addVersionInfo(version).addWifiInfo(isWifiConnected).build().send();

        Map<String, String> map = new HashMap<>();
        map.put("ОС и ее версия: ", String.valueOf(android.os.Build.VERSION.SDK_INT));
        map.put("Модель устройства: ", device);
        map.put("Версия мобильного приложения: ", version);
        map.put("Тип соединения:",  (isWifiConnected ? "wi-fi" : "mobile"));

        MainApplication.setName(name);
        DataKeeper.setClientName(context, name);
        MainApplication.setDestination(destination, new LTDialogAttributes(map));
        if(destination != null) {
            MainApplication.currentConversation = destination.getTouchPoint().getTouchPointId();
            callback.createChat();
        }

    }

}
