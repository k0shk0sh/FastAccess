package com.fastaccess.ui.modules.floating;

import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.view.WindowManager;

import com.fastaccess.ui.widgets.floating.FloatingTouchCallback;
import com.fastaccess.ui.widgets.floating.FloatingView;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.List;

/**
 * Created by Kosh on 23 Oct 2016, 12:14 AM
 */

public interface BaseFloatingMvp {

    interface BaseView<M> {

        Loader getLoader();

        BaseRecyclerAdapter getAdapter();

        BaseFloatingPresenter<M, ? extends BaseView<M>> getPresenter();

        void onLoaderLoaded(@Nullable List<M> data);

        void onViewMoving(int x, int y);

        void onStoppedMoving();

        void onLongPressed();

        void onDoubleTapped();

        void onSingleTapped();

        void onTouchedOutside();

        void onBackPressed();

        void onDestroy();

        void onToggleVisibility(boolean showFloating);

        void onConfigChanged(int orientation);

        void onUpdateXY();

        void setupParamsSize();
    }

    interface BasePresenter<M, V extends BaseView<M>> extends BaseViewHolder.OnItemClickListener<M>,
            FloatingTouchCallback, Loader.OnLoadCompleteListener<List<M>> {
        void onUpdateWindowParams(@NonNull WindowManager windowManager,
                                  @NonNull WindowManager.LayoutParams originalParams,
                                  @NonNull FloatingView floatingView,
                                  int x, int y);

        void onToggleVisibility(boolean showFloating, @NonNull WindowManager windowManager,
                                @NonNull WindowManager.LayoutParams originalParams, @NonNull android.view.View view,
                                @NonNull FloatingView floatingView, boolean isHorizontal);

        void onMoveToEdge(@NonNull WindowManager windowManager, @NonNull WindowManager.LayoutParams originalParams,
                          @NonNull FloatingView floatingView, @NonNull Point szWindow);
    }
}
