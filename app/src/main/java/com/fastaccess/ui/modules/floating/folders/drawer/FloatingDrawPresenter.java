package com.fastaccess.ui.modules.floating.folders.drawer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.content.Loader;
import android.view.View;

import com.fastaccess.data.dao.AppsModel;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.List;

/**
 * Created by Kosh on 22 Oct 2016, 3:13 PM
 */

public class FloatingDrawPresenter extends BasePresenter<FloatingDrawerMvp.View> implements FloatingDrawerMvp.Presenter {

    protected FloatingDrawPresenter(@NonNull FloatingDrawerMvp.View view) {
        super(view);
    }

    public static FloatingDrawPresenter with(@NonNull FloatingDrawerMvp.View view) {
        return new FloatingDrawPresenter(view);
    }

    @Override public void onLoadComplete(Loader<List<AppsModel>> loader, List<AppsModel> data) {
        if (isAttached()) getView().onAppsLoaded(data);
    }

    @Override public void onItemClick(int position, View v, AppsModel item) {
        try {
            Context context = v.getContext();
            PackageManager manager = context.getPackageManager();
            Intent intent = manager.getLaunchIntentForPackage(item.getPackageName());
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setComponent(new ComponentName(item.getPackageName(), item.getActivityInfoName()));
            context.startActivity(intent);
        } catch (Exception e) {// app uninstalled/not found
            e.printStackTrace();
            item.delete();
        }
        if (isAttached()) getView().onTouchedOutside();
    }

    @Override public void onItemLongClick(int position, View v, AppsModel item) {
        onItemClick(position, v, item);
    }
}
