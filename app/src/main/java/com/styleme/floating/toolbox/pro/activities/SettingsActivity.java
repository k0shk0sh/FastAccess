package com.styleme.floating.toolbox.pro.activities;

import android.os.Bundle;

import com.styleme.floating.toolbox.pro.R;
import com.styleme.floating.toolbox.pro.activities.base.BaseActivity;
import com.styleme.floating.toolbox.pro.fragments.SettingsFragment;

/**
 * Created by Kosh on 9/3/2015. copyrights are reserved
 */
public class SettingsActivity extends BaseActivity {
    @Override
    protected int layout() {
        return R.layout.settings_layout;
    }

    @Override
    protected boolean canBack() {
        return true;
    }

    @Override
    protected boolean hasMenu() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, new SettingsFragment())
                    .commit();
        }
    }
}
