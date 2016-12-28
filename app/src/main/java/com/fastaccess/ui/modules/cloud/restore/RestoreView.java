package com.fastaccess.ui.modules.cloud.restore;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.widget.Toast;

import com.fastaccess.R;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.ui.base.BaseActivity;
import com.fastaccess.ui.modules.cloud.backup.BackupView;
import com.fastaccess.ui.widgets.dialog.MessageDialogView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import icepick.State;

/**
 * Created by Kosh on 23 Oct 2016, 9:05 PM
 */

public class RestoreView extends BaseActivity<RestoreMvp.View, RestorePresenter> implements RestoreMvp.View {

    public static final String USER_ID_INTENT = "user_id";
    private RestorePresenter presenter;
    private ProgressDialog progressDialog;
    private DatabaseReference database;
    private FirebaseUser user;
    @State String userId;

    @Override protected int layout() {
        return 0;
    }

    @NonNull @Override protected RestorePresenter getPresenter() {
        if (presenter == null) {
            presenter = RestorePresenter.with(this);
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

    @Override public void onRestoreCompleted() {
        onHideProgress();
        onShowMessage(R.string.successfully_restored);
        finish();
    }

    @Nullable @Override public FirebaseUser user() {
        if (user == null) user = FirebaseAuth.getInstance().getCurrentUser();
        return user;
    }

    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null && getIntent() != null && getIntent().getExtras() != null) {
            userId = getIntent().getExtras().getString(USER_ID_INTENT);
        }
        if (!InputHelper.isEmpty(userId)) {
            if (savedInstanceState == null) {
                MessageDialogView.newInstance(R.string.restore, R.string.restore_warning)
                        .show(getSupportFragmentManager(), "MessageDialogView");
            }
        } else {
            getPresenter().onRestore(getDatabase(), userId);
        }
    }

    @Override protected void onStop() {
        super.onStop();
        try {// user might cancel if we never registered the listener
            getDatabase().removeEventListener(getPresenter());
        } catch (Exception ignored) {}
    }

    @Override public void onMessageDialogActionClicked(boolean isOk, int requestCode) {
        super.onMessageDialogActionClicked(isOk, requestCode);
        if (isOk) {
            getPresenter().onRestore(getDatabase(), userId);
        } else {
            finish();
        }
    }

    @Override public void onDialogDismissed() {
        super.onDialogDismissed();
        finish();
    }

    private ProgressDialog getProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(getString(R.string.restore_in_progress));
        }
        return progressDialog;
    }

    private DatabaseReference getDatabase() {
        if (database == null) {
            database = FirebaseDatabase.getInstance().getReference(BackupView.BACKUP_DATABASE_NAME);
        }
        return database;
    }
}
