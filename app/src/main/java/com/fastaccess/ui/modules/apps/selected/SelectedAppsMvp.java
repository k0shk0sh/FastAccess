package com.fastaccess.ui.modules.apps.selected;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v7.view.ActionMode;

import com.fastaccess.data.dao.AppsModel;
import com.fastaccess.ui.adapter.DeviceAppsAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;
import com.fastaccess.ui.widgets.recyclerview.touch.ItemTouchHelperAdapter;

import java.util.List;

/**
 * Created by Kosh on 10 Oct 2016, 11:40 PM
 */

public interface SelectedAppsMvp {

    interface View {
        void onStartLoading();

        void onAppsLoaded(@Nullable List<AppsModel> data);

        void onLoaderReset();

        void setSelection(@NonNull String componentName, int position);

        boolean hasSelection();

        void onActionModeDestroyed();

        void onRemoveSelectedApps();

        void onFilter(@Nullable String text);

        void onNotifyChanges();

        void onNotifyItemMoved(int fromPosition, int toPosition);

        void onSwap(int fromPosition, int toPosition);

        void onSelectAll();
    }

    interface Presenter extends LoaderManager.LoaderCallbacks<List<AppsModel>>,
            BaseViewHolder.OnItemClickListener<AppsModel>,
            ActionMode.Callback, ItemTouchHelperAdapter {
        void onRemoveSelectedApps(@Nullable List<AppsModel> selections);

        void onSaveIndexChanges(@NonNull DeviceAppsAdapter adapter, int fromPosition, int toPosition);
    }
}
