package com.fastaccess.ui.modules.apps.folders.create;

import android.support.annotation.NonNull;

import com.fastaccess.ui.base.mvp.presenter.BasePresenter;

/**
 * Created by Kosh on 11 Oct 2016, 8:26 PM
 */

public class CreateFolderPresenter extends BasePresenter<CreateFolderMvp.View> implements CreateFolderMvp.Presenter {

    protected CreateFolderPresenter(@NonNull CreateFolderMvp.View view) {
        super(view);
    }

    public static CreateFolderPresenter with(@NonNull CreateFolderMvp.View view) {
        return new CreateFolderPresenter(view);
    }
}
