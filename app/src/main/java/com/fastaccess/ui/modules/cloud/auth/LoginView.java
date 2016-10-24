package com.fastaccess.ui.modules.cloud.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ProgressBar;

import com.fastaccess.R;
import com.fastaccess.helper.Logger;
import com.fastaccess.ui.base.BaseActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.OnClick;
import icepick.State;

/**
 * Created by Kosh on 23 Oct 2016, 7:56 PM
 */

public class LoginView extends BaseActivity<LoginMvp.View, LoginPresenter> implements LoginMvp.View {

    public static final int BACKUP_TYPE = 0;
    public static final int RESTORE_TYPE = 1;
    public static final String TYPE = "type";

    @BindView(R.id.topProgress) ProgressBar topProgress;
    @BindView(R.id.signInBtn) SignInButton signInBtn;
    @State int type;

    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInOptions signInOptions;
    private ProgressDialog progressDialog;
    private LoginPresenter presenter;
    private FirebaseAuth firebaseAuth;


    @OnClick(R.id.signInBtn) void onSignIn() {
        getPresenter().onSignIn(this, getGoogleApiClient());
    }

    @Override protected int layout() {
        return R.layout.sign_in_layout;
    }

    @NonNull @Override protected LoginPresenter getPresenter() {
        if (presenter == null) presenter = LoginPresenter.with(this);
        return presenter;
    }

    @Override protected boolean isTransparent() {
        return false;
    }

    @Override protected boolean canBack() {
        return true;
    }

    @Override public void onShowProgress() {
        topProgress.setVisibility(View.VISIBLE);
        if (!getProgressDialog().isShowing()) getProgressDialog().show();
    }

    @Override public void onHideProgress() {
        topProgress.setVisibility(View.GONE);
        if (getProgressDialog().isShowing()) getProgressDialog().dismiss();
    }

    @Override public void onShowMessage(@StringRes int resId) {
        onShowMessage(getString(resId));
    }

    @Override public void onShowMessage(@NonNull String msg) {
        onHideProgress();
        Snackbar.make(signInBtn, msg, Snackbar.LENGTH_LONG).show();
    }

    @Override public void onConnected() {
        Logger.e();
    }

    @Override public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Logger.e();
    }

    @Override public void onSignedIn(@NonNull GoogleSignInAccount account) {
        getPresenter().onFirebaseSignIn(account, getFirebaseAuth());
    }

    @Override public void onFirebaseUser(@NonNull FirebaseUser user) {
        onHideProgress();
        getPresenter().onStartBackOrRestore(type, this);
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            type = getIntent().getExtras().getInt(TYPE);
        }
        setTitle(type == BACKUP_TYPE ? R.string.backup : R.string.restore);
        signInBtn.setSize(SignInButton.SIZE_WIDE);
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getPresenter().onActivityResult(requestCode, resultCode, data);
    }

    @Override protected void onStart() {
        super.onStart();
        getFirebaseAuth().addAuthStateListener(getPresenter());
    }

    @Override protected void onStop() {
        super.onStop();
        getFirebaseAuth().removeAuthStateListener(getPresenter());
    }

    private ProgressDialog getProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
            progressDialog.setMessage(getString(R.string.in_progress));
        }
        return progressDialog;
    }

    private GoogleApiClient getGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, getPresenter())
                    .addApi(Auth.GOOGLE_SIGN_IN_API, getSignInOptions())
                    .build();
        }
        return mGoogleApiClient;
    }

    private GoogleSignInOptions getSignInOptions() {
        if (signInOptions == null) {
            signInOptions = new GoogleSignInOptions
                    .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
        }
        return signInOptions;
    }

    public FirebaseAuth getFirebaseAuth() {
        if (firebaseAuth == null) {
            firebaseAuth = FirebaseAuth.getInstance();
        }
        return firebaseAuth;
    }
}
