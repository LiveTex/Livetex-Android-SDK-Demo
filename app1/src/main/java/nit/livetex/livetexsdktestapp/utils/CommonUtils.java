package nit.livetex.livetexsdktestapp.utils;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nit.livetex.livetexsdktestapp.MainApplication;

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
                    String path = cursor.getString(column_index);
                    return path;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static Uri getOutputMediaFile(Context context) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return Uri.fromFile(new File(getStorageDir(context).getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg"));
    }

    private static File getStorageDir(Context context) {
        if (!isStorageAvailable())
            return context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File mediaStorageDir = new File(context.getExternalCacheDir(), "LiveTex");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        return mediaStorageDir;
    }

    private static boolean isStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
    public static boolean isKitKat() {
        return android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }
    private static Uri getMediaUri() {
        String state = Environment.getExternalStorageState();
        if (!state.equalsIgnoreCase(Environment.MEDIA_MOUNTED))
            return MediaStore.Images.Media.INTERNAL_CONTENT_URI;

        return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    }
    public static Uri getRealPathFromURI(Context context, Uri contentUri) {
       // Context context = MainApplication.getInstance();
        if (isKitKat()) {
            // get the id of the image selected by the user
            try {
                String wholeID = DocumentsContract.getDocumentId(contentUri);
                String id = wholeID.split(":")[1];

                String[] projection = {MediaStore.Images.Media.DATA};
                String whereClause = MediaStore.Images.Media._ID + "=?";
                Cursor cursor = context.getContentResolver().query(getMediaUri(),
                        projection, whereClause, new String[]{id}, null);
                if (cursor != null) {
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    if (cursor.moveToFirst()) {
                        Uri result = Uri.parse(cursor.getString(column_index));
                        cursor.close();
                        return result;
                    }
                    cursor.close();
                } else {
                    return contentUri;
                }
            } catch(Exception e) {
                e.printStackTrace();

            }


        } else {
            Uri res = null;
            final String[] columns = {MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME};
            Cursor cursor = context.getContentResolver()
                    .query(contentUri, columns, null, null, null);
            if (cursor == null || !cursor.moveToFirst()) {
                if (cursor != null) cursor.close();
                return null;
            }
            String path;
            int columnIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
            if (columnIndex != -1) {
                path = cursor.getString(columnIndex);
                res = Uri.parse(path);
            } else {
                columnIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME);
                if (columnIndex != -1) {
                    res = contentUri;
                }
            }
            cursor.close();
            return res;
        }
        return null;
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
























