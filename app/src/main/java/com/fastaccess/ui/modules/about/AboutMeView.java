package com.fastaccess.ui.modules.about;

import android.support.annotation.NonNull;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.ui.base.BaseActivity;

import butterknife.OnClick;

/**
 * Created by Kosh on 03 Nov 2016, 9:59 PM
 */

public class AboutMeView extends BaseActivity<AboutMeMvp.View, AboutMePresenter> implements AboutMeMvp.View {

    private AboutMePresenter presenter;

    @Override protected int layout() {
        return R.layout.about_me_layout;
    }

    @NonNull @Override protected AboutMePresenter getPresenter() {
        if (presenter == null) {
            presenter = AboutMePresenter.with(this);
        }
        return presenter;
    }

    @Override protected boolean isTransparent() {
        return true;
    }

    @Override protected boolean canBack() {
        return true;
    }

    @OnClick({R.id.gPlus, R.id.facebook, R.id.twitter, R.id.github}) public void onClick(View view) {
        switch (view.getId()) {
            case R.id.gPlus:
                getPresenter().onOpenGooglePlus(this);
                break;
            case R.id.facebook:
                getPresenter().onOpenFacebook(this);
                break;
            case R.id.twitter:
                getPresenter().onOpenTwitter(this);
                break;
            case R.id.github:
                getPresenter().onOpenGithub(this);
                break;
        }
    }
}
