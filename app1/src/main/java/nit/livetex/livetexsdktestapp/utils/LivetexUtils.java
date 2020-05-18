package nit.livetex.livetexsdktestapp.utils;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.content.FileProvider;

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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nit.livetex.livetexsdktestapp.BuildConfig;

/**
 * Created by user on 28.07.15.
 */
public class LivetexUtils {

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static HashMap<String, String> fromStringSet(Set<String> set) {
        HashMap<String, String> map = new HashMap<>();
        for (String s : set) {
            String[] parts = s.split(":");
            map.put(parts[0], (parts.length == 1) ? "" : parts[1]);
        }
        return map;
    }

    public static boolean isEmpty(EditText... ets) {
        for (EditText editText : ets) {
            if (editText.getText() == null || editText.getText().length() == 0) {
                return true;
            }
        }
        return false;
    }

    public static void clear(EditText... ets) {
        for (EditText editText : ets) {
            editText.setText("");
        }
    }

    public static boolean isInetActive(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void galleryAddPic(Context context, Uri uri) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, uri.getPath());
        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    public static void multipart(String url, File file) throws IOException {
        MultipartUtility multipartUtility = new MultipartUtility(url, "UTF-8");
        multipartUtility.addFilePart("file", file);
        multipartUtility.finish();
    }

    public static Point getScreenSize(Activity activity) {
        Point screeSize = new Point();
        getDefaultDisplay(activity).getSize(screeSize);
        return screeSize;
    }

    public static Display getDefaultDisplay(Context context) {
        return ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
    }

    public static long getID() {
        long LIMIT = 10000000000L;
        long last = 0;
        // 10 digits.
        long id = System.currentTimeMillis() % LIMIT;
        if (id <= last) {
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
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static boolean isEmailValid(String email) {
        String regEx = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        Matcher matcherObj = Pattern.compile(regEx).matcher(email);
        return matcherObj.matches();
    }

}
























