package com.fastaccess.ui.modules.cloud.restore;

import android.support.annotation.NonNull;

import com.fastaccess.R;
import com.fastaccess.data.dao.BackupRestoreModel;
import com.fastaccess.helper.Logger;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.cloud.backup.BackupView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * Created by Kosh on 23 Oct 2016, 9:04 PM
 */

public class RestorePresenter extends BasePresenter<RestoreMvp.View> implements RestoreMvp.Presenter {

    protected RestorePresenter(@NonNull RestoreMvp.View view) {
        super(view);
    }

    public static RestorePresenter with(@NonNull RestoreMvp.View view) {
        return new RestorePresenter(view);
    }

    @Override public void onRestore(DatabaseReference databaseReference) {
        FirebaseUser user = getView().user();
        if (user == null) {
            getView().onShowMessage(R.string.login_first_msg);
        } else {
            getView().onShowProgress();
            Logger.e(user.getUid());
            Query query = databaseReference
                    .child(BackupView.BACKUP_DATABASE_NAME)
                    .child(user.getUid())
                    .limitToFirst(1);
            query.keepSynced(true);
            query.addListenerForSingleValueEvent(this);
        }
    }

    @Override public void onDataChange(DataSnapshot dataSnapshot) {
        if (!isAttached()) return;
        if (dataSnapshot != null) {
            Logger.e(dataSnapshot);
            FirebaseUser user = getView().user();
            if (user == null) {
                getView().onHideProgress();
                getView().onShowMessage(R.string.login_first_msg);
                getView().finishOnError();
                return;
            }
            if (dataSnapshot.hasChildren()) {
                BackupRestoreModel.restore(dataSnapshot.getValue(BackupRestoreModel.class));
            }
            getView().onHideProgress();
            getView().onRestoreCompleted();
        } else {
            getView().onHideProgress();
            getView().onShowMessage(R.string.no_data_to_restore);
            getView().finishOnError();
        }
    }

    @Override public void onCancelled(DatabaseError databaseError) {
        Logger.e(databaseError.getMessage());
        getView().onHideProgress();
        getView().finishOnError();
    }
}
