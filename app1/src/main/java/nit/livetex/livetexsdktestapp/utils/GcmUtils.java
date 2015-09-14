package nit.livetex.livetexsdktestapp.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

/**
 * Created by sergey.so on 22.12.2014.
 *
 */
public class GcmUtils {

    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    public static final String PUSH_SET = "push_set";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private static final String GCM_SENDER_ID = "277402781258"/*"27374021591"*/;
    private static final String GCM = "gcm_id";

    public static void unregister(final Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(PUSH_SET, false).commit();
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(GCM, null).commit();
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    GoogleCloudMessaging.getInstance(context).unregister();
                } catch (IOException e) {
                }
                return null;
            }
        }.execute();
    }

    public static void startGCM(Activity activity, Callback callback) {
        if (!checkPlayServices(activity)) {
            callback.onResult(false, "This device is not supported by play services.");
            return;
        }
        checkGCM(activity, callback);
    }

    private static boolean checkPlayServices(Activity activity) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.e("livetex", "This device is not supported.");
            }
            return false;
        }
        return true;
    }

    private static void checkGCM(final Context context, Callback callback) {
        if (!isGcmSet(context)) {
            getGcmId(context, callback);
        } else {
            callback.onResult(true, restoreRegistrationId(context));
        }
    }

    private static void getGcmId(final Context context,final Callback callback) {
        String gcmKey = PreferenceManager.getDefaultSharedPreferences(context).getString(GCM, null);
        if (gcmKey == null) {
            new AsyncTask<String, String, String>() {

                ProgressDialog progressDialog;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    progressDialog = ProgressDialog.show(context, "", "Подождите");
                }

                @Override
                protected String doInBackground(String... params) {
                    try {
                        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
                        String regid = gcm.register(GCM_SENDER_ID);
                        // You should send the registration ID to your server over HTTP, so it
                        // can use GCM/HTTP or CCS to send messages to your app.
                        // For this demo: we don't need to send it because the device will send
                        // upstream messages to a server that echo back the message using the
                        // 'from' address in the message.

                        // Persist the regID - no need to register again.
                        storeRegistrationId(context, regid);
                        return regid;
                    } catch (IOException ex) {
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(String aVoid) {
                    super.onPostExecute(aVoid);
                    if (progressDialog != null) {
                        progressDialog.cancel();
                    }
                    if (aVoid != null) {
                        storeRegistrationId(context, aVoid);
                        setGcm(context);
                        callback.onResult(true, aVoid);
                    } else {
                        callback.onResult(false, "I/O Exception");
                    }
                }
            }.execute(null, null, null);
        } else {
            callback.onResult(true, gcmKey);
        }
    }


    private static void storeRegistrationId(Context context, String regId) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(GCM, regId).commit();
    }

    public static String restoreRegistrationId(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(GCM, null);
    }

    private static boolean isGcmSet(final Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PUSH_SET, false);
    }

    private static void setGcm(final Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(PUSH_SET, true).commit();
    }

    public interface Callback {
        public void onResult(boolean status, String msg);
    }
}
