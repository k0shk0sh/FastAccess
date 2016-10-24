package com.fastaccess.helper;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.Fragment;

import com.fastaccess.R;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;

import java.io.File;

/**
 * Created by Kosh on 12/12/15 10:51 PM
 */
public class ActivityHelper {

    public static final int REQUEST_CODE = 100;
    public static final int CAMERA_REQUEST_CODE = 101;
    public static final int SELECT_PHOTO_REQUEST = 102;

    @Nullable public static Activity getActivity(@Nullable Context cont) {
        if (cont == null) return null;
        else if (cont instanceof Activity) return (Activity) cont;
        else if (cont instanceof ContextWrapper) return getActivity(((ContextWrapper) cont).getBaseContext());
        return null;
    }

    public static int getScreenOrientation(Context activity) {
        return activity.getResources().getConfiguration().orientation;
    }

    public static void startActivity(@NonNull Context context, Class className) {
        Intent intent = new Intent(context, className);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public static void startActivityWithFinish(@NonNull Activity context, Class className) {
        Intent intent = new Intent(context, className);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public static void startGalleryIntent(@NonNull Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        activity.startActivityForResult(intent, SELECT_PHOTO_REQUEST);
    }

    public static void startGalleryIntent(@NonNull Fragment fragment) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        fragment.startActivityForResult(intent, SELECT_PHOTO_REQUEST);
    }
    public static void startCameraIntent(@NonNull Activity activity, @NonNull File file) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
            activity.startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
        }
    }

    public static void startCustomTab(@NonNull Activity context) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(ViewHelper.getPrimaryColor(context));
        CustomTabsIntent tabsIntent = builder.build();
        tabsIntent.launchUrl(context, Uri.parse("https://github.com/k0shk0sh/NewKam"));//TODO change url
    }

    public static void startLibs(@NonNull Activity activity) {
        new LibsBuilder()
                .withFields(R.string.class.getFields())
                .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                .withActivityTheme(R.style.AppTheme)
                .withAboutIconShown(true)
                .withAboutVersionShown(true)
                .withAutoDetect(true)
                .withLicenseShown(true)
                .withVersionShown(true)
                .withActivityTitle(activity.getString(R.string.libs))
                .start(activity);
    }

}
