package com.fastaccess.ui.modules.cloud.backup;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.widget.Toast;

import com.fastaccess.R;
import com.fastaccess.ui.base.BaseActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Kosh on 23 Oct 2016, 9:05 PM
 */

public class BackupView extends BaseActivity<BackupMvp.View, BackupPresenter> implements BackupMvp.View {
    public static final String BACKUP_DATABASE_NAME = "backup_db";

    private BackupPresenter presenter;
    private ProgressDialog progressDialog;
    private DatabaseReference database;

    @Override protected int layout() {
        return 0;
    }

    @NonNull @Override protected BackupPresenter getPresenter() {
        if (presenter == null) {
            presenter = BackupPresenter.with(this);
        }
        return presenter;
    }

    @Override protected boolean isTransparent() {
        return false;
    }

    @Override protected boolean canBack() {
        return false;
    }

    @Override public void onShowProgress() {
        if (!getProgressDialog().isShowing()) getProgressDialog().show();
    }

    @Override public void onHideProgress() {
        if (getProgressDialog().isShowing()) getProgressDialog().dismiss();
    }

    @Override public void onShowMessage(@StringRes int resId) {
        onShowMessage(getString(resId));
    }

    @Override public void onShowMessage(@NonNull String msg) {
        onHideProgress();
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    @Override public void finishOnError() {
        finish();
    }

    @Override public void onBackupCompleted() {
        onHideProgress();
        onShowMessage(R.string.successfully_backup);
        finish();
    }

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPresenter().onBackup(getDatabase());
    }

    private ProgressDialog getProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(getString(R.string.backup_in_progress));
        }
        return progressDialog;
    }

    private DatabaseReference getDatabase() {
        if (database == null) {
            database = FirebaseDatabase.getInstance().getReference();
        }
        return database;
    }
}
