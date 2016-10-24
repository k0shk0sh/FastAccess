package com.fastaccess.ui.modules.apps.device;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v7.view.ActionMode;

import com.fastaccess.data.dao.AppsModel;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.List;

/**
 * Created by Kosh on 10 Oct 2016, 11:40 PM
 */

public interface DeviceAppsMvp {

    interface View {
        void onStartLoading();

        void onAppsLoaded(@Nullable List<AppsModel> data);

        void onLoaderReset();

        void setSelection(@NonNull String componentName, int position);

        boolean hasSelection();

        void onActionModeDestroyed();

        void onOpenAppDetails(@NonNull android.view.View view, @NonNull AppsModel appsModel);

        void onAddSelectedApps();

        void onFilter(@Nullable String text);

        void onSelectAll();
    }

    interface Presenter extends LoaderManager.LoaderCallbacks<List<AppsModel>>,
            BaseViewHolder.OnItemClickListener<AppsModel>,
            ActionMode.Callback {
        void onAddSelectedApps(@Nullable List<AppsModel> selections);
    }
}
