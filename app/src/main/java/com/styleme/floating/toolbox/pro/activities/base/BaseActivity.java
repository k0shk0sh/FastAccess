package com.styleme.floating.toolbox.pro.activities.base;

import android.app.ActivityManager;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.styleme.floating.toolbox.pro.R;
import com.styleme.floating.toolbox.pro.fragments.SettingsFragment;
import com.styleme.floating.toolbox.pro.global.helper.AppHelper;

import butterknife.ButterKnife;

/**
 * Created by Kosh on 8/22/2015. copyrights are reserved
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected abstract int layout();

    protected abstract boolean canBack();

    protected abstract boolean hasMenu();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (AppHelper.isDarkTheme(this)) {
            setTheme(R.style.DarkTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(layout());
        ButterKnife.bind(this);
        PreferenceManager.setDefaultValues(this, R.xml.general_settings, false);
        if (AppHelper.isLollipop()) {
            getWindow().setNavigationBarColor(AppHelper.getAccentColor(this));
            setTaskDescription(new ActivityManager.TaskDescription(getString(R.string.app_name), BitmapFactory.decodeResource(getResources(), R
                    .mipmap.ic_launcher), AppHelper.getPrimaryColor(this)));//changes the color in the recent apps too.
            if (canBack()) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(AppHelper.getPrimaryDarkColor(AppHelper.getPrimaryColor(this)));
            }
        }
        if (findViewById(R.id.toolbar) != null) {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setBackgroundColor(AppHelper.getPrimaryColor(this));
            setSupportActionBar(toolbar);
            final ActionBar ab = getSupportActionBar();
            if (ab != null) {
                if (canBack()) {
                    ab.setHomeAsUpIndicator(R.drawable.ic_back);
                    ab.setDisplayHomeAsUpEnabled(true);
                } else {
                    ab.setHomeAsUpIndicator(R.drawable.ic_menu);
                    ab.setDisplayHomeAsUpEnabled(true);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        if (hasMenu()) {
//            return true;
//        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (canBack()) {
            if (item.getItemId() == android.R.id.home) {
                onBackPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == SettingsFragment.REQUEST_STORAGE) {
            if (verifyPermissions(grantResults)) {
                Snackbar.make(findViewById(R.id.toolbar), "Permission Granted, you may continue using" +
                        " this function now.", Snackbar.LENGTH_LONG).show();
            } else {
                Snackbar.make(findViewById(R.id.toolbar), "Permission Denied, you may not be able to " +
                        "use this functionality", Snackbar.LENGTH_LONG).show();
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private boolean verifyPermissions(int[] grantResults) {
        if (grantResults.length < 1) {
            return false;
        }
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


}
