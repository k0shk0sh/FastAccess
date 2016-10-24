package com.fastaccess.helper;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.Fragment;

import com.fastaccess.R;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;

/**
 * Created by Kosh on 12/12/15 10:51 PM
 */
public class ActivityHelper {

    public static final int SELECT_PHOTO_REQUEST = 102;

    @Nullable public static Activity getActivity(@Nullable Context cont) {
        if (cont == null) return null;
        else if (cont instanceof Activity) return (Activity) cont;
        else if (cont instanceof ContextWrapper) return getActivity(((ContextWrapper) cont).getBaseContext());
        return null;
    }

    public static void startGalleryIntent(@NonNull Fragment fragment) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        fragment.startActivityForResult(intent, SELECT_PHOTO_REQUEST);
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
