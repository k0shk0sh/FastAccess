package com.fastaccess.ui.widgets.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.helper.Bundler;
import com.fastaccess.ui.base.BaseBottomSheetDialog;
import com.fastaccess.ui.widgets.FontTextView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Kosh on 16 Sep 2016, 2:15 PM
 */

public class MessageDialogView extends BaseBottomSheetDialog {

    public interface MessageDialogViewActionCallback {
        void onMessageDialogActionClicked(boolean isOk, int requestCode);

        void onDialogDismissed();
    }

    @BindView(R.id.title) FontTextView title;

    @BindView(R.id.message) FontTextView message;

    @Nullable private MessageDialogViewActionCallback callback;

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() != null && getParentFragment() instanceof MessageDialogViewActionCallback) {
            callback = (MessageDialogViewActionCallback) getParentFragment();
        } else if (context instanceof MessageDialogViewActionCallback) {
            callback = (MessageDialogViewActionCallback) context;
        }
    }

    @Override public void onDetach() {
        super.onDetach();
        callback = null;
    }

    @OnClick({R.id.cancel, R.id.ok}) public void onClick(View view) {
        if (callback != null) {
            isAlreadyHidden = true;
            callback.onMessageDialogActionClicked(view.getId() == R.id.ok, getArguments().getInt("requestCode"));
        }
        dismiss();
    }

    @Override protected int layoutRes() {
        return R.layout.message_dialog;
    }

    @Override protected void onViewCreated(@NonNull View view) {
        Bundle bundle = getArguments();
        title.setText(bundle.getInt("bundleTitle"));
        message.setText(bundle.getInt("bundleMsg"));
    }

    @Override protected void onDismissedByScrolling() {
        super.onDismissedByScrolling();
        if (callback != null) callback.onDialogDismissed();
    }

    @Override protected void onHidden() {
        if (callback != null) callback.onDialogDismissed();
        super.onHidden();
    }

    @NonNull public static MessageDialogView newInstance(@StringRes int bundleTitle, @StringRes int bundleMsg) {
        return newInstance(bundleTitle, bundleMsg, 0);
    }

    @NonNull public static MessageDialogView newInstance(@StringRes int bundleTitle, @StringRes int bundleMsg, int requestCode) {
        MessageDialogView messageDialogView = new MessageDialogView();
        messageDialogView.setArguments(Bundler
                .start()
                .put("bundleTitle", bundleTitle)
                .put("bundleMsg", bundleMsg)
                .put("requestCode", requestCode)
                .end());
        return messageDialogView;
    }
}
