package com.fastaccess.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kosh20111 on 18 Oct 2016, 9:29 PM
 */
public class PrefHelper {
    private static PrefHelper prefHelper;
    private Context context;

    private PrefHelper(Context context) {
        this.context = context;
    }

    public static void init(@NonNull Context context) {
        if (prefHelper == null) {
            prefHelper = new PrefHelper(context.getApplicationContext());
        }
    }

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
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(prefHelper.context).edit();
        if (value instanceof String) {
            edit.putString(key, (String) value);
        } else if (value instanceof Integer) {
            edit.putInt(key, (int) value);
        } else if (value instanceof Long) {
            edit.putLong(key, (long) value);
        } else if (value instanceof Boolean) {
            edit.putBoolean(key, (boolean) value);
        } else if (value instanceof Float) {
            edit.putFloat(key, (float) value);
        } else {
            edit.putString(key, value.toString());
        }
        edit.apply();
    }

    @Nullable public static String getString(@NonNull String key) {
        return PreferenceManager.getDefaultSharedPreferences(prefHelper.context).getString(key, null);
    }

    public static boolean getBoolean(@NonNull String key) {
        return PreferenceManager.getDefaultSharedPreferences(prefHelper.context).getBoolean(key, false);
    }

    public static int getInt(@NonNull String key) {
        return PreferenceManager.getDefaultSharedPreferences(prefHelper.context).getInt(key, 0);
    }

    public static long getLong(@NonNull String key) {
        return PreferenceManager.getDefaultSharedPreferences(prefHelper.context).getLong(key, 0);
    }

    public static float getFloat(@NonNull String key) {
        return PreferenceManager.getDefaultSharedPreferences(prefHelper.context).getFloat(key, 0);
    }

    public static void clearKey(@NonNull String key) {
        PreferenceManager.getDefaultSharedPreferences(prefHelper.context).edit().remove(key).apply();
    }

    public static boolean isExist(@NonNull String key) {
        return PreferenceManager.getDefaultSharedPreferences(prefHelper.context).contains(key);
    }

    public static void clearPrefs() {
        PreferenceManager.getDefaultSharedPreferences(prefHelper.context).edit().clear().apply();
    }

    public static Map<String, Object> getAll() {
        Map<String, Object> toBackupMap = new HashMap<>();
        Map<String, ?> prefs = PreferenceManager.getDefaultSharedPreferences(prefHelper.context).getAll();
        for (String key : prefs.keySet()) {
            if (!InputHelper.isEmpty(key) && !key.equalsIgnoreCase("null")) {// sometimes key is null, for no fucking reason.
                toBackupMap.put(key, prefs.get(key));
            }
        }
        return toBackupMap;
    }
}
