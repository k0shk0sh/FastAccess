package com.fastaccess.ui.modules.apps.selected;

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
import com.fastaccess.provider.loader.SelectedAppsLoader;
import com.fastaccess.ui.adapter.DeviceAppsAdapter;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.List;

/**
 * Created by Kosh on 10 Oct 2016, 11:45 PM
 */

public class SelectedAppsPresenter extends BasePresenter<SelectedAppsMvp.View> implements SelectedAppsMvp.Presenter {

    protected SelectedAppsPresenter(@NonNull SelectedAppsMvp.View view) {
        super(view);
    }

    public static SelectedAppsPresenter with(@NonNull SelectedAppsMvp.View view) {
        return new SelectedAppsPresenter(view);
    }

    @Override public Loader<List<AppsModel>> onCreateLoader(int id, Bundle args) {
        if (isAttached()) getView().onStartLoading();
        return new SelectedAppsLoader(App.getInstance().getApplicationContext());
    }

    @Override public void onLoadFinished(Loader<List<AppsModel>> loader, List<AppsModel> data) {
        getView().onAppsLoaded(data);
    }

    @Override public void onLoaderReset(Loader<List<AppsModel>> loader) {
        if (isAttached()) getView().onLoaderReset();
    }

    @Override public void onItemClick(int position, View v, AppsModel item) {
        getView().setSelection(item.getComponentName().toShortString(), position);
    }

    @Override public void onItemLongClick(int position, View v, AppsModel item) {
        //op-out for drag & drop
    }

    @Override public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.remove_menu, menu);
        return true;
    }

    @Override public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        if (item.getItemId() == R.id.remove) {
            getView().onRemoveSelectedApps();
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

    @Override public void onRemoveSelectedApps(@Nullable List<AppsModel> selections) {
        if (selections != null && !selections.isEmpty()) {
            for (AppsModel selection : selections) {
                selection.delete();
            }
            if (isAttached()) getView().onNotifyChanges();
        }
    }

    @Override public void onSaveIndexChanges(@NonNull DeviceAppsAdapter adapter, int fromPosition, int toPosition) {
        AppsModel fromTo = adapter.getItem(toPosition);
        AppsModel toFrom = adapter.getItem(fromPosition);
        int actualFrom = fromTo.getIndexPosition();
        int actualTo = toFrom.getIndexPosition();
        fromTo.setIndexPosition(actualTo);
        fromTo.save();
        toFrom.setIndexPosition(actualFrom);
        toFrom.save();
    }

    @Override public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                if (isAttached()) getView().onSwap(i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                if (isAttached()) getView().onSwap(i, i - 1);
            }
        }
        if (isAttached()) getView().onNotifyItemMoved(fromPosition, toPosition);
    }

    @Override public void onItemDismiss(int position) {}

    @Override public void onItemStoppedMoving() {
        if (isAttached()) getView().onNotifyChanges();
    }
}
