package com.fastaccess.ui.modules.apps.selected;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.data.dao.AppsModel;
import com.fastaccess.data.dao.events.FloatingEventModel;
import com.fastaccess.data.dao.events.SelectedAppsEventModel;
import com.fastaccess.data.dao.events.ThemePackEventModel;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.ui.adapter.DeviceAppsAdapter;
import com.fastaccess.ui.base.BaseFragment;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;
import com.fastaccess.ui.widgets.recyclerview.touch.SimpleItemTouchHelperCallback;
import com.mikhaellopez.circularfillableloaders.CircularFillableLoaders;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import icepick.State;

/**
 * Created by Kosh on 10 Oct 2016, 11:47 PM
 */

public class SelectedAppsView extends BaseFragment<SelectedAppsMvp.View, SelectedAppsPresenter> implements SelectedAppsMvp.View {
    public static final String TAG = "SelectedAppsView";

    @BindView(R.id.recycler) DynamicRecyclerView recycler;
    @BindView(R.id.empty_text) FontTextView emptyText;
    @BindView(R.id.empty) NestedScrollView empty;
    @BindView(R.id.progressBar) CircularFillableLoaders progressBar;
    @State HashMap<String, AppsModel> selection = new LinkedHashMap<>();
    private SelectedAppsPresenter presenter;
    private DeviceAppsAdapter adapter;
    private ActionMode actionMode;
    private Loader loader;

    public static SelectedAppsView newInstance() {
        return new SelectedAppsView();
    }

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        EventBus.getDefault().register(this);
    }

    @Override public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);
    }

    @Override protected int fragmentLayout() {
        return R.layout.grid_list;
    }

    @NonNull @Override protected SelectedAppsPresenter getPresenter() {
        if (presenter == null) {
            presenter = SelectedAppsPresenter.with(this);
        }
        return presenter;
    }

    @Override protected void onFragmentCreated(View view, @Nullable Bundle savedInstanceState) {
        recycler.setEmptyView(empty);
        emptyText.setText(R.string.no_apps_selected);
        adapter = new DeviceAppsAdapter(new ArrayList<AppsModel>(), getPresenter(), selection, true);
        recycler.setAdapter(adapter);
        loader = getLoaderManager().initLoader(0, null, getPresenter());
        if (!selection.isEmpty()) {
            actionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(getPresenter());
            actionMode.setTitle(getString(R.string.selected) + " ( " + adapter.selectionSize() + " )");
        }
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(getPresenter(), false);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recycler);
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

    @Override public void onRemoveSelectedApps() {
        getPresenter().onRemoveSelectedApps(adapter.getSelections());
        if (actionMode != null) actionMode.finish();
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

    @Override public void onNotifyChanges() {
        if (loader != null) loader.onContentChanged();//notify changes
        EventBus.getDefault().post(new FloatingEventModel());
    }

    @Override public void onNotifyItemMoved(int fromPosition, int toPosition) {
        adapter.notifyItemMoved(fromPosition, toPosition);
    }

    @Override public void onSwap(int fromPosition, int toPosition) {
        getPresenter().onSaveIndexChanges(adapter, fromPosition, toPosition);
        Collections.swap(adapter.getData(), fromPosition, toPosition);
    }

    @Override public void onSelectAll() {
        adapter.clearSelection();
        for (int i = 0; i < adapter.getData().size(); i++) {
            AppsModel model = adapter.getItem(i);
            setSelection(model.getComponentName().toShortString(), i);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN) public void onEvent(SelectedAppsEventModel eventModel) {
        onNotifyChanges();
    }

    @Subscribe(threadMode = ThreadMode.MAIN) public void onEvent(ThemePackEventModel model) {
        if (loader != null) loader.forceLoad();
    }
}
