package com.fastaccess.ui.modules.apps.folders;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.Loader;
import android.view.View;

import com.fastaccess.App;
import com.fastaccess.R;
import com.fastaccess.data.dao.FolderModel;
import com.fastaccess.provider.loader.FoldersLoader;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

import java.util.List;

/**
 * Created by Kosh on 11 Oct 2016, 7:34 PM
 */

public class FoldersPresenter extends BasePresenter<FoldersMvp.View> implements FoldersMvp.Presenter {
    protected FoldersPresenter(@NonNull FoldersMvp.View view) {
        super(view);
    }

    public static FoldersPresenter with(@NonNull FoldersMvp.View view) {
        return new FoldersPresenter(view);
    }

    @Override public Loader<List<FolderModel>> onCreateLoader(int id, Bundle args) {
        if (isAttached()) getView().onStartLoading();
        return new FoldersLoader(App.getInstance().getApplicationContext());
    }

    @Override public void onLoadFinished(Loader<List<FolderModel>> loader, List<FolderModel> data) {
        getView().onFoldersLoaded(data);
    }

    @Override public void onLoaderReset(Loader<List<FolderModel>> loader) {
        if (isAttached()) getView().onLoaderReset();
    }

    @Override public void onItemClick(int position, View v, FolderModel item) {
        if (v.getId() == R.id.folderImage || v.getId() == R.id.editFolder) {
            getView().onEditFolder(item);
        } else if (v.getId() == R.id.delete) {
            getView().onDeleteFolder(item, position);
        } else {
            getView().onAddAppsToFolder(item);
        }
    }

    @Override public void onItemLongClick(int position, View v, FolderModel item) {
        onItemClick(position, v, item);
    }
}
