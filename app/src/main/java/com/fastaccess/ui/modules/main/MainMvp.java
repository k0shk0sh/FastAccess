package com.fastaccess.ui.modules.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;

import com.fastaccess.ui.base.mvp.BaseMvp;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import it.sephiroth.android.library.bottomnavigation.BottomNavigation;
import it.sephiroth.android.library.bottomnavigation.BottomNavigation.OnMenuItemSelectionListener;

/**
 * Created by Kosh on 10 Oct 2016, 10:56 PM
 */

public interface MainMvp {

    int DEVICE_APPS = 0;
    int FOLDERS = 1;
    int SELECTED_APPS = 2;

    @IntDef({
            DEVICE_APPS,
            FOLDERS,
            SELECTED_APPS,
    })
    @Retention(RetentionPolicy.SOURCE) @interface NavigationType {}

    interface View {
        void onNavigationChanged(@NavigationType int navType);

        void onOpenDrawer();

        void onCloseDrawer();

        void onOpenSettings();

        void onStartService();

        void onStopService();

        void onShowBadge(@IdRes int itemId);

        void onHideBadge(@IdRes int itemId);

        void onSelectMenuItem(@IdRes int itemId);

        void onBackup();

        void onRestore();
    }

    interface Presenter extends BaseMvp.FAPresenter<View>, OnNavigationItemSelectedListener, OnMenuItemSelectionListener {
        void onActivityStarted(@Nullable Bundle savedInstance, @NonNull MainView mainView,
                               @NonNull BottomNavigation bottomNavigation,
                               @NonNull NavigationView navigationView);

        boolean canBackPress(@NonNull DrawerLayout drawerLayout);

        void onModuleChanged(@NonNull FragmentManager fragmentManager, @NavigationType int type);

        void onShowHideFragment(@NonNull FragmentManager fragmentManager, @NonNull Fragment toShow, @NonNull Fragment toHide);

        void onAddAndHide(@NonNull FragmentManager fragmentManager, @NonNull Fragment toAdd, @NonNull Fragment toHide);

        void onFilterResult(@NonNull FragmentManager supportFragmentManager, @Nullable String text);

        void onCreateNewFolder(@NonNull FragmentManager supportFragmentManager);

        void onActivityForResult(int requestCode, int resultCode);

        void onHandleShortcuts(@NonNull MainView mainView, @Nullable Intent intent);

        void onBackupRestore(int backupType, @NonNull MainView mainView);
    }
}
