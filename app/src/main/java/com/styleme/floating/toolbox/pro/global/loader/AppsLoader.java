package com.styleme.floating.toolbox.pro.global.loader;


import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.styleme.floating.toolbox.pro.AppController;
import com.styleme.floating.toolbox.pro.global.helper.AppHelper;
import com.styleme.floating.toolbox.pro.global.helper.IconCache;
import com.styleme.floating.toolbox.pro.global.model.AppsModel;
import com.styleme.floating.toolbox.pro.global.receiver.ApplicationsReceiver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppsLoader extends AsyncTaskLoader<List<AppsModel>> {
    private static final String TAG = "AppsLoader";
    private ApplicationsReceiver mAppsObserver;
    private final PackageManager mPm;
    private List<AppsModel> mApps;
    private IconCache mIconCache;

    public AppsLoader(Context ctx) {
        super(ctx);
        mPm = getContext().getPackageManager();
        mIconCache = AppController.getController().getIconCache(false);
    }

    @Override
    public List<AppsModel> loadInBackground() {
        List<AppsModel> entries = new ArrayList<AppsModel>();
        try {
            Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            List<ResolveInfo> list = mPm.queryIntentActivities(mainIntent, 0);
            if (list == null) {
                list = new ArrayList<>();
            }
            Collections.sort(list, new ResolveInfo.DisplayNameComparator(mPm));
            for (ResolveInfo resolveInfo : list) {
                if (!resolveInfo.activityInfo.applicationInfo.packageName.equals(getContext().getPackageName())) {
                    AppsModel check = new AppsModel().getAppByPackage(resolveInfo.activityInfo.applicationInfo.packageName);
                    if (check == null) {
                        AppsModel model = new AppsModel(mPm, resolveInfo, mIconCache, null);
                        entries.add(model);
                    }
                }
            }
            return entries;
        } catch (Exception e) {
            e.printStackTrace();
            return AppHelper.getInstalledPackages(getContext(), mIconCache);
        }
    }

    @Override
    public void deliverResult(List<AppsModel> apps) {
        if (isReset()) {
            if (apps != null) {
                return;
            }
        }
        List<AppsModel> oldApps = mApps;
        mApps = apps;
        if (isStarted()) {
            super.deliverResult(apps);
        }
    }

    @Override
    protected void onStartLoading() {
        if (mApps != null) {
            deliverResult(mApps);
        }
        if (mAppsObserver == null) {
            mAppsObserver = new ApplicationsReceiver(this);
        }
        if (takeContentChanged()) {
            mIconCache = AppController.getController().getIconCache(true);
            forceLoad();
        } else if (mApps == null) {
            mIconCache = AppController.getController().getIconCache(true);
            forceLoad();
        }


    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    protected void onReset() {
        onStopLoading();
        if (mApps != null) {
            mApps = null;
        }
        if (mAppsObserver != null) {
            getContext().unregisterReceiver(mAppsObserver);
            mAppsObserver = null;
        }
    }

    @Override
    public void onCanceled(List<AppsModel> apps) {
        super.onCanceled(apps);
    }

    @Override
    public void forceLoad() {
        super.forceLoad();
    }


    private Comparator<PackageInfo> sortApps = new Comparator<PackageInfo>() {
        @Override
        public int compare(PackageInfo one, PackageInfo two) {
            return one.applicationInfo.loadLabel(mPm).toString().compareTo(two.applicationInfo.loadLabel(mPm).toString());
        }
    };

}
