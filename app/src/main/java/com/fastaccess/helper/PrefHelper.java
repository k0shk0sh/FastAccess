package com.fastaccess.helper;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.App;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kosh20111 on 18 Oct 2016, 9:29 PM
 */
public class PrefHelper {
    

    /**
     * @param key
     *         ( the Key to used to retrieve this data later  )
     * @param value
     *         ( any kind of primitive values  )
     *         <p/>
     *         non can be null!!!
     */
    public static void set(@NonNull String key, @NonNull Object value) {
        if (InputHelper.isEmpty(key)) {
            throw new NullPointerException("Key must not be null! (key = " + key + "), (value = " + value + ")");
        }
        Logger.e(key, value);
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(App.getInstance()).edit();
        if (value instanceof String) {
            edit.putString(key, (String) value);
        } else if (value instanceof Integer || value instanceof Long || value instanceof Float) {
            edit.putInt(key, Integer.parseInt(value.toString()));
        } else if (value instanceof Boolean) {
            edit.putBoolean(key, (boolean) value);
        } else {
            edit.putString(key, value.toString());
        }
        edit.apply();
    }

    @Nullable public static String getString(@NonNull String key) {
        return PreferenceManager.getDefaultSharedPreferences(App.getInstance()).getString(key, null);
    }

    public static boolean getBoolean(@NonNull String key) {
        return PreferenceManager.getDefaultSharedPreferences(App.getInstance()).getBoolean(key, false);
    }

    public static int getInt(@NonNull String key) {
        return PreferenceManager.getDefaultSharedPreferences(App.getInstance()).getInt(key, 0);
    }

    public static void clearKey(@NonNull String key) {
        PreferenceManager.getDefaultSharedPreferences(App.getInstance()).edit().remove(key).apply();
    }

    public static boolean isExist(@NonNull String key) {
        return PreferenceManager.getDefaultSharedPreferences(App.getInstance()).contains(key);
    }

    public static void clearPrefs() {
        PreferenceManager.getDefaultSharedPreferences(App.getInstance()).edit().clear().apply();
    }

    public static Map<String, Object> getAll() {
        Map<String, Object> toBackupMap = new HashMap<>();
        Map<String, ?> prefs = PreferenceManager.getDefaultSharedPreferences(App.getInstance()).getAll();
        for (String key : prefs.keySet()) {
            if (!InputHelper.isEmpty(key) && !key.equalsIgnoreCase("null")) {// sometimes key is null, for no fucking reason.
                toBackupMap.put(key, prefs.get(key));
            }
        }
        return toBackupMap;
    }
}
