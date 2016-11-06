package com.fastaccess.ui.modules.floating.folders;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.Toast;

import com.fastaccess.R;
import com.fastaccess.data.dao.FolderModel;
import com.fastaccess.data.dao.events.FolderEventModel;
import com.fastaccess.provider.loader.FoldersLoader;
import com.fastaccess.ui.adapter.FloatingFoldersAdapter;
import com.fastaccess.ui.modules.floating.BaseFloatingMvp;
import com.fastaccess.ui.modules.floating.BaseFloatingPresenter;
import com.fastaccess.ui.modules.floating.BaseFloatingView;
import com.fastaccess.ui.modules.floating.folders.drawer.FloatingDrawerView;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;

import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kosh on 14 Oct 2016, 9:12 PM
 */


public class FloatingFoldersView extends BaseFloatingView<FolderModel> implements FloatingFoldersMvp.View {
    private FloatingFoldersPresenter presenter;
    private FoldersLoader foldersLoader;
    private FloatingFoldersAdapter adapter;

    private FloatingFoldersView(@NonNull Context context, boolean isHorizontal) {
        super(context, isHorizontal);
    }

    public static FloatingFoldersView with(@NonNull Context context, boolean isHorizontal) {
        return new FloatingFoldersView(context, isHorizontal);
    }

    @SuppressWarnings("unused") @Subscribe public void onEvent(FolderEventModel model) {
        if (foldersLoader != null) foldersLoader.onContentChanged();
    }

    @Override public void onOpenFolder(@NonNull View v, @NonNull FolderModel item) {
        FloatingDrawerView.with(this).onShow(windowManager, floatingView, item);
    }

    @Override public Loader getLoader() {
        if (foldersLoader == null) {
            foldersLoader = new FoldersLoader(context);
            foldersLoader.registerListener(10, getPresenter());
            foldersLoader.startLoading();
        }
        return foldersLoader;
    }

    @Override public void onLoaderLoaded(@Nullable List<FolderModel> data) {
        if (data == null || data.isEmpty()) {
            Toast.makeText(context, R.string.no_folders_floating, Toast.LENGTH_LONG).show();
        }
        super.onLoaderLoaded(data);
    }

    @Override public BaseRecyclerAdapter getAdapter() {
        if (adapter == null) {
            adapter = new FloatingFoldersAdapter(new ArrayList<FolderModel>(), getPresenter(), isHorizontal);
        }
        return adapter;
    }

    @Override public BaseFloatingPresenter<FolderModel, ? extends BaseFloatingMvp.BaseView<FolderModel>> getPresenter() {
        if (presenter == null) {
            presenter = FloatingFoldersPresenter.with(this);
        }
        return presenter;
    }
}
