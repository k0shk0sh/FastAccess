package com.fastaccess.provider.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.fastaccess.R;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.NotificationHelper;
import com.fastaccess.helper.PrefConstant;
import com.fastaccess.helper.PrefHelper;
import com.fastaccess.ui.modules.floating.apps.FloatingVHView;
import com.fastaccess.ui.modules.floating.folders.FloatingFoldersView;
import com.fastaccess.ui.modules.main.MainView;

/**
 * Created by Kosh on 13 Oct 2016, 7:32 PM
 */

public class FloatingService extends Service {
    private FloatingVHView floatingVHView;
    private FloatingFoldersView floatingFoldersView;

    @Override public void onCreate() {
        super.onCreate();
        String floatingMode = PrefHelper.getString(PrefConstant.FLOATING_MODE);
        if (!InputHelper.isEmpty(floatingMode)) {
            switch (floatingMode) {
                case "Apps":
                    floatingVHView = FloatingVHView.with(this, PrefConstant.isHorizontal());
                    break;
                case "Folders":
                    floatingFoldersView = FloatingFoldersView.with(this, PrefConstant.isHorizontal());
                    break;
            }
        } else {
            floatingVHView = FloatingVHView.with(this, PrefConstant.isHorizontal());
        }
        startForeground(NotificationHelper.NOTIFICATION_ID, NotificationHelper.getNonCancellableNotification(this,
                getString(R.string.app_name), getString(R.string.click_to_open_fa),
                PrefHelper.getBoolean(PrefConstant.STATUS_BAR_HIDDEN) ? R.drawable.ic_notification : R.drawable.ic_fa_notification,
                PendingIntent.getActivity(this, 0, new Intent(this, MainView.class), PendingIntent.FLAG_UPDATE_CURRENT)));
    }

    @Nullable @Override public IBinder onBind(Intent intent) {
        return null;
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override public void onTaskRemoved(Intent rootIntent) {
        PendingIntent service = PendingIntent.getService(getApplicationContext(), 1001,
                new Intent(getApplicationContext(), FloatingService.class), PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 3000, service);
        super.onTaskRemoved(rootIntent);
    }

    @Override public void onDestroy() {
        super.onDestroy();
        if (floatingVHView != null) floatingVHView.onDestroy();
        if (floatingFoldersView != null) floatingFoldersView.onDestroy();
    }
}
