package nit.livetex.livetexsdktestapp.utils;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;

/**
 * Created by user on 05.11.15.
 */
public class DeviceUtils {

    public static String getDevice() {
        String deviceModel = android.os.Build.MODEL;
        String deviceManufacturer = android.os.Build.MANUFACTURER;
        String deviceModelAndManufacturer = "";
        if (deviceModel != null) deviceModelAndManufacturer += deviceModel;
        if (deviceManufacturer != null) deviceModelAndManufacturer += " " + deviceManufacturer;
        return deviceModelAndManufacturer;
    }

    public static boolean isScreenOn(Context context) {
        KeyguardManager kgMgr = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        return !kgMgr.inKeyguardRestrictedInputMode();
    }

    public static int getAndroidVersion() {
        return android.os.Build.VERSION.SDK_INT;
    }

    public static String getLibVersion(Context context) {
        String version = "";
        try {
            version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return version;
    }

    public static float getBatteryLevel(Context context) {
        Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        // Error checking that probably isn't needed but I added just in case.
        if(level == -1 || scale == -1) {
            return 50.0f;
        }

        return ((float)level / (float)scale) * 100.0f;
    }

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isConnected();
    }

    public static long getFreeMemoryCount(Context context) {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        // available memory in megabytes
        return mi.availMem / 1048576L;
    }
}
