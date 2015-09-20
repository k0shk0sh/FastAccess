package com.styleme.floating.toolbox.pro.global.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.styleme.floating.toolbox.pro.global.helper.Notifier;
import com.styleme.floating.toolbox.pro.widget.FloatingLayout;

/**
 * Created by Kosh on 9/4/2015. copyrights are reserved
 */
public class FloatingService extends Service {


    private FloatingLayout floatingLayout;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startForeground(Notifier.FORGROUND_NOTIFICATION, Notifier.foregroundNotification(this));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (floatingLayout == null) {
            floatingLayout = new FloatingLayout(this);
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floatingLayout != null) {
            floatingLayout.onDestroy();
        }
    }
}
