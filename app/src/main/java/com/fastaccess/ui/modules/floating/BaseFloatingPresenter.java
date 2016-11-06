package com.fastaccess.ui.modules.floating;

import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.v4.content.Loader;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;

import com.fastaccess.R;
import com.fastaccess.helper.AnimHelper;
import com.fastaccess.helper.Logger;
import com.fastaccess.helper.PrefConstant;
import com.fastaccess.helper.PrefHelper;
import com.fastaccess.helper.ViewHelper;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.widgets.floating.FloatingView;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;

import java.util.List;

/**
 * Created by Kosh on 23 Oct 2016, 12:18 AM
 */

public class BaseFloatingPresenter<M, V extends BaseFloatingMvp.BaseView<M>> extends BasePresenter<V> implements BaseFloatingMvp.BasePresenter<M, V> {

    private final TimeInterpolator moveEdgeInterpolator = new AccelerateInterpolator();

    protected BaseFloatingPresenter(@NonNull V view) {
        super(view);
    }

    @Override public void onViewMoving(int x, int y) {
        if (isAttached()) getView().onViewMoving(x, y);
    }

    @Override public void onSingleTapped() {
        if (isAttached()) getView().onSingleTapped();
    }

    @Override public void onDoubleTapped() {
        if (isAttached()) getView().onDoubleTapped();
    }

    @Override public void onLongPressed() {
        if (isAttached()) getView().onLongPressed();
    }

    @Override public void onSwipe(int swipeDirection) {}//Op-out

    @Override public void onBackPressed() {
        onTouchOutside();
    }

    @Override public void onTouchOutside() {
        if (isAttached()) getView().onTouchedOutside();
    }

    @Override public void onStoppedMoving() {
        if (isAttached()) getView().onStoppedMoving();
    }

    @Override public void onConfigChanged(int orientation) {
        if (isAttached()) getView().onConfigChanged(orientation);
    }

    @Override public void onItemClick(int position, View v, M item) {
        if (isAttached()) getView().onTouchedOutside();
    }

    @Override public void onItemLongClick(int position, View v, M item) {
        onItemClick(position, v, item);
    }

    @Override public void onUpdateWindowParams(@NonNull WindowManager windowManager,
                                               @NonNull WindowManager.LayoutParams originalParams,
                                               @NonNull FloatingView floatingView,
                                               int x, int y) {
        originalParams.x = x;
        originalParams.y = y;
        windowManager.updateViewLayout(floatingView, originalParams);
    }

    @Override public void onToggleVisibility(final boolean showFloating, @NonNull final WindowManager windowManager,
                                             @NonNull final WindowManager.LayoutParams originalParams, @NonNull final View view,
                                             @NonNull final FloatingView floatingView, boolean isHorizontal) {
        AnimHelper.animateVisibility(floatingView, showFloating);
        AnimHelper.animateVisibility(view, !showFloating, showFloating ? new AnimHelper.AnimationCallback() {
            @Override public void onAnimationEnd() {
                if (!isAttached()) return;
                getView().setupParamsSize();
            }

            @Override public void onAnimationStart() {}
        } : null);
        if (!showFloating) {
            if (!isAttached()) return;
            if (isHorizontal) {
                final DynamicRecyclerView recycler = (DynamicRecyclerView) view.findViewById(R.id.recycler);
                originalParams.width = ViewHelper.getWidthFromRecyclerView(recycler, windowManager);
            }
            windowManager.updateViewLayout(view, originalParams);
        }
    }

    @Override public void onMoveToEdge(@NonNull final WindowManager windowManager, @NonNull final WindowManager.LayoutParams originalParams,
                                       @NonNull final FloatingView floatingView, @NonNull Point szWindow) {
        if (!PrefHelper.getBoolean(PrefConstant.FA_EDGES_STICKY)) return;
        int w = originalParams.width;
        final boolean isMoveRightEdge = originalParams.x + w / 2 <= szWindow.x / 2;
        final int goalPositionX = isMoveRightEdge ? 0 : szWindow.x - w;
        Logger.e(originalParams.x, goalPositionX, isMoveRightEdge);
        if (!isAttached() || !floatingView.isShown()) return;
        ValueAnimator mMoveEdgeAnimator = ValueAnimator.ofInt(originalParams.x, goalPositionX);
        mMoveEdgeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override public void onAnimationUpdate(ValueAnimator animation) {
                if (!isAttached() || !floatingView.isShown()) return;
                originalParams.x = (Integer) animation.getAnimatedValue();
                windowManager.updateViewLayout(floatingView, originalParams);
                getView().onUpdateXY();
            }
        });
        mMoveEdgeAnimator.setDuration(200L);
        mMoveEdgeAnimator.setInterpolator(moveEdgeInterpolator);
        mMoveEdgeAnimator.start();
    }

    @Override public void onLoadComplete(Loader<List<M>> loader, List<M> data) {
        if (isAttached()) getView().onLoaderLoaded(data);
    }
}
