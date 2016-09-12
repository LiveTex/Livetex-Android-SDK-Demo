package nit.livetex.livetexsdktestapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by dev on 26.04.16.
 */
public class UtilsTokens {
    private static final String PREFERENCES = "com.libhh.PREFS";
    private static final String TOKENS = "com.libhh.TOKENS";
    private static final String LAST_TOKEN = "livetex.lastToken";

    public static void addToken(Context context, String token) {
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        Set<String> currentTokens = getTokens(context);
        currentTokens.add(token);
        pref.edit().clear().commit();
        pref.edit().putStringSet(TOKENS, currentTokens).commit();
    }

    public static SharedPreferences getPrefs(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        return prefs;
    }

    public static Set<String> getTokens(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        Set<String> tokens = pref.getStringSet(TOKENS, new HashSet<String>());
        return tokens;
    }

    public static boolean hasToken(Context context, String token) {
        Set<String> tokens = getTokens(context);
        return tokens.contains(token);
    }

    public static void saveLastToken(Context context, String token) {
        context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).edit().putString(LAST_TOKEN, token).apply();
    }

    public static String getLastToken(Context context) {
        return context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE).getString(LAST_TOKEN, "");
    }
}
