package nit.livetex.livetexsdktestapp.utils;

import android.content.Context;
import android.os.Build;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by user on 20.11.15.
 */
public class UserData {

    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";

    private Context context;
    private String userName;
    private String email;
    private String memory;
    private String device;
    private String version;
    private String batteryLevel;
    private boolean isWifiConnected;

    public UserData(Context context, String userName, String email, String memory, String device, String version, String batteryLevel, boolean isWifiConnected) {
        this.context = context;
        this.userName = userName;
        this.email = email;
        this.memory = memory;
        this.device = device;
        this.version = version;
        this.batteryLevel = batteryLevel;
        this.isWifiConnected = isWifiConnected;
    }

    public void send() {
        DataKeeper.setHHUserData(context, toStringSet());
    }

    public Set<String> toStringSet() {
        Set<String> set = new HashSet<>();
        //id, network type, email, platform, device, app version
        set.add("name:" + userName);
        set.add("email:" + email);
        set.add("ОС и ее версия:" + "Android " + Build.VERSION.RELEASE);
       // set.add("memory:" + memory);
        set.add("Модель устройства: " + device);
        set.add("Версия мобильного приложения: " + version);
       // set.add("battery lavel:" + batteryLevel);
        set.add("Тип соединения:" + (isWifiConnected ? "wi-fi" : "mobile"));
        return set;
    }

    public static class UserDataBuilder {

        private Context context;
        private String userName = "";
        private String email = "";
        private String memory = "";
        private String device = "";
        private String version = "";
        private String batteryLevel = "";
        private boolean isWifiConnected = false;

        public UserDataBuilder(Context context) {
            this.context = context;
        }

        public UserDataBuilder addUserName(String name) {
            this.userName = name;
            return this;
        }

        public UserDataBuilder addEmail(String email) {
            this.email = email;
            return this;
        }

        public UserDataBuilder addMemoryInfo(Long memory) {
            this.memory = Long.toString(memory);
            return this;
        }

        public UserDataBuilder addDeviceInfo(String device) {
            this.device = device;
            return this;
        }

        public UserDataBuilder addVersionInfo(String version) {
            this.version = version;
            return this;
        }

        public UserDataBuilder addBatteryLevelInfo(float batteryLevel) {
            this.batteryLevel = Float.toString(batteryLevel);
            return this;
        }

        public UserDataBuilder addWifiInfo(boolean isConnected) {
            this.isWifiConnected = isConnected;
            return this;
        }

        public UserData build() {
            UserData userData = new UserData(context, userName, email, memory, device, version, batteryLevel, isWifiConnected);
            return userData;
        }
    }
}
