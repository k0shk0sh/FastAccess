package com.fastaccess.ui.modules.cloud.backup;

import android.support.annotation.NonNull;

import com.fastaccess.R;
import com.fastaccess.data.dao.BackupRestoreModel;
import com.fastaccess.helper.Logger;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by Kosh on 23 Oct 2016, 9:04 PM
 */

public class BackupPresenter extends BasePresenter<BackupMvp.View> implements BackupMvp.Presenter {

    protected BackupPresenter(@NonNull BackupMvp.View view) {
        super(view);
    }

    public static BackupPresenter with(@NonNull BackupMvp.View view) {
        return new BackupPresenter(view);
    }

    @Override public void onBackup(DatabaseReference databaseReference) {
        BackupRestoreModel model = BackupRestoreModel.backup();
        if (model == null || model.getUid() == null) {
            getView().onShowMessage(R.string.login_first_msg);
            getView().finishOnError();
        } else {
            getView().onShowProgress();
            Logger.e(model, model.getFolders(), model.getAppsModels());
            databaseReference.child(BackupView.BACKUP_DATABASE_NAME).child(model.getUid()).setValue(model, this);
        }
    }

    @Override public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
        if (databaseError != null) {
            getView().onShowMessage(databaseError.getDetails());
            getView().finishOnError();
        } else {
            getView().onBackupCompleted();
        }
    }
}
