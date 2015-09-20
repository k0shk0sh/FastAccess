package com.styleme.floating.toolbox.pro.global.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.styleme.floating.toolbox.pro.global.loader.MyAppsLoader;
import com.styleme.floating.toolbox.pro.global.loader.MyPopupAppsLoader;

/**
 * Created by kosh on 12/12/2014. CopyRights @ styleme
 */
public class MyAppsReceiver extends BroadcastReceiver {
    public static String DATA_ADDED = "dataAdded";
    public static String DATE_DELETE = "onDataDeleted";
    public static String REARRANGED = "rearrange";
    private MyAppsLoader mLoader;
    private MyPopupAppsLoader popupAppsLoader;

    public MyAppsReceiver() {}

    public MyAppsReceiver(MyAppsLoader loader) {
        mLoader = loader;
        IntentFilter databaseChange = new IntentFilter(DATA_ADDED);
        mLoader.getContext().registerReceiver(this, databaseChange);
        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addDataScheme("package");
        mLoader.getContext().registerReceiver(this, filter);
    }

    public MyAppsReceiver(MyPopupAppsLoader loader) {
        popupAppsLoader = loader;
        IntentFilter databaseChange = new IntentFilter(DATA_ADDED);
        popupAppsLoader.getContext().registerReceiver(this, databaseChange);
        IntentFilter onDelete = new IntentFilter(DATE_DELETE);
        popupAppsLoader.getContext().registerReceiver(this, onDelete);
        popupAppsLoader.getContext().registerReceiver(this, new IntentFilter(REARRANGED));
        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addDataScheme("package");
        popupAppsLoader.getContext().registerReceiver(this, filter);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isReplacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false);
        if (isReplacing) {
            return;
        }
        if (intent.getAction().equalsIgnoreCase(REARRANGED)) {
            if (popupAppsLoader != null) popupAppsLoader.onContentChanged();
        } else {
            if (mLoader != null) mLoader.onContentChanged();
            if (popupAppsLoader != null) popupAppsLoader.onContentChanged();
        }
    }
}
