package com.fastaccess.helper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.fastaccess.R;
import com.fastaccess.provider.service.FloatingService;

/**
 * Created by kosh20111 on 9/8/2015. CopyRights @ Innov8tif
 */
public class NotificationHelper {

    public static final int NOTIFICATION_ID = 20111;

    public static void notifyShort(@NonNull Context context, @NonNull String title, @NonNull String msg, @DrawableRes int iconId) {
        notifyShort(context, title, msg, iconId, NOTIFICATION_ID, null);
    }

    public static void notifyShort(@NonNull Context context, @NonNull String title, @NonNull String msg, @DrawableRes int iconId,
                                   @NonNull PendingIntent pendingIntent) {
        notifyShort(context, title, msg, iconId, NOTIFICATION_ID, pendingIntent);
    }

    public static void notifyShort(@NonNull Context context, @NonNull String title, @NonNull String msg, @DrawableRes int iconId, int nId) {
        notifyShort(context, title, msg, iconId, nId, null);
    }

    public static void notifyShort(@NonNull Context context, @NonNull String title, @NonNull String msg, @DrawableRes int iconId, int nId,
                                   @Nullable PendingIntent pendingIntent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(context)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentTitle(title)
                .setContentText(msg)
                .setSmallIcon(iconId)
                .setContentIntent(pendingIntent)
                .build();
        notificationManager.notify(nId, notification);
    }

    public static void notifyShort(@NonNull Context context, @NonNull String title, String msg, @DrawableRes int iconId,
                                   @NonNull NotificationCompat.Action action) {
        notifyShort(context, title, msg, iconId, action, NOTIFICATION_ID);
    }

    public static void notifyShort(@NonNull Context context, @NonNull String title, String msg, @DrawableRes int iconId,
                                   @NonNull NotificationCompat.Action action, int nId) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(context)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentTitle(title)
                .setContentText(msg)
                .setSmallIcon(iconId)
                .addAction(action)
                .setContentIntent(action.actionIntent)
                .build();
        notificationManager.notify(nId, notification);
    }

    public static void notifyBig(@NonNull Context context, @NonNull String title, @NonNull String msg, @DrawableRes int iconId) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(context)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentText(msg)
                .setSmallIcon(iconId)
                .setStyle(new NotificationCompat.BigTextStyle().setBigContentTitle(title).setSummaryText(msg).bigText(msg))
                .build();
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    public static void notifyWithImage(@NonNull Context context, @NonNull String title, @NonNull String msg, @DrawableRes int iconId,
                                       @NonNull Bitmap bitmap) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(context)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentTitle(title)
                .setContentText(msg)
                .setSmallIcon(iconId)
                .setStyle(new NotificationCompat.BigPictureStyle().setBigContentTitle(title).setSummaryText(msg).bigPicture(bitmap))
                .build();
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    public static Notification getNonCancellableNotification(@NonNull Context content, @NonNull String title, @NonNull String msg,
                                                             @DrawableRes int iconId, @NonNull PendingIntent pendingIntent) {
        return new NotificationCompat.Builder(content)
                .setAutoCancel(false)
                .setOngoing(true)
                .setContentTitle(title)
                .setContentText(msg)
                .setSmallIcon(iconId)
                .setContentIntent(pendingIntent)
                .build();
    }

    public static void collapseFAService(Context context, int size) {
        context.stopService(new Intent(context, FloatingService.class));
        Intent notificationIntent = new Intent(context, FloatingService.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, notificationIntent, 0);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
        int icon = R.drawable.ic_fa_notification;
        long finalTime = System.currentTimeMillis();
        if (PrefHelper.getBoolean(PrefConstant.STATUS_BAR_HIDDEN)) {
            icon = R.drawable.ic_notification;
        }
        notificationBuilder
                .setPriority(Notification.PRIORITY_LOW)
                .setWhen(finalTime)
                .setSmallIcon(icon)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.click_to_start_service))
                .setNumber(size)
                .setAutoCancel(false)
                .setOngoing(true);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationBuilder.setContentIntent(pendingIntent);
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    public static void cancelNotification(@NonNull Context context, int id) {
        int finalId = id == 0 ? NOTIFICATION_ID : id;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(finalId);
    }

    public static void cancelAllNotifications(@NonNull Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }
}
