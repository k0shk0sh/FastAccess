package com.fastaccess.ui.modules.whats_new;

import android.content.Context;
import android.support.annotation.NonNull;

import com.fastaccess.helper.AppHelper;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

/**
 * Created by Kosh on 05 Nov 2016, 3:29 AM
 */

public class WhatsNewPresenter extends BasePresenter<WhatsNewMvp.View> implements WhatsNewMvp.Presenter {
    private WhatsNewPresenter(@NonNull WhatsNewMvp.View view) {
        super(view);
    }

    public static WhatsNewPresenter with(@NonNull WhatsNewMvp.View view) {
        return new WhatsNewPresenter(view);
    }

    @Override public void openRateApp(@NonNull Context context) {
        AppHelper.openAppInPlayStore(context);
    }
}
