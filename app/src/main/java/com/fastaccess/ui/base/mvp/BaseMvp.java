package com.fastaccess.ui.base.mvp;

/**
 * Created by Kosh on 25 May 2016, 9:09 PM
 */

public interface BaseMvp {

    interface FAPresenter<V> {
        void attachView(V view);

        void onDestroy();
    }
}
