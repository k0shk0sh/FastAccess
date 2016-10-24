package com.fastaccess.ui.modules.cloud.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fastaccess.R;
import com.fastaccess.helper.Logger;
import com.fastaccess.ui.base.mvp.presenter.BasePresenter;
import com.fastaccess.ui.modules.cloud.backup.BackupView;
import com.fastaccess.ui.modules.cloud.restore.RestoreView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

/**
 * Created by Kosh on 23 Oct 2016, 7:49 PM
 */

public class LoginPresenter extends BasePresenter<LoginMvp.View> implements LoginMvp.Presenter {

    private static final int SIGN_IN_REQUEST_CODE = 100;

    protected LoginPresenter(@NonNull LoginMvp.View view) {
        super(view);
    }

    public static LoginPresenter with(@NonNull LoginMvp.View view) {
        return new LoginPresenter(view);
    }

    @Override public void onSignIn(@NonNull LoginView loginView, @NonNull GoogleApiClient googleApiClient) {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        loginView.startActivityForResult(signInIntent, SIGN_IN_REQUEST_CODE);
        loginView.onShowProgress();
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SIGN_IN_REQUEST_CODE) {
            if (isAttached()) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if (result.isSuccess()) {
                    GoogleSignInAccount account = result.getSignInAccount();
                    if (account != null) {
                        getView().onSignedIn(account);
                    } else {
                        getView().onShowMessage(R.string.failed_login);
                    }
                } else {
                    getView().onShowMessage(R.string.failed_login);
                }
            }
        }

    }

    @Override public void onFirebaseSignIn(@NonNull GoogleSignInAccount acct, @NonNull FirebaseAuth auth) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential).addOnCompleteListener(this);
    }

    @Override public void onStartBackOrRestore(int type, @NonNull LoginView loginView) {
        Intent intent = new Intent(loginView, type == LoginView.BACKUP_TYPE ? BackupView.class : RestoreView.class);
        loginView.startActivity(intent);
        loginView.finish();
    }

    @Override public void onConnected(@Nullable Bundle bundle) {
        if (isAttached()) getView().onConnected();
    }

    @Override public void onConnectionSuspended(int i) {
        //TODO
    }

    @Override public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (isAttached()) {
            if (connectionResult.getErrorMessage() != null) {
                getView().onShowMessage(connectionResult.getErrorMessage());
            }
            getView().onConnectionFailed(connectionResult);
        }
    }

    @Override public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (isAttached()) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                getView().onFirebaseUser(user);
            } else {
                Logger.e();
            }
        }
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored") @Override public void onComplete(@NonNull Task<AuthResult> task) {
        Logger.e(task.isSuccessful(), task.isComplete());
        if (isAttached()) {
            if (!task.isSuccessful()) {
                if (task.getException() != null && task.getException().getMessage() != null) {
                    getView().onShowMessage(task.getException().getMessage());
                } else {
                    getView().onShowMessage(R.string.failed_login);
                }
            }
        }
    }
}
