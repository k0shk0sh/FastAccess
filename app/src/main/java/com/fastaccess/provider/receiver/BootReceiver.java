package com.fastaccess.provider.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.fastaccess.helper.PermissionsHelper;
import com.fastaccess.helper.PrefConstant;
import com.fastaccess.provider.service.FloatingService;

public class BootReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context context, Intent intent) {
        if (PrefConstant.isAutoStart() && PermissionsHelper.isSystemAlertGranted(context)) {
            if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
                Intent serviceIntent = new Intent(context, FloatingService.class);
                context.startService(serviceIntent);
            }
        }
    }
}
