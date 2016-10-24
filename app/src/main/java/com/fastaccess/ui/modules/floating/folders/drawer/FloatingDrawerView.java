package com.fastaccess.ui.modules.floating.folders.drawer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.NinePatchDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.fastaccess.R;
import com.fastaccess.data.dao.AppsModel;
import com.fastaccess.data.dao.FolderModel;
import com.fastaccess.data.dao.ThemePackEventModel;
import com.fastaccess.helper.ViewHelper;
import com.fastaccess.provider.loader.SelectedAppsLoader;
import com.fastaccess.ui.adapter.FloatingAppsAdapter;
import com.fastaccess.ui.adapter.viewholder.AppDrawerHolder;
import com.fastaccess.ui.modules.floating.folders.FloatingFoldersMvp;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import static android.graphics.PixelFormat.TRANSLUCENT;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
import static android.view.WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
import static android.view.WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;

/**
 * Created by Kosh on 22 Oct 2016, 11:45 AM
 */

public class FloatingDrawerView implements FloatingDrawerMvp.View {

    private WindowManager.LayoutParams originalParams;
    private final FloatingFoldersMvp.View view;
    private AppDrawerHolder drawerHolder;
    private SelectedAppsLoader appsLoader;
    private FloatingAppsAdapter adapter;
    private WindowManager windowManager;
    private FloatingDrawPresenter presenter;


    private FloatingDrawerView(@NonNull FloatingFoldersMvp.View view) {
        this.view = view;
        EventBus.getDefault().register(this);
    }

    public static FloatingDrawerView with(@NonNull FloatingFoldersMvp.View view) {
        return new FloatingDrawerView(view);
    }

    private void setupParams(@NonNull WindowManager windowManager) {
        this.windowManager = windowManager;
        originalParams = new WindowManager.LayoutParams(TYPE_PRIORITY_PHONE,
                FLAG_WATCH_OUTSIDE_TOUCH | FLAG_NOT_TOUCH_MODAL, TRANSLUCENT);
        Point szWindow = new Point();
        windowManager.getDefaultDisplay().getSize(szWindow);
        updateParams(ViewHelper.isLandscape(drawerHolder.appDrawer.getResources()) ? 2 : 1, false);
        originalParams.gravity = Gravity.CENTER;
        windowManager.addView(drawerHolder.appDrawer, originalParams);
        drawerHolder.appDrawer.animate().scaleX(1f).scaleY(1f);
    }

    private void updateParams(int orientation, boolean update) {
        Point szWindow = new Point();
        windowManager.getDefaultDisplay().getSize(szWindow);
//        originalParams.width = MATCH_PARENT;
//        originalParams.height = WRAP_CONTENT;
        originalParams.width = orientation == Configuration.ORIENTATION_PORTRAIT ? szWindow.x - 50 : (szWindow.x - 150);
        originalParams.height = orientation == Configuration.ORIENTATION_PORTRAIT ? (int) (szWindow.y / 1.8) : (szWindow.y - 200);
        if (update) windowManager.updateViewLayout(drawerHolder.appDrawer, originalParams);
    }

    @SuppressLint("InflateParams")
    @Override public void onShow(@NonNull WindowManager windowManager, @NonNull View view, @NonNull FolderModel folder) {
        this.windowManager = windowManager;
        Context context = view.getContext();
        drawerHolder = new AppDrawerHolder(LayoutInflater.from(view.getContext()).inflate(R.layout.floating_folder_layout, null, false), this);
        adapter = new FloatingAppsAdapter(new ArrayList<AppsModel>(), getPresenter(), false);
        drawerHolder.recycler.setAdapter(adapter);
        drawerHolder.emptyText.setText(R.string.no_apps);
        drawerHolder.recycler.setEmptyView(drawerHolder.emptyText);
        NinePatchDrawable drawable = (NinePatchDrawable) drawerHolder.appDrawer.getBackground();
        drawable.setColorFilter(new PorterDuffColorFilter(folder.getColor(),
                PorterDuff.Mode.MULTIPLY));
        setupParams(windowManager);
        appsLoader = new SelectedAppsLoader(context, folder.getId());
        appsLoader.registerListener(folder.hashCode(), getPresenter());
        appsLoader.startLoading();
    }

    @Override public void onAppsLoaded(@Nullable List<AppsModel> models) {
        if (models != null) adapter.insertItems(models);
        else adapter.clear();
    }

    @Override public void onConfigChanged(int orientation) {
        updateParams(orientation, true);
    }

    @Override public void onTouchedOutside() {
        onDestroy();
    }

    @Override public void onBackPressed() {
        onTouchedOutside();
    }

    @Override public void onDestroy() {
        if (windowManager != null) {
            if (drawerHolder != null && drawerHolder.appDrawer.isShown()) {
                drawerHolder.appDrawer.animate().scaleY(0).scaleX(0).withEndAction(new Runnable() {
                    @Override public void run() {
                        windowManager.removeView(drawerHolder.appDrawer);
                        drawerHolder.onDestroy();
                        if (appsLoader != null) appsLoader.unregisterListener(getPresenter());
                        EventBus.getDefault().unregister(this);
                        getPresenter().onDestroy();
                    }
                });
            }
        }
    }

    @SuppressWarnings("unused") @Subscribe public void onEvent(ThemePackEventModel model) {
        if (appsLoader != null) appsLoader.forceLoad();
    }

    private FloatingDrawPresenter getPresenter() {
        if (presenter == null) {
            presenter = FloatingDrawPresenter.with(this);
        }
        return presenter;
    }
}
