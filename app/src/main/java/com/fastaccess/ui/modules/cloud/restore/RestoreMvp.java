package com.fastaccess.ui.modules.cloud.restore;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Kosh on 23 Oct 2016, 8:56 PM
 */

public interface RestoreMvp {

    interface View {
        void onShowProgress();

        void onHideProgress();

        void onShowMessage(@StringRes int resId);

        void onShowMessage(@NonNull String msg);

        void finishOnError();

        void onRestoreCompleted();

        @Nullable FirebaseUser user();
    }

    interface Presenter extends ValueEventListener {

        void onRestore(@NonNull DatabaseReference databaseReference, @Nullable String userId);
    }
}
