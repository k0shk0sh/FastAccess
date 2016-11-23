package com.fastaccess.ui.modules.cloud.auth;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by Kosh on 23 Oct 2016, 7:33 PM
 */

public interface LoginMvp {

    interface View {
        void onShowProgress();

        void onHideProgress();

        void onShowMessage(@StringRes int resId);

        void onShowMessage(@NonNull String msg);

        void onConnected();

        void onConnectionFailed(@NonNull ConnectionResult connectionResult);

        void onSignedIn(@NonNull GoogleSignInAccount account);

        void onFirebaseUser(@NonNull FirebaseUser user);
    }

    interface Presenter extends GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
            FirebaseAuth.AuthStateListener, OnCompleteListener<AuthResult> {
        void onSignIn(@NonNull LoginView loginView, @NonNull GoogleApiClient googleApiClient);

        void onActivityResult(int requestCode, int resultCode, Intent data);

        void onFirebaseSignIn(@NonNull GoogleSignInAccount account, @NonNull FirebaseAuth auth);

        void onStartBackOrRestore(int type, @NonNull LoginView loginView);
    }
}
