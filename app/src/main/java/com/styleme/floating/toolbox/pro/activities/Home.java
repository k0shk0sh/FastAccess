package com.styleme.floating.toolbox.pro.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.styleme.floating.toolbox.pro.AppController;
import com.styleme.floating.toolbox.pro.R;
import com.styleme.floating.toolbox.pro.activities.base.BaseActivity;
import com.styleme.floating.toolbox.pro.fragments.MyAppsList;
import com.styleme.floating.toolbox.pro.global.adapter.PagerAdapter;
import com.styleme.floating.toolbox.pro.global.helper.AppHelper;
import com.styleme.floating.toolbox.pro.global.model.AppsModel;
import com.styleme.floating.toolbox.pro.global.model.EventType;
import com.styleme.floating.toolbox.pro.global.model.EventsModel;
import com.styleme.floating.toolbox.pro.global.service.FloatingService;

import butterknife.Bind;

public class Home extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tabs)
    TabLayout tabs;
    @Bind(R.id.appbar)
    AppBarLayout appbar;
    @Bind(R.id.viewpager)
    ViewPager viewpager;
    @Bind(R.id.main_content)
    CoordinatorLayout mainContent;
    @Bind(R.id.nav_view)
    NavigationView navView;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    public static int OVERLAY_PERMISSION_REQ_CODE = 1234;

    @Override
    protected int layout() {
        return R.layout.activity_main;
    }

    @Override
    protected boolean canBack() {
        return false;
    }

    @Override
    protected boolean hasMenu() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!AppHelper.hasSeenWhatsNew(this)) {
            startActivity(new Intent(this, IntroActivity.class));
        }
        AppController.getController().eventBus().register(this);
        drawerLayout.setStatusBarBackgroundColor(AppHelper.getPrimaryDarkColor(AppHelper.getPrimaryColor(this)));
        navView.setItemIconTintList(ColorStateList.valueOf(AppHelper.getAccentColor(this)));
        if (AppHelper.isDarkTheme(this)) {
            navView.setItemTextColor(ColorStateList.valueOf(AppHelper.getAccentColor(this)));
        }
        navView.setNavigationItemSelectedListener(this);
        tabs.setBackgroundColor(AppHelper.getPrimaryColor(this));
        tabs.setSelectedTabIndicatorColor(AppHelper.getAccentColor(this));
        viewpager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
        tabs.setupWithViewPager(viewpager);
        setupTabs();
        if (new AppsModel().getAll() != null && new AppsModel().getAll().size() != 0) {
            startFloating();
        }
    }

    private void setupTabs() {
        if (tabs.getTabAt(0) != null) {
            tabs.getTabAt(0).setIcon(R.drawable.ic_apps_selector);
        }
        if (tabs.getTabAt(1) != null) {
            tabs.getTabAt(1).setIcon(R.drawable.ic_my_apps_selector);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (AppController.getController().eventBus().isRegistered(this)) {
            AppController.getController().eventBus().unregister(this);
        }
    }

    public void onEvent(final EventsModel eventsModel) {
        if (eventsModel != null) {
            if (eventsModel.getEventType() != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (eventsModel.getEventType()) {
                            case THEME:
                                recreate();
                                break;
                            case APPS_COUNT:
                                setupCount(eventsModel);
                                break;
                            case MY_APPS_COUNT:
                                setupCount(eventsModel);
                                break;
                        }
                    }
                });
            }
        }
    }

    private void setupCount(final EventsModel eventsModel) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (eventsModel.getEventType() == EventType.APPS_COUNT) {
                    if (navView.getMenu() != null) {
                        navView.getMenu().findItem(R.id.phoneList).setTitle(getString(R.string.phone_applications) + " ( " + eventsModel.getAppsCount
                                () + "" + " )");
                    }
                } else if (eventsModel.getEventType() == EventType.MY_APPS_COUNT) {
                    if (navView.getMenu() != null) {
                        navView.getMenu().findItem(R.id.myList).setTitle(getString(R.string.selected_apps) + " ( " + new AppsModel().getAll
                                ().size() + " )");
                    }
                }
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        drawerLayout.closeDrawer(GravityCompat.START);
        switch (menuItem.getItemId()) {
            case R.id.action_settings:
                startSettings();
                return true;
            case R.id.stop:
                stopService(new Intent(this, FloatingService.class));
                return true;
            case R.id.play:
                if (new AppsModel().getAll() != null && new AppsModel().getAll().size() != 0) {
                    startFloating();
                } else {
                    Snackbar.make(mainContent, R.string.no_apps_to_start, Snackbar.LENGTH_LONG).show();
                }
                return true;
            case R.id.rate:
                openMarket();
                return true;
            case R.id.deleteAll:
                if (viewpager != null && viewpager.getAdapter() != null) {
                    MyAppsList appsList = (MyAppsList) viewpager.getAdapter().instantiateItem(viewpager, 1);
                    if (appsList != null) {
                        appsList.deleteAll();
                    }
                }
                return true;
            case R.id.myList:
                viewpager.setCurrentItem(1, true);
                return true;
            case R.id.phoneList:
                viewpager.setCurrentItem(0, true);
                return true;
            case R.id.team:
                startActivity(new Intent(this, TheTeam.class));
                return true;
        }
        return true;
    }

    private void openMarket() {
        Intent i = null;
        try {
            i = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.styleme.floating.toolbox.pro"));
        } catch (android.content.ActivityNotFoundException anfe) {
            i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google" + ".com/store/apps/details?id=com.styleme.floating.toolbox.pro"));
        }
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    private void startFloating() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
            }
        } else {
            startService(new Intent(this, FloatingService.class));
        }
    }

    private void startSettings() {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "FA Wont Work Unless This Permission is granted", Toast.LENGTH_LONG).show();
            } else {
                startService(new Intent(this, FloatingService.class));
            }
        }
    }
}
