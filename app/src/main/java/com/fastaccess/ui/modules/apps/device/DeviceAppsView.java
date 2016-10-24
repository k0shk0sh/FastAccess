package com.fastaccess.ui.modules.apps.device;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.data.dao.AppsModel;
import com.fastaccess.data.dao.DeviceAppsEventModel;
import com.fastaccess.data.dao.ThemePackEventModel;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.ui.adapter.DeviceAppsAdapter;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.modules.main.MainMvp;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;
import com.mikhaellopez.circularfillableloaders.CircularFillableLoaders;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import icepick.State;

/**
 * Created by Kosh on 10 Oct 2016, 11:47 PM
 */

public class DeviceAppsView extends BaseFragment<DeviceAppsMvp.View, DeviceAppsPresenter> implements DeviceAppsMvp.View {
    public static final String TAG = "DeviceAppsView";
    @BindView(R.id.recycler) DynamicRecyclerView recycler;
    @BindView(R.id.empty_text) FontTextView emptyText;
    @BindView(R.id.empty) NestedScrollView empty;
    @BindView(R.id.progressBar) CircularFillableLoaders progressBar;
    @State HashMap<String, AppsModel> selection = new LinkedHashMap<>();
    private DeviceAppsPresenter presenter;
    private DeviceAppsAdapter adapter;
    private ActionMode actionMode;
    private Loader loader;
    private MainMvp.View mainCallback;

    public static DeviceAppsView newInstance() {
        return new DeviceAppsView();
    }

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof MainMvp.View)) {
            throw new RuntimeException(context.getClass().getSimpleName() + " is not implementing MainMvp.View");
        }
        mainCallback = (MainMvp.View) context;
        EventBus.getDefault().register(this);
    }

    @Override public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);
        mainCallback = null;
    }

    @Override protected int fragmentLayout() {
        return R.layout.grid_list;
    }

    @NonNull @Override protected DeviceAppsPresenter getPresenter() {
        if (presenter == null) {
            presenter = DeviceAppsPresenter.with(this);
        }
        return presenter;
    }

    @Override protected void onFragmentCreated(View view, @Nullable Bundle savedInstanceState) {
        recycler.setEmptyView(empty);
        adapter = new DeviceAppsAdapter(new ArrayList<AppsModel>(), getPresenter(), selection);
        recycler.setAdapter(adapter);
        loader = getLoaderManager().initLoader(0, null, getPresenter());
        if (!selection.isEmpty()) {
            actionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(getPresenter());
            actionMode.setTitle(getString(R.string.selected) + " ( " + adapter.selectionSize() + " )");
        }
    }

    @Override public void onStartLoading() {
        recycler.showProgress(progressBar);
    }

    @Override public void onAppsLoaded(@Nullable List<AppsModel> data) {
        recycler.hideProgress(progressBar);
        if (data != null) adapter.insertItems(data);
        else adapter.clear();
    }

    @Override public void onLoaderReset() {
        recycler.hideProgress(progressBar);
        adapter.clear();
    }

    @Override public void setSelection(@NonNull String packageName, int position) {
        adapter.select(packageName, position, !adapter.isSelected(packageName));
        if (hasSelection()) {
            if (actionMode == null) {
                actionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(getPresenter());
            }
            actionMode.setTitle(getString(R.string.selected) + " ( " + adapter.selectionSize() + " )");
        } else {
            actionMode.finish();
        }
    }

    @Override public boolean hasSelection() {
        return adapter.hasSelection();
    }

    @Override public void onActionModeDestroyed() {
        adapter.clearSelection();
        actionMode = null;
    }

    @Override public void onOpenAppDetails(@NonNull View view, @NonNull AppsModel appsModel) {

    }

    @Override public void onAddSelectedApps() {
        getPresenter().onAddSelectedApps(adapter.getSelections());
        if (actionMode != null) actionMode.finish();
        if (mainCallback != null) mainCallback.onShowBadge(R.id.selectedApps);
    }

    @Override public void onFilter(@Nullable String text) {
        if (progressBar.isShown()) {
            return;
        }
        if (InputHelper.isEmpty(text)) {
            loader.onContentChanged();
        } else {
            adapter.getFilter().filter(text);
        }
    }

    @Override public void onSelectAll() {
        adapter.clearSelection();
        for (int i = 0; i < adapter.getData().size(); i++) {
            AppsModel model = adapter.getItem(i);
            setSelection(model.getComponentName().toShortString(), i);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN) public void onEvent(DeviceAppsEventModel model) {
        if (loader != null) loader.onContentChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN) public void onEvent(ThemePackEventModel model) {
        if (loader != null) loader.forceLoad();
    }
}
