package nit.livetex.livetexsdktestapp;

import android.content.Context;
import android.content.SharedPreferences;

import livetex.sdk.LogUtil;

/**
 * Created by sergey.so on 02.12.2014.
 *
 */
public class DataKeeper {

    private static final String PREFERENCES = "com.livetex.livetexsdktestapp.PREFS";
    private static final String APP_ID_KEY = "com.livetex.livetexsdktestapp.application_id";
    private static final String EMPLOYEE_ID_KEY = "com.livetex.livetexsdktestapp.employeeId";

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

    public static void saveEmployee(Context context, String employeId){
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        pref.edit()
                .putString(EMPLOYEE_ID_KEY, employeId)
                .apply();
    }

    public static String restoreEmployee(Context context){
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        return pref.getString(EMPLOYEE_ID_KEY, "");
    }

    public static void dropEmployeeId(Context context){
        context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
                .edit()
                .remove(EMPLOYEE_ID_KEY)
                .commit();
    }
}
