package com.styleme.floating.toolbox.pro.global.helper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import com.styleme.floating.toolbox.pro.R;
import com.styleme.floating.toolbox.pro.activities.Home;
import com.styleme.floating.toolbox.pro.global.service.FloatingService;

/**
 * Created by Kosh on 9/4/2015. copyrights are reserved
 */
public class Notifier {

    public static int FORGROUND_NOTIFICATION = 1001;

    public static Notification foregroundNotification(Context context) {
        Intent showTaskIntent = new Intent(context, Home.class);
        showTaskIntent.setAction(Intent.ACTION_MAIN);
        showTaskIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        showTaskIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, showTaskIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        int icon = R.drawable.ic_home;
        long finalTime = System.currentTimeMillis();
        if (AppHelper.isStatusBarIconHidden(context)) {
            icon = R.drawable.ic_notification;
        }
        return new NotificationCompat.Builder(context)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.manage))
                .setSmallIcon(icon)
                .setWhen(finalTime)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setOngoing(true)
                .build();
    }

    public static void createNotification(Context context, int size) {
        context.stopService(new Intent(context, FloatingService.class));
        Intent notificationIntent = new Intent(context, FloatingService.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, notificationIntent, 0);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
        int icon = R.drawable.ic_home;
        long finalTime = System.currentTimeMillis();
        if (AppHelper.isStatusBarIconHidden(context)) {
            icon = R.drawable.ic_notification;
        }
        notificationBuilder
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setWhen(finalTime)
                .setSmallIcon(icon)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.click_to_start))
                .setNumber(size)
                .setAutoCancel(true)
                .setOngoing(true);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationBuilder.setContentIntent(pendingIntent);
        notificationManager.notify(FORGROUND_NOTIFICATION, notificationBuilder.build());
    }


    public static void cancelNotification(Context context) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) context.getSystemService(ns);
        nMgr.cancel(FORGROUND_NOTIFICATION);
    }
}
