package com.fastaccess.provider.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.fastaccess.data.dao.FolderModel;

import java.util.List;

/**
 * Created by Kosh on 11 Oct 2016, 7:37 PM
 */

public class FoldersLoader extends AsyncTaskLoader<List<FolderModel>> {

    private List<FolderModel> folderModels;

    public FoldersLoader(Context context) {
        super(context);
    }

    @Override public List<FolderModel> loadInBackground() {
        return FolderModel.getFolders();
    }

    @Override public void deliverResult(List<FolderModel> folders) {
        if (isReset()) {
            if (folders != null) {
                return;
            }
        }
        folderModels = folders;
        if (isStarted()) {
            super.deliverResult(folders);
        }
    }

    @Override protected void onStartLoading() {
        if (folderModels != null) {
            deliverResult(folderModels);
        }
        if (takeContentChanged()) {
            forceLoad();
        } else if (folderModels == null) {
            forceLoad();
        }


    }

    @Override protected void onStopLoading() {
        cancelLoad();
    }

    @Override protected void onReset() {
        onStopLoading();
        if (folderModels != null) {

            folderModels = null;
        }
    }

    @Override public void onCanceled(List<FolderModel> apps) {
        super.onCanceled(apps);
    }

    @Override public void forceLoad() {
        super.forceLoad();
    }
}
