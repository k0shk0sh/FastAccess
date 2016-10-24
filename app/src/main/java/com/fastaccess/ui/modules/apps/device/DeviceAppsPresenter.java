package com.fastaccess.ui.modules.apps.device;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.fastaccess.App;
import com.fastaccess.R;
import com.fastaccess.data.dao.AppsModel;
import com.fastaccess.data.dao.FloatingEventModel;
import com.fastaccess.data.dao.SelectedAppsEventModel;
import com.fastaccess.helper.Logger;
import com.fastaccess.provider.loader.DeviceAppsLoader;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * Created by Kosh on 10 Oct 2016, 11:45 PM
 */

public class DeviceAppsPresenter extends BasePresenter<DeviceAppsMvp.View> implements DeviceAppsMvp.Presenter {

    protected DeviceAppsPresenter(@NonNull DeviceAppsMvp.View view) {
        super(view);
    }

    public static DeviceAppsPresenter with(@NonNull DeviceAppsMvp.View view) {
        return new DeviceAppsPresenter(view);
    }

    @Override public Loader<List<AppsModel>> onCreateLoader(int id, Bundle args) {
        if (isAttached()) getView().onStartLoading();
        return new DeviceAppsLoader(App.getInstance().getApplicationContext());
    }

    @Override public void onLoadFinished(Loader<List<AppsModel>> loader, List<AppsModel> data) {
        getView().onAppsLoaded(data);
    }

    @Override public void onLoaderReset(Loader<List<AppsModel>> loader) {
        if (isAttached()) getView().onLoaderReset();
    }

    @Override public void onItemClick(int position, View v, AppsModel item) {
        if (getView().hasSelection()) {
            onItemLongClick(position, v, item);
        } else {
            getView().onOpenAppDetails(v, item);
        }
    }

    @Override public void onItemLongClick(int position, View v, AppsModel item) {
        getView().setSelection(item.getComponentName().toShortString(), position);
    }

    @Override public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.add_menu, menu);
        return true;
    }

    @Override public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        if (item.getItemId() == R.id.add) {
            getView().onAddSelectedApps();
            return true;
        } else if (item.getItemId() == R.id.selectAll) {
            getView().onSelectAll();
            return true;
        }
        return false;
    }

    @Override public void onDestroyActionMode(ActionMode mode) {
        getView().onActionModeDestroyed();
    }

    @Override public void onAddSelectedApps(@Nullable List<AppsModel> selections) {
        if (selections != null && !selections.isEmpty()) {
            for (int i = 0; i < selections.size(); i++) {
                AppsModel model = selections.get(i);
                if (AppsModel.exists(model.getActivityInfoName(), model.getPackageName())) {
                    continue;
                }
                int lastPosition = AppsModel.lastPosition() + 1;
                Logger.e(lastPosition);
                model.setIndexPosition(lastPosition);
                model.save();
            }
            EventBus.getDefault().post(new SelectedAppsEventModel());
            EventBus.getDefault().post(new FloatingEventModel());
        }
    }
}
