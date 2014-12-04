package nit.livetex.livetexsdktestapp;

import android.content.Context;
import android.content.SharedPreferences;

import livetex.sdk.LogUtil;

/**
 * Created by sergey.so on 02.12.2014.
 */
public class DataKeeper {

    private static final String PREFERENCES = "com.livetex.livetexsdktestapp.PREFS";
    private static final String APP_ID_KEY = "com.livetex.livetexsdktestapp.application_id";

    public static void saveAppId(Context context, String appId){
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        pref.edit()
                .putString(APP_ID_KEY, appId)
                .apply();
    }

    public static String restoreAppId(Context context){
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        LogUtil.log("restoreToken " + pref.getString(APP_ID_KEY, ""));
        return pref.getString(APP_ID_KEY, "");
    }
}
