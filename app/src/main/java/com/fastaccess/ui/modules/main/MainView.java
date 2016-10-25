package com.fastaccess.ui.modules.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.fastaccess.R;
import com.fastaccess.helper.AnimHelper;
import com.fastaccess.helper.AppHelper;
import com.fastaccess.helper.NotificationHelper;
import com.fastaccess.helper.PermissionsHelper;
import com.fastaccess.provider.service.FloatingService;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.modules.apps.device.DeviceAppsView;
import com.fastaccess.ui.modules.cloud.auth.LoginView;
import com.fastaccess.ui.modules.settings.SettingsView;
import com.fastaccess.ui.widgets.FontEditText;
import com.fastaccess.ui.widgets.ForegroundImageView;
import com.fastaccess.ui.widgets.dialog.MessageDialogView;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import butterknife.OnTouch;
import icepick.State;
import it.sephiroth.android.library.bottomnavigation.BadgeProvider;
import it.sephiroth.android.library.bottomnavigation.BottomNavigation;

public class MainView extends BaseActivity<MainMvp.View, MainPresenter> implements MainMvp.View {
    public final static int BACKUP_REQUEST_CODE = 1;
    public final static int RESTORE_REQUEST_CODE = 2;

    @MainMvp.NavigationType @State int navType;

    @BindView(R.id.searchEditText) FontEditText searchEditText;
    @BindView(R.id.clear) ForegroundImageView clear;
    @BindView(R.id.appbar) AppBarLayout appbar;
    @BindView(R.id.coordinatorLayout) CoordinatorLayout coordinatorLayout;
    @BindView(R.id.navigation) NavigationView navigation;
    @BindView(R.id.drawerLayout) DrawerLayout drawerLayout;
    @BindView(R.id.bottomNavigation) BottomNavigation bottomNavigation;
    @BindView(R.id.fab) FloatingActionButton fab;
    private MainPresenter presenter;
    private BadgeProvider badgeProvider;


    @OnClick(R.id.fab) void onClick() {
        if (navType == MainMvp.FOLDERS) {
            getPresenter().onCreateNewFolder(getSupportFragmentManager());
        }
    }

    @OnTouch(R.id.searchEditText) boolean onTouch() {
        appbar.setExpanded(false, true);
        return false;
    }

    @OnTextChanged(value = R.id.searchEditText, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED) void onTextChange(Editable s) {
        String text = s.toString();
        if (text.length() == 0) {
            getPresenter().onFilterResult(getSupportFragmentManager(), text);
            AnimHelper.animateVisibility(clear, false);
        } else {
            AnimHelper.animateVisibility(clear, true);
            getPresenter().onFilterResult(getSupportFragmentManager(), text);
        }
    }

    @OnClick(value = {R.id.searchIcon, R.id.clear}) void onClick(View view) {
        if (view.getId() == R.id.clear) {
            AppHelper.hideKeyboard(searchEditText);
            searchEditText.setText("");
        }
    }

    @Override protected int layout() {
        return R.layout.activity_main;
    }

    @NonNull @Override protected MainPresenter getPresenter() {
        if (presenter == null) {
            presenter = MainPresenter.with(this);
        }
        return presenter;
    }

    @Override protected boolean isTransparent() {
        return true;
    }

    @Override protected boolean canBack() {
        return false;
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, DeviceAppsView.newInstance(), DeviceAppsView.TAG)
                    .commit();
            getPresenter().onHandleShortcuts(this, getIntent());
        }
        setToolbarIcon(R.drawable.ic_menu);
        getPresenter().onActivityStarted(savedInstanceState, this, bottomNavigation, navigation);
        if (null != savedInstanceState) getBadgeProvider().restore(savedInstanceState);
        if (navType == MainMvp.FOLDERS) {
            fab.show();
        } else {
            fab.hide();
        }
    }

    @Override public void onNavigationChanged(@MainMvp.NavigationType int navType) {
        //noinspection WrongConstant
        if (bottomNavigation.getSelectedIndex() != navType) bottomNavigation.setSelectedIndex(navType, true);
        this.navType = navType;
        getPresenter().onModuleChanged(getSupportFragmentManager(), navType);
        if (navType == MainMvp.FOLDERS) {
            fab.show();
        } else {
            fab.hide();
        }
    }

    @Override public void onOpenDrawer() {
        if (!drawerLayout.isDrawerOpen(GravityCompat.START)) drawerLayout.openDrawer(GravityCompat.START);
    }

    @Override public void onCloseDrawer() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) drawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override public void onOpenSettings() {
        startActivity(new Intent(this, SettingsView.class));
    }

    @Override public void onStartService() {
        if (PermissionsHelper.systemAlertPermissionIsGranted(this)) {
            startService(new Intent(this, FloatingService.class));
        } else {
            Toast.makeText(this, R.string.floating_window_warning, Toast.LENGTH_LONG).show();
        }
    }

    @Override public void onStopService() {
        stopService(new Intent(this, FloatingService.class));
        NotificationHelper.cancelAllNotifications(this);
    }

    @Override public void onShowBadge(@IdRes int itemId) {
        if (!getBadgeProvider().hasBadge(itemId)) {
            getBadgeProvider().show(itemId);
        }
    }

    @Override public void onHideBadge(@IdRes int itemId) {
        if (getBadgeProvider().hasBadge(itemId)) {
            getBadgeProvider().remove(itemId);
        }
    }

    @Override public void onSelectMenuItem(@IdRes int itemId) {
        navigation.getMenu().findItem(itemId).setChecked(true);
    }

    @Override public void onBackup() {
        MessageDialogView.newInstance(R.string.backup, R.string.backup_warning, BACKUP_REQUEST_CODE)
                .show(getSupportFragmentManager(), "MessageDialogView");
    }

    @Override public void onRestore() {
        MessageDialogView.newInstance(R.string.restore, R.string.restore_warning, RESTORE_REQUEST_CODE)
                .show(getSupportFragmentManager(), "MessageDialogView");
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onOpenDrawer();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onBackPressed() {
        if (getPresenter().canBackPress(drawerLayout)) {
            super.onBackPressed();
        } else {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PermissionsHelper.OVERLAY_PERMISSION_REQ_CODE) {
            if (resultCode == Activity.RESULT_OK) onStartService();//start service since the user wanted to in the first time.
        }
    }

    @Override protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getPresenter().onHandleShortcuts(this, intent);
    }

    @NonNull private BadgeProvider getBadgeProvider() {
        if (badgeProvider == null) {
            badgeProvider = bottomNavigation.getBadgeProvider();
        }
        return badgeProvider;
    }

    @Override public void onMessageDialogActionClicked(boolean isOk, int requestCode) {
        if (isOk) {
            getPresenter().onBackupRestore(requestCode == BACKUP_REQUEST_CODE ? LoginView.BACKUP_TYPE : LoginView.RESTORE_TYPE, this);
        }
    }
}
