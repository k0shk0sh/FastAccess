package com.styleme.floating.toolbox.pro.global.helper;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.TypedValue;
import android.webkit.MimeTypeMap;

import com.styleme.floating.toolbox.pro.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Kosh on 8/17/2015. copyrights are reserved
 */
public class AppHelper {

    private static final String folderName = Environment.getExternalStorageDirectory() + "/Fast Access/";
    public static final String ICON_PACK = "fa_icon_pack";

    public static Bitmap getBitmap(Context context, String packageName) {
        try {
            return drawableToBitmap(context.getPackageManager().getApplicationIcon(packageName));
        } catch (PackageManager.NameNotFoundException e) {
            if (isLollipop()) {
                return drawableToBitmap(context.getDrawable(R.drawable.ic_not_found));
            }
            return drawableToBitmap(context.getResources().getDrawable(R.drawable.ic_not_found));
        }
    }

    public static Drawable getDrawable(Context context, String packageName) {
        try {
            return context.getPackageManager().getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            if (isLollipop()) {
                return context.getDrawable(R.drawable.ic_not_found);
            }
            return context.getResources().getDrawable(R.drawable.ic_not_found);
        }
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        return ((BitmapDrawable) drawable).getBitmap();
    }

    public static boolean isLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static long getFolderSize(File f) {
        long size = 0;
        if (f.isDirectory()) {
            for (File file : f.listFiles()) {
                size += getFolderSize(file);
            }
        } else {
            size = f.length();
        }
        return size;
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static boolean isDarkTheme(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("dark_theme", false);
    }

    public static int getAccentColor(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt("accent_color", context.getResources().getColor(R.color.accent));
    }

    public static int getPrimaryColor(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt("primary_color", context.getResources().getColor(R.color.primary));
    }

    public static int getPrimaryDarkColor(int color) {
        double tran = 0.8;
        int a = Color.alpha(color);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        return Color.argb(a, Math.max((int) (r * tran), 0), Math.max((int) (g * tran), 0), Math.max((int) (b * tran), 0));
    }

    public static int getAlpha(int color) {
        double tran = 0.9;
        int a = Color.alpha(color);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        return Color.argb(a, Math.max((int) (r * tran), 0), Math.max((int) (g * tran), 0), Math.max((int) (b * tran), 0));
    }

    public static String prettifyDate(long timestamp) {
        SimpleDateFormat dateFormat;
        if (DateUtils.isToday(timestamp)) {
            dateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        } else {
            dateFormat = new SimpleDateFormat("dd MMM hh:mm a", Locale.getDefault());
        }
        return dateFormat.format(timestamp);
    }

    public static boolean isBackupData(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("backup_data", false);
    }

    public static boolean isRestoreData(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("restore_data", false);
    }

    public static String extension(String file) {
        return MimeTypeMap.getFileExtensionFromUrl(file);
    }

    private static Drawable getColorDrawable(int colorCode) {
        return new ColorDrawable(colorCode);
    }

    public static StateListDrawable selector(int color) {
        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[]{android.R.attr.state_pressed}, getColorDrawable(color));
        drawable.addState(new int[]{android.R.attr.state_focused}, getColorDrawable(color));
        drawable.addState(new int[]{android.R.attr.state_selected}, getColorDrawable(color));
        drawable.addState(new int[]{android.R.attr.state_activated}, getColorDrawable(color));
        return drawable;
    }

    public static void setHasSeenWhatsNew(Context context) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("guide_showed", true).apply();
    }

    public static boolean hasSeenWhatsNew(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("guide_showed", false);
    }

    public static boolean isStatusBarIconHidden(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("status_bar_hidden", false);
    }

    public static boolean isEdged(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("edges", false);
    }

    public static String getImage(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("image_path", null);
    }

    public static void setImage(Context context, String path) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("image_path", path).apply();
    }

    public static void clearImage(Context context) {
        if (getImage(context) != null) {
            deleteFile(getImage(context));
        }
        PreferenceManager.getDefaultSharedPreferences(context).edit().remove("image_path").apply();
    }

    public static void deleteFile(String imageLocation) {
        if (imageLocation != null && imageLocation.trim().length() > 5) {
            File file = new File(imageLocation);
            if (file.exists()) {
                boolean fi = file.delete();
            }
        }
    }

    public static String getJpgImagePath(String path) {
        return path + ".jpg";
    }

    public static String generateFileName() {
        return "FA-Image.jpg";
    }

    public static File folderName() {
        File file = new File(folderName);
        if (!file.exists())
            file.mkdir();
        return file;
    }

    public static File getFinalFile() {
        File file = new File(folderName(), ".nomedia");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                file.mkdir();
            }
        }
        return new File(folderName(), generateFileName());
    }

    public static File generateBackupFile() {
        File file = new File(folderName(), "FA-BACKUP.json");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                file.mkdir();
            }
        }
        return file;
    }

    public static File getBackupFile() {
        return new File(folderName(), "FA-BACKUP.json");
    }

    public static String saveBitmap(Bitmap image) {
        try {
            File file = getFinalFile();
            if (file.exists()) {
                file.delete();
            }
            OutputStream fOut = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            Log.e("saveToExternalStorage()", "saveBitmap", e);
            return null;
        }
    }

    public static void putIconPack(Context context, String pack) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(ICON_PACK, pack).apply();
    }

    public static String getIconPack(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(ICON_PACK, "");
    }

    public static String getIconSize(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("size", "medium");
    }

    public static String getGapSize(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("gap", "medium");
    }

    public static boolean isSavePositionEnabled(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("savePosition", false);
    }

    public static void savePosition(Context context, int positionY, int positionX) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt("fa_positionY", positionY).apply();
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt("fa_positionX", positionX).apply();
    }

    public static int getPositionY(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt("fa_positionY", 0);
    }

    public static int getPositionX(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt("fa_positionX", 0);
    }

    public static boolean isAutoTransparent(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("autoTrans", true);
    }

    public static Map<String, ?> getAllPrefs(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getAll();
    }

    public static void setFaIconSize(Context context, int size) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt("fa_icon_size", size).apply();
    }

    public static int getFaIconSize(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt("fa_icon_size", 20);
    }

    public static boolean isManualSize(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("manualSize", false);
    }

    public static int toPx(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static boolean isAutoStart(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("autoStart", true);
    }

}
