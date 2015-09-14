package nit.livetex.livetexsdktestapp.utils;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by user on 28.07.15.
 */
public class CommonUtils {

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static boolean isEmpty(EditText... ets) {
        for(EditText editText : ets) {
            if(editText.getText() ==null || editText.getText().length() == 0) {
                return true;
            }
        }
        return false;
    }

    public static void clear(EditText... ets) {
        for(EditText editText : ets) {
            editText.setText("");
        }
    }

    public static boolean isInetActive(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String getPath(Context context, Uri uri) {
        if("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {"_data"};
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if(cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {

            }
        }
        else if("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static void multipart(String url, File file) throws IOException {
        MultipartUtility multipartUtility = new MultipartUtility(url, "UTF-8");
        multipartUtility.addFilePart("file", file);
        multipartUtility.finish();


    }

    public static long getID() {
        long LIMIT = 10000000000L;
        long last = 0;
        // 10 digits.
        long id = System.currentTimeMillis() % LIMIT;
        if ( id <= last ) {
            id = (last + 1) % LIMIT;
        }
        return last = id;
    }

    public static String sendMultipart(File file, String url) throws IOException {
        HttpClient client = new DefaultHttpClient();

        HttpEntity entity = MultipartEntityBuilder
                .create()
//                .addTextBody("field1", "val1")
//                .addTextBody("field2","val2")
                .addBinaryBody("file", file, ContentType.create("application/zip"), file.getName())
                .build();
        HttpPost post = new HttpPost(url);
        post.setEntity(entity);

        HttpResponse response = client.execute(post);
        HttpEntity ent = response.getEntity();
        String resp = EntityUtils.toString(ent, "UTF-8");
        return resp;
    }

    public static void hideSoftKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static boolean isEmailValid(String email) {
        String regEx = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Matcher matcherObj = Pattern.compile(regEx).matcher(email);
        return matcherObj.matches();
    }

}
























