package nit.livetex.livetexsdktestapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import sdk.LogUtil;

/**
 * Created by sergey.so on 02.12.2014.
 *
 */
public class DataKeeper {

    private static final String PREFERENCES = "com.livetex.livetexsdktestapp.PREFS";
    private static final String APP_ID_KEY = "com.livetex.livetexsdktestapp.application_id";
    private static final String EMPLOYEE_ID_KEY = "com.livetex.livetexsdktestapp.employeeId";
    private static final String REG_ID = "livetex.regId";
    private static final String LAST_MESSAGE = "livetex.lastMessage";

    public static String getLastMessage(Context context) {
        return context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).getString(LAST_MESSAGE, "");
    }

    public static void saveLastMessage(Context context, String lastMessage) {
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        pref.edit().putString(LAST_MESSAGE, lastMessage).apply();
    }

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

    public static void dropAll(Context context) {
        context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
                .edit()
                .clear()
                .commit();
    }

    public static void saveRegId(Context context, String regId) {
        context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
                .edit().putString(REG_ID, regId).commit();
    }

    public static String restoreRegId(Context context) {
        return context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).getString(REG_ID, "");
    }
}
