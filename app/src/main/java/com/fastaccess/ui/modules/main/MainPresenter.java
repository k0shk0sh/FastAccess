package com.fastaccess.ui.modules.main;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

import com.fastaccess.BuildConfig;
import com.fastaccess.R;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.Logger;
import com.fastaccess.helper.PrefConstant;
import com.fastaccess.helper.PrefHelper;
import com.fastaccess.helper.TypeFaceHelper;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.apps.device.DeviceAppsView;
import com.fastaccess.ui.modules.apps.folders.FoldersView;
import com.fastaccess.ui.modules.apps.selected.SelectedAppsView;
import com.fastaccess.ui.modules.cloud.auth.LoginView;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.firebase.auth.FirebaseUser;

import it.sephiroth.android.library.bottomnavigation.BottomNavigation;

import static com.fastaccess.helper.AppHelper.getFragmentByTag;
import static com.fastaccess.helper.AppHelper.getVisibleFragment;

/**
 * Created by Kosh on 10 Oct 2016, 11:13 PM
 */

public class MainPresenter extends BasePresenter<MainMvp.View> implements MainMvp.Presenter {

    private MainPresenter(@NonNull MainMvp.View view) {
        super(view);
    }

    public static MainPresenter with(MainMvp.View view) {
        return new MainPresenter(view);
    }

    @Override public void onActivityStarted(@Nullable Bundle savedInstance,
                                            @NonNull MainView mainView,
                                            @NonNull BottomNavigation bottomNavigation,
                                            @NonNull NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(this);
        Typeface myTypeface = TypeFaceHelper.getTypeface();
        bottomNavigation.setDefaultTypeface(myTypeface);
        bottomNavigation.setOnMenuItemClickListener(this);
        if (savedInstance == null) {
            bottomNavigation.setDefaultSelectedIndex(0);
        }
    }

    @Override public boolean canBackPress(@NonNull DrawerLayout drawerLayout) {
        return !drawerLayout.isDrawerOpen(GravityCompat.START);
    }

    @SuppressWarnings("ConstantConditions")
    @Override public void onModuleChanged(@NonNull FragmentManager fragmentManager, @MainMvp.NavigationType int type) {
        Fragment currentVisible = getVisibleFragment(fragmentManager);
        DeviceAppsView deviceAppsView = (DeviceAppsView) getFragmentByTag(fragmentManager, DeviceAppsView.TAG);
        FoldersView foldersView = (FoldersView) getFragmentByTag(fragmentManager, FoldersView.TAG);
        SelectedAppsView selectedAppsView = (SelectedAppsView) getFragmentByTag(fragmentManager, SelectedAppsView.TAG);
        switch (type) {
            case MainMvp.DEVICE_APPS:
                if (deviceAppsView == null) {
                    onAddAndHide(fragmentManager, DeviceAppsView.newInstance(), currentVisible);
                } else {
                    onShowHideFragment(fragmentManager, deviceAppsView, currentVisible);
                }
                break;
            case MainMvp.FOLDERS:
                if (foldersView == null) {
                    onAddAndHide(fragmentManager, FoldersView.newInstance(), currentVisible);
                } else {
                    onShowHideFragment(fragmentManager, foldersView, currentVisible);
                }
                break;
            case MainMvp.SELECTED_APPS:
                if (selectedAppsView == null) {
                    onAddAndHide(fragmentManager, SelectedAppsView.newInstance(), currentVisible);
                } else {
                    onShowHideFragment(fragmentManager, selectedAppsView, currentVisible);
                }
                break;
        }
    }

    @Override public void onShowHideFragment(@NonNull FragmentManager fragmentManager, @NonNull Fragment toShow, @NonNull Fragment toHide) {
        Logger.e("show", toShow.getClass().getSimpleName(), "hide", toHide.getClass().getSimpleName());
        fragmentManager
                .beginTransaction()
                .hide(toHide)
                .show(toShow)
                .commit();
    }

    @Override public void onAddAndHide(@NonNull FragmentManager fragmentManager, @NonNull Fragment toAdd, @NonNull Fragment toHide) {
        Logger.e("add", toAdd.getClass().getSimpleName(), "hide", toHide.getClass().getSimpleName());
        fragmentManager
                .beginTransaction()
                .hide(toHide)
                .add(R.id.container, toAdd, toAdd.getClass().getSimpleName())
                .commit();
    }

    @Override public void onFilterResult(@NonNull FragmentManager supportFragmentManager, @Nullable String text) {
        Fragment fragment = getVisibleFragment(supportFragmentManager);
        Logger.e(fragment);
        if (fragment != null) {
            if (fragment instanceof DeviceAppsView) {
                ((DeviceAppsView) fragment).onFilter(text);
            } else if (fragment instanceof FoldersView) {
                ((FoldersView) fragment).onFilter(text);
            } else if (fragment instanceof SelectedAppsView) {
                ((SelectedAppsView) fragment).onFilter(text);
            }
        }
    }

    @Override public void onCreateNewFolder(@NonNull FragmentManager supportFragmentManager) {
        Fragment foldersView = getVisibleFragment(supportFragmentManager);
        if (!(foldersView instanceof FoldersView)) {
            throw new RuntimeException("Oops, Folders Fragment is not currently visible.");
        }
        ((FoldersView) foldersView).onCreateNewFolder();
    }

    @Override public void onHandleShortcuts(@NonNull MainView mainView, @Nullable Intent intent) {
        if (intent != null && intent.getAction() != null) {
            String action = intent.getAction();
            switch (action) {
                case "FOLDER_MODE":
                    PrefHelper.set(PrefConstant.FLOATING_MODE, "Folders");
                    if (isAttached()) {
                        getView().onStopService();
                        getView().onStartService();
                    }
                    break;
                case "APPS_MODE":
                    PrefHelper.set(PrefConstant.FLOATING_MODE, "Apps");
                    if (isAttached()) {
                        getView().onStopService();
                        getView().onStartService();
                    }
                    break;
            }
        }
    }

    @Override public void onBackupRestore(int backupType, @NonNull MainView mainView) {
        Intent intent = new Intent(mainView, LoginView.class);
        intent.putExtras(Bundler.start().put(LoginView.TYPE, backupType).end());
        mainView.startActivity(intent);
    }

    @Override public void onShareUserBackup(@NonNull MainView mainView, @NonNull FirebaseUser currentUser) {
        String packageName = mainView.getApplicationContext().getPackageName();
        Uri deepLinkBuilder = new Uri.Builder()
                .scheme("http")
                .authority(BuildConfig.FA_HOST)
                .appendQueryParameter(BuildConfig.SHARED_URI, currentUser.getUid())
                .build();
        Uri.Builder builder = new Uri.Builder()
                .scheme("https")
                .authority(mainView.getResources().getString(R.string.link_ref) + ".app.goo.gl")
                .path("/")
                .appendQueryParameter("link", Uri.parse(deepLinkBuilder.toString()).toString())
                .appendQueryParameter("apn", packageName);
        ShareCompat.IntentBuilder.from(mainView)
                .setType("message/*")
                .setSubject(mainView.getString(R.string.sharing_backup))
                .setChooserTitle(mainView.getString(R.string.share_my_backup))
                .setHtmlText("<a href='" + Uri.decode(builder.toString()) + "'>" + mainView.getString(R.string.click_here_html) +
                        "</a><br/><b>~" + mainView.getString(R.string.app_name) + "</b>").startChooser();
    }

    @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        getView().onCloseDrawer();
        switch (item.getItemId()) {
            case R.id.settings:
                getView().onOpenSettings();
                return true;
            case R.id.start:
                getView().onStartService();
                return true;
            case R.id.stop:
                getView().onStopService();
                return true;
            case R.id.myApps:
                onMenuItemSelect(R.id.myApps, 0);
                return true;
            case R.id.folders:
                onMenuItemSelect(R.id.folders, 1);
                return true;
            case R.id.selectedApps:
                onMenuItemSelect(R.id.selectedApps, 2);
                return true;
            case R.id.backup:
                getView().onBackup();
                break;
            case R.id.restore:
                getView().onRestore();
                break;
            case R.id.shareBackup:
                getView().onShareBackup();
                break;
        }
        return false;
    }

    @Override public void onMenuItemSelect(@IdRes int id, int position) {
        if (isAttached()) {
            getView().onNavigationChanged(position);
            getView().onHideBadge(id);
            getView().onSelectMenuItem(id);
        }
    }

    @Override public void onMenuItemReselect(@IdRes int id, int position) {}

    @Override public void onResult(@NonNull AppInviteInvitationResult result) {
        if (result.getStatus().isSuccess()) {
            Intent intent = result.getInvitationIntent();
            String deepLink = AppInviteReferral.getDeepLink(intent);
            Uri data = intent.getData();
            String userId = data.getQueryParameter(BuildConfig.SHARED_URI);
            Logger.e(deepLink, data, userId);
            if (!InputHelper.isEmpty(userId)) {
                if (isAttached()) getView().onRestoreFromUserId(userId);
            }
        } else {
            Logger.e("no deep link found.");
        }
    }
}
