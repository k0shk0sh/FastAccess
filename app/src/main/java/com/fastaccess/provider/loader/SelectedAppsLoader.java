package com.fastaccess.provider.loader;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v4.content.AsyncTaskLoader;

import com.fastaccess.App;
import com.fastaccess.data.dao.AppsModel;
import com.fastaccess.provider.icon.IconCache;
import com.fastaccess.provider.receiver.ApplicationsReceiver;

import java.util.ArrayList;
import java.util.List;

public class SelectedAppsLoader extends AsyncTaskLoader<List<AppsModel>> {
    private ApplicationsReceiver mAppsObserver;
    private final PackageManager packageManager;
    private List<AppsModel> appsModelList;
    private long folderId = -1;

    public SelectedAppsLoader(Context ctx) {
        super(ctx);
        packageManager = getContext().getPackageManager();
    }

    public SelectedAppsLoader(Context ctx, long folderId) {
        super(ctx);
        this.folderId = folderId;
        packageManager = getContext().getPackageManager();
    }

    @Override public List<AppsModel> loadInBackground() {
        List<AppsModel> savedApps = folderId == -1 ? AppsModel.getApps() : AppsModel.getApps(folderId);
        if (savedApps == null || savedApps.isEmpty()) return new ArrayList<>();
        IconCache iconCache = App.getInstance().getIconCache();
        for (AppsModel model : savedApps) {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(model.getPackageName(), model.getActivityInfoName()));
            ResolveInfo resolveInfo = packageManager.resolveActivity(intent, 0);
            if (resolveInfo != null) {
                iconCache.getTitleAndIcon(model, resolveInfo, null);
            } else {
                model.delete();//app is uninstalled!
            }
        }
        return savedApps;
    }

    @Override public void deliverResult(List<AppsModel> apps) {
        if (isReset()) {
            if (apps != null) {
                return;
            }
        }
        appsModelList = apps;
        if (isStarted()) {
            super.deliverResult(apps);
        }
    }

    @Override protected void onStartLoading() {
        if (appsModelList != null && !appsModelList.isEmpty()) {
            deliverResult(appsModelList);
        }
        if (mAppsObserver == null) {
            mAppsObserver = new ApplicationsReceiver(this);
        }
        if (takeContentChanged()) {
            forceLoad();
        } else if (appsModelList == null) {
            forceLoad();
        }


    }

    @Override protected void onStopLoading() {
        cancelLoad();
    }

    @Override protected void onReset() {
        onStopLoading();
        if (appsModelList != null) {
            appsModelList = null;
        }
        if (mAppsObserver != null) {
            getContext().unregisterReceiver(mAppsObserver);
            mAppsObserver = null;
        }
    }

    @Override public void onCanceled(List<AppsModel> apps) {
        super.onCanceled(apps);
    }

    @Override public void forceLoad() {
        if (appsModelList != null) appsModelList.clear();
        super.forceLoad();
    }
}
