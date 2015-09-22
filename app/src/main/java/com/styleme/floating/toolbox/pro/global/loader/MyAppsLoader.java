package com.styleme.floating.toolbox.pro.global.loader;


import android.content.AsyncTaskLoader;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.styleme.floating.toolbox.pro.AppController;
import com.styleme.floating.toolbox.pro.global.helper.IconCache;
import com.styleme.floating.toolbox.pro.global.model.AppsModel;
import com.styleme.floating.toolbox.pro.global.receiver.MyAppsReceiver;

import java.util.ArrayList;
import java.util.List;

public class MyAppsLoader extends AsyncTaskLoader<List<AppsModel>> {
    private static final String TAG = "AppsLoader";
    private MyAppsReceiver mAppsObserver;
    private final PackageManager mPm;
    private List<AppsModel> mApps;
    private IconCache mIconCache;

    public MyAppsLoader(Context ctx) {
        super(ctx);
        mPm = getContext().getPackageManager();
        mIconCache = AppController.getController().getIconCache(false);
    }

    @Override
    public List<AppsModel> loadInBackground() {
        AppListCreator appListCreator = new AppListCreator();
        List<AppsModel> entries = new ArrayList<>();
        for (AppsModel model : appListCreator.getAppList(getContext())) {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(model.getPackageName(), model.getActivityInfoName()));
            ResolveInfo app = mPm.resolveActivity(intent, 0);
            if (app != null) {
                model.DoIt(mPm, app, mIconCache, null);
                entries.add(model);
            } else {
                model.delete();
            }
        }
        return entries;
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
            mAppsObserver = new MyAppsReceiver(this);
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

}
