package com.styleme.floating.toolbox.pro.global.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.styleme.floating.toolbox.pro.global.helper.AppHelper;
import com.styleme.floating.toolbox.pro.global.helper.Notifier;
import com.styleme.floating.toolbox.pro.widget.floating.FloatingHorizontalLayout;
import com.styleme.floating.toolbox.pro.widget.floating.FloatingLayout;

/**
 * Created by Kosh on 9/4/2015. copyrights are reserved
 */
public class FloatingService extends Service {


    private FloatingHorizontalLayout horizontal;
    private FloatingLayout vertical;

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
        setupPopup();
        return START_STICKY;
    }

    private void setupPopup() {
        if (AppHelper.isHorizontal(this)) {
            if (horizontal == null) {
                horizontal = new FloatingHorizontalLayout(this);
            }
        } else {
            if (vertical == null) {
                vertical = new FloatingLayout(this);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (horizontal != null) {horizontal.onDestroy();}
        if (vertical != null) vertical.onDestroy();
    }

}
