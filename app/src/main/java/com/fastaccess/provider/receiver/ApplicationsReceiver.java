package com.fastaccess.provider.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.Loader;

import static android.content.Intent.ACTION_PACKAGE_REMOVED;
import static android.content.Intent.ACTION_UNINSTALL_PACKAGE;

/**
 * Created by kosh on 18 Oct 2016, 9:33 PM
 */
public class ApplicationsReceiver extends BroadcastReceiver {

    private final static IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);

    static {
        filter.addAction(ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addAction(Intent.ACTION_INSTALL_PACKAGE);
        filter.addAction(ACTION_UNINSTALL_PACKAGE);
        filter.addDataScheme("package");
    }

    private Loader loader;

    public ApplicationsReceiver(Loader loader) {
        this.loader = loader;
        this.loader.getContext().registerReceiver(this, filter);
    }

    @Override public void onReceive(Context context, Intent intent) {
        boolean isReplacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false);
        if (isReplacing) return;
        if (loader != null) loader.onContentChanged();
    }
}
