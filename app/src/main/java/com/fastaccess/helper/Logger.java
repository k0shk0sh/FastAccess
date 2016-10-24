package com.fastaccess.helper;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

import static com.fastaccess.helper.GsonHelper.gson;

/**
 * Created by Kosh on 04/12/15 11:52 PM. copyrights @ Innov8tif
 */
public class Logger {

    private final static String TAG = "Logger";

    private static void e(@NonNull String tag, @Nullable Object text) {
        Log.e(tag, text != null ? text.toString() : "LOGGER IS NULL");//avoid null
    }

    private static void d(@NonNull String tag, @Nullable Object text) {
        Log.d(tag, text != null ? text.toString() : "LOGGER IS NULL");//avoid null
    }

    private static void i(@NonNull String tag, @Nullable Object text) {
        Log.i(tag, text != null ? text.toString() : "LOGGER IS NULL");//avoid null
    }

    public static void e(@Nullable Object text) {e(getCurrentClassName() + " || " + getCurrentMethodName(), text);}

    public static void d(@Nullable Object text) {
        d(getCurrentClassName() + " || " + getCurrentMethodName(), text);//avoid null
    }

    public static void i(@Nullable Object text) {
        i(getCurrentClassName() + " || " + getCurrentMethodName(), text);//avoid null
    }

    public static void e(Object... objects) {
        if (objects != null && objects.length > 0) {
            e(getCurrentClassName() + " || " + getCurrentMethodName(), Arrays.toString(objects));
        } else {
            e(getCurrentClassName() + " || " + getCurrentMethodName(), getCurrentMethodName());
        }
    }

    public static void e(List<Object> objects) {
        if (objects != null) {
            e(getCurrentClassName() + " || " + getCurrentMethodName(), Arrays.toString(objects.toArray()));
        } else {
            e(TAG, null);
        }
    }

    public static void longE(@NonNull Object text) {
        String veryLongString = text.toString();
        int maxLogSize = 4000;
        for (int i = 0; i <= veryLongString.length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = (i + 1) * maxLogSize;
            end = end > veryLongString.length() ? veryLongString.length() : end;
            e(getCurrentClassName() + " || " + getCurrentMethodName(), veryLongString.substring(start, end));
        }

    }

    public static void eJson(Object object) {
        e(getCurrentClassName() + " || " + getCurrentMethodName(), gson().toJson(object));
    }

    private static String getCurrentMethodName() {
        try {
            return Thread.currentThread().getStackTrace()[4].getMethodName() + "()";
        } catch (Exception ignored) {}
        return TAG;
    }

    private static String getCurrentLineNumber() {
        try {
            return " :" + Thread.currentThread().getStackTrace()[4].getLineNumber();
        } catch (Exception ignored) {}
        return TAG;
    }

    private static String getCurrentClassName() {
        try {
            String className = Thread.currentThread().getStackTrace()[4].getClassName();
            String[] temp = className.split("[\\.]");
            className = temp[temp.length - 1];
            return className;
        } catch (Exception ignored) {}
        return TAG;
    }
}
