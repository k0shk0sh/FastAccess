package com.fastaccess.ui.modules.intro;

import android.support.annotation.NonNull;

import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

/**
 * Created by Kosh on 06 Nov 2016, 12:31 PM
 */

public class IntroPagePresenter extends BasePresenter<IntroPageMvp.View> implements IntroPageMvp.Presenter {
    protected IntroPagePresenter(@NonNull IntroPageMvp.View view) {
        super(view);
    }

    public static IntroPagePresenter with(@NonNull IntroPageMvp.View view) {
        return new IntroPagePresenter(view);
    }
}
