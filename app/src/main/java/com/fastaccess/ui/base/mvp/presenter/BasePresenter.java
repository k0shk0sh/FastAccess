package com.fastaccess.ui.base.mvp.presenter;

import android.support.annotation.NonNull;

import com.fastaccess.ui.base.mvp.BaseMvp;

/**
 * Created by Kosh on 25 May 2016, 9:12 PM
 */

public class BasePresenter<V> implements BaseMvp.FAPresenter<V> {

    private V view;

    private BasePresenter() {throw new RuntimeException("Cant not be initialized");}

    protected BasePresenter(@NonNull V view) {
        attachView(view);
    }

    @Override public void attachView(@NonNull V view) {
        this.view = view;
    }

    @Override public void onDestroy() {
        view = null;
    }

    protected boolean isAttached() {
        return view != null;
    }

    protected V getView() {
        checkViewAttached();
        return view;
    }

    private void checkViewAttached() {
        if (!isAttached()) throw new NullPointerException("View is not injected to presenter");
    }

}
