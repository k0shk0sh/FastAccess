package com.fastaccess.ui.modules.cloud.backup;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.google.firebase.database.DatabaseReference;

/**
 * Created by Kosh on 23 Oct 2016, 8:56 PM
 */

public interface BackupMvp {

    interface View {
        void onShowProgress();

        void onHideProgress();

        void onShowMessage(@StringRes int resId);

        void onShowMessage(@NonNull String msg);

        void finishOnError();

        void onBackupCompleted();
    }

    interface Presenter extends DatabaseReference.CompletionListener {

        void onBackup(DatabaseReference databaseReference);
    }
}
