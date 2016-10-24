package com.fastaccess.provider.loader;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v4.content.AsyncTaskLoader;

import com.fastaccess.App;
import com.fastaccess.BuildConfig;
import com.fastaccess.data.dao.AppsModel;
import com.fastaccess.helper.AppHelper;
import com.fastaccess.provider.icon.IconCache;
import com.fastaccess.provider.receiver.ApplicationsReceiver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeviceAppsLoader extends AsyncTaskLoader<List<AppsModel>> {
    private ApplicationsReceiver mAppsObserver;
    private final PackageManager packageManager;
    private List<AppsModel> appsModelList;

    public DeviceAppsLoader(Context ctx) {
        super(ctx);
        packageManager = getContext().getPackageManager();
    }

    @Override public List<AppsModel> loadInBackground() {
        try {
            List<AppsModel> entries = new ArrayList<>();
            Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> list = packageManager.queryIntentActivities(mainIntent, 0);
            if (list == null || list.isEmpty()) {
                return entries;
            }
            Collections.sort(list, new ResolveInfo.DisplayNameComparator(packageManager));
            String appPackage = BuildConfig.APPLICATION_ID;
            IconCache iconCache = App.getInstance().getIconCache();
            for (ResolveInfo resolveInfo : list) {
                if (!resolveInfo.activityInfo.applicationInfo.packageName.equalsIgnoreCase(appPackage)) {
                    AppsModel model = new AppsModel();
                    model.setPackageName(resolveInfo.activityInfo.applicationInfo.packageName);
                    model.setActivityInfoName(resolveInfo.activityInfo.name);
                    iconCache.getTitleAndIcon(model, resolveInfo, null);
                    entries.add(model);
                }
            }
            return entries;
        } catch (Exception e) {//catching TransactionTooLargeException,
            e.printStackTrace();
            return AppHelper.getInstalledPackages(getContext());
        }
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
