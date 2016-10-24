package com.fastaccess.helper;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Kosh on 19 Oct 2016, 6:55 PM
 */

public class PermissionsHelper {

    public static final int OVERLAY_PERMISSION_REQ_CODE = 1;

    @TargetApi(Build.VERSION_CODES.M) public static boolean isSystemAlertGranted(@NonNull Context context) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(context);
    }

    @TargetApi(Build.VERSION_CODES.M) public static boolean systemAlertPermissionIsGranted(@NonNull AppCompatActivity context) {
        if (!isSystemAlertGranted(context)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.getPackageName()));
            context.startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
            return false;
        }
        return true;
    }
}


