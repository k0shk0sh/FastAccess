package com.styleme.floating.toolbox.pro.global.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.styleme.floating.toolbox.pro.global.helper.AppHelper;
import com.styleme.floating.toolbox.pro.global.model.AppsModel;
import com.styleme.floating.toolbox.pro.global.service.FloatingService;

public class AutoStart extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (AppHelper.isAutoStart(context)) {
            AppsModel database = new AppsModel();
            if (database.countAll() != 0) {
                if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
                    Intent serviceIntent = new Intent(context, FloatingService.class);
                    context.startService(serviceIntent);
                }
            }
        }
    }
}
