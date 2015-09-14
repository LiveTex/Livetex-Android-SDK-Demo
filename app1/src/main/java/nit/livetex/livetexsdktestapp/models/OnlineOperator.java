package nit.livetex.livetexsdktestapp.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by user on 12.08.15.
 */
public class OnlineOperator {

    private final static String OPERATOR_ID = "operatorId";
    private final static String OPERATOR_NAME = "operatorName";
    private final static String OPERATOR_AVATAR = "operatorAvatar";

    private String id;
    private String name;
    private String avatar;

    public OnlineOperator(String id, String name, String avatar) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAvatar() {
        return avatar;
    }

    public OnlineOperator(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        id = prefs.getString(OPERATOR_ID, "");
        name = prefs.getString(OPERATOR_NAME, "");
        avatar = prefs.getString(OPERATOR_AVATAR, "");
    }

    public void toSharedPreferences(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(OPERATOR_AVATAR, avatar)
                .putString(OPERATOR_ID, id)
                .putString(OPERATOR_NAME, name).commit();
    }


}
