package com.fastaccess.helper;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.fastaccess.App;
import com.fastaccess.BuildConfig;
import com.fastaccess.data.dao.AppsModel;
import com.fastaccess.provider.icon.IconCache;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by kosh20111 on 18 Oct 2016, 9:29 PM
 */

public class AppHelper {

    public static boolean isApplicationInstalled(Context context, String packageName) {
        PackageInfo info = null;
        try {
            info = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return info != null;
    }

    public static boolean isM() {return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;}

    public static boolean isLollipopOrHigher() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static void hideKeyboard(@NonNull View view) {
        hideKeyboard(view, view.getContext());
    }

    public static void hideKeyboard(@NonNull View view, @NonNull Context activity) {
        InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @NonNull public static List<AppsModel> getInstalledPackages(@NonNull Context context) {
        final PackageManager pm = context.getPackageManager();
        Process process;
        List<AppsModel> result = new ArrayList<>();
        BufferedReader bufferedReader = null;
        IconCache iconCache = App.getInstance().getIconCache();
        try {
            process = Runtime.getRuntime().exec("pm list packages");
            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                final String packageName = line.substring(line.indexOf(':') + 1);
                PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);
                Intent mainIntent = pm.getLaunchIntentForPackage(packageInfo.applicationInfo.packageName);
                if (mainIntent != null) {
                    ResolveInfo resolveInfo = pm.resolveActivity(mainIntent, 0);
                    if (resolveInfo != null) {
                        if (!packageName.equalsIgnoreCase(BuildConfig.APPLICATION_ID)) {
                            AppsModel model = new AppsModel();
                            model.setPackageName(resolveInfo.activityInfo.applicationInfo.packageName);
                            model.setActivityInfoName(resolveInfo.activityInfo.name);
                            model.setAppName(resolveInfo.loadLabel(pm).toString());
                            iconCache.getTitleAndIcon(model, resolveInfo, null);
                            result.add(model);
                        }
                    }
                }
            }
            process.waitFor();
            Collections.sort(result, AppsModel.sortApps());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null)
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return result;
    }

    @Nullable public static Fragment getFragmentByTag(@NonNull FragmentManager fragmentManager, @NonNull String tag) {
        return fragmentManager.findFragmentByTag(tag);
    }

    @Nullable public static Fragment getVisibleFragment(@NonNull FragmentManager manager) {
        List<Fragment> fragments = manager.getFragments();
        if (fragments != null && !fragments.isEmpty()) {
            for (Fragment fragment : fragments) {
                if (fragment != null && fragment.isVisible()) {
                    Logger.e(fragment.getClass().getSimpleName(), fragment.isVisible());
                    return fragment;
                }
            }
        }
        return null;
    }

    @Nullable public static String saveBitmap(Bitmap image) {
        try {
            File file = FileHelper.generateFile("fa_image_icon");
            if (file.exists()) {
                file.delete();
            }
            OutputStream fOut = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 70, fOut);
            fOut.flush();
            fOut.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("StringBufferReplaceableByString") @NonNull public static String getEmailBody() {
        return new StringBuilder()
                .append("Version Code: ").append(BuildConfig.VERSION_CODE)
                .append("\n")
                .append("Version Name: ").append(BuildConfig.VERSION_NAME)
                .append("\n")
                .append("OS Version: ").append(Build.VERSION.SDK_INT)
                .append("\n")
                .append("Manufacturer: ").append(Build.MANUFACTURER)
                .append("\n")
                .append("Phone Model: ").append(Build.MODEL)
                .append("\n")
                .append("--------------------------------------------")
                .append("\n")
                .toString();
    }

    public static void openAppInPlayStore(@NonNull Context context) {
        final String appPackageName = BuildConfig.APPLICATION_ID;
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }
}
