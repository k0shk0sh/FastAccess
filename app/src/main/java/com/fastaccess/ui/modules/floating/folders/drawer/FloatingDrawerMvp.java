package com.fastaccess.ui.modules.floating.folders.drawer;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.view.WindowManager;

import com.fastaccess.data.dao.AppsModel;
import com.fastaccess.data.dao.FolderModel;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.List;

/**
 * Created by Kosh on 22 Oct 2016, 3:11 PM
 */

public interface FloatingDrawerMvp {

    interface View {

        void onShow(@NonNull WindowManager windowManager, @NonNull android.view.View view, @NonNull FolderModel folder);

        void onAppsLoaded(@Nullable List<AppsModel> models);

        void onConfigChanged(int orientation);

        void onTouchedOutside();

        void onBackPressed();

        void onDestroy();
    }

    interface Presenter extends BaseViewHolder.OnItemClickListener<AppsModel>,
            Loader.OnLoadCompleteListener<List<AppsModel>> {}
}
