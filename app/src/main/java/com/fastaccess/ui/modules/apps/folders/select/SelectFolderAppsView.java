package com.fastaccess.ui.modules.apps.folders.select;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.fastaccess.R;
import com.fastaccess.data.dao.AppsModel;
import com.fastaccess.data.dao.FolderModel;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.Logger;
import com.fastaccess.ui.adapter.SelectFolderAppsAdapter;
import com.fastaccess.ui.base.BaseBottomSheetDialog;
import com.fastaccess.ui.modules.apps.folders.create.CreateFolderMvp;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;
import com.mikhaellopez.circularfillableloaders.CircularFillableLoaders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import icepick.State;

/**
 * Created by Kosh on 11 Oct 2016, 10:24 PM
 */

public class SelectFolderAppsView extends BaseBottomSheetDialog implements SelectFolderAppsMvp.View {

    @State HashMap<String, AppsModel> selection = new LinkedHashMap<>();
    private long folderId;

    @BindView(R.id.recycler) DynamicRecyclerView recycler;
    @BindView(R.id.empty_text) FontTextView emptyText;
    @BindView(R.id.empty) NestedScrollView empty;
    @BindView(R.id.progressBar) CircularFillableLoaders progressBar;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.topProgress) ProgressBar topProgress;
    @BindView(R.id.appbar) AppBarLayout appbar;
    private SelectFolderAppsAdapter adapter;
    private SelectFolderAppsPresenter presenter;
    private FolderModel folderModel;
    private CreateFolderMvp.OnNotifyFoldersAdapter callback;

    public static SelectFolderAppsView newInstance(long folderId) {
        SelectFolderAppsView view = new SelectFolderAppsView();
        view.setArguments(Bundler.start().put("folderId", folderId).end());
        return view;
    }

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        if (getPresenter() != null && getParentFragment() instanceof CreateFolderMvp.OnNotifyFoldersAdapter) {
            callback = (CreateFolderMvp.OnNotifyFoldersAdapter) getParentFragment();
        } else if (context instanceof CreateFolderMvp.OnNotifyFoldersAdapter) {
            callback = (CreateFolderMvp.OnNotifyFoldersAdapter) context;
        } else {
            throw new RuntimeException("Activity/Fragment must implement OnNotifyFoldersAdapter");
        }
    }

    @Override public void onDetach() {
        super.onDetach();
    }

    @Override protected int layoutRes() {
        return R.layout.select_folder_apps_layout;
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        folderId = getArguments().getLong("folderId");
        if (savedInstanceState == null) {
            List<AppsModel> apps = AppsModel.getApps(getFolderModel().getId());
            if (!apps.isEmpty()) {
                for (AppsModel m : apps) {
                    selection.put(m.getActivityInfoName(), m);
                }
            }
        }
    }

    @Override protected void onViewCreated(@NonNull View view) {
        toolbar.setTitle(R.string.select_apps);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                dismiss();
            }
        });
        toolbar.inflateMenu(R.menu.add_menu);
        toolbar.getMenu().findItem(R.id.add).setIcon(R.drawable.ic_done);
        toolbar.getMenu().findItem(R.id.selectAll).setVisible(false);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.add) {
                    onAddApps();
                }
                return false;
            }
        });
        adapter = new SelectFolderAppsAdapter(new ArrayList<AppsModel>(), getPresenter(), selection);
        recycler.setEmptyView(empty);
        recycler.setAdapter(adapter);
        getLoaderManager().initLoader(2, null, getPresenter());
    }

    @Override public void onStartLoading() {
        recycler.showProgress(progressBar);
    }

    @Override public void onAppsLoaded(@Nullable List<AppsModel> models) {
        recycler.hideProgress(progressBar);
        if (models == null) {
            adapter.clear();
            dismiss();
            return;
        }
        adapter.insertItems(models);
        Logger.e(models.size());
    }

    @Override public void onLoaderReset() {
        if (recycler == null) return;
        recycler.hideProgress(progressBar);
        adapter.clear();
    }

    @Override public void onRowClicked(@NonNull AppsModel model, int position) {
        adapter.select(model.getActivityInfoName(), position, !adapter.isSelected(model.getActivityInfoName()));
    }

    @NonNull private FolderModel getFolderModel() {
        if (folderModel == null) {
            folderModel = FolderModel.findById(FolderModel.class, folderId);
        }
        if (folderModel == null) {
            throw new NullPointerException("folderModel is null, make sure passing the right id");
        }
        return folderModel;
    }

    private void onAddApps() {
        List<AppsModel> appsModels = adapter.getSelections();
        AppsModel.deleteAllByFolder(getFolderModel());
        if (appsModels != null && !appsModels.isEmpty()) {
            for (AppsModel app : appsModels) {
                app.setFolderId(getFolderModel().getId());
                app.save();
            }
        }
        callback.onNotifyChanges();
        dismiss();
    }

    public SelectFolderAppsPresenter getPresenter() {
        if (presenter == null) presenter = SelectFolderAppsPresenter.with(this);
        return presenter;
    }
}
