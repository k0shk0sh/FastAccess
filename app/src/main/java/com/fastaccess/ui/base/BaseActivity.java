package com.fastaccess.ui.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.fastaccess.BuildConfig;
import com.fastaccess.R;
import com.fastaccess.helper.AppHelper;
import com.fastaccess.helper.PrefConstant;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.whats_new.WhatsNewView;
import com.fastaccess.ui.widgets.dialog.MessageDialogView;

import butterknife.BindView;
import butterknife.ButterKnife;
import icepick.Icepick;

/**
 * Created by Kosh on 24 May 2016, 8:48 PM
 */

public abstract class BaseActivity<V, P extends BasePresenter<V>> extends AppCompatActivity implements
        MessageDialogView.MessageDialogViewActionCallback {

    @LayoutRes protected abstract int layout();

    @Nullable @BindView(R.id.toolbar) Toolbar toolbar;

    @NonNull protected abstract P getPresenter();

    protected abstract boolean isTransparent();

    protected abstract boolean canBack();

    @Override protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (layout() != 0) {
            setContentView(layout());
            ButterKnife.bind(this);
        }
        Icepick.setDebug(BuildConfig.DEBUG);
        if (savedInstanceState != null && !savedInstanceState.isEmpty()) {
            Icepick.restoreInstanceState(this, savedInstanceState);
        }
        if (PrefConstant.showWhatsNew()) {
            startActivity(new Intent(this, WhatsNewView.class));
            PrefConstant.setWhatsNewVersion();
        }
        setupToolbarAndStatusBar();
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (canBack()) {
            if (item.getItemId() == android.R.id.home) {
                supportFinishAfterTransition();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override protected void onDestroy() {
        //noinspection ConstantConditions
        if (getPresenter() != null) getPresenter().onDestroy();
        super.onDestroy();
    }

    @Override public void onDialogDismissed() {

    }//pass

    @Override public void onMessageDialogActionClicked(boolean isOk, int requestCode) {

    }//pass

    private void setupToolbarAndStatusBar() {
        if (AppHelper.isLollipopOrHigher()) {
            changeAppColor();
        }
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (canBack()) {
                if (getSupportActionBar() != null) {
                    if (toolbar != null) {
                        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                supportFinishAfterTransition();
                            }
                        });
                    }
                    getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }
            }
        }
    }

    protected void setToolbarIcon(@DrawableRes int res) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(res);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @RequiresApi(value = 21) protected void changeAppColor() {
        if (AppHelper.isLollipopOrHigher()) {
            if (!isTransparent()) {
                getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.primary_dark));
            }
        }
    }
}
