package com.styleme.floating.toolbox.pro.global.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.styleme.floating.toolbox.pro.global.loader.AppsLoader;

/**
 * Created by kosh on 12/12/2014. CopyRights @ styleme
 */
public class ApplicationsReceiver extends BroadcastReceiver {
    public static String DATA_CHANGED = "dataChanged";
    private AppsLoader mLoader;

    public ApplicationsReceiver() {}

    public ApplicationsReceiver(AppsLoader loader) {
        mLoader = loader;
        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addDataScheme("package");
        mLoader.getContext().registerReceiver(this, filter);
        mLoader.getContext().registerReceiver(this, new IntentFilter(DATA_CHANGED));
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isReplacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false);
        if (isReplacing) {
            return;
        }
        mLoader.onContentChanged();

    }
}
