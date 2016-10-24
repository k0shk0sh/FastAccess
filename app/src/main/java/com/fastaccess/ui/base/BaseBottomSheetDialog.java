package com.fastaccess.ui.base;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import com.fastaccess.helper.Logger;
import com.fastaccess.helper.ViewHelper;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import icepick.Icepick;

/**
 * Created by Kosh on 16 Sep 2016, 2:11 PM
 */

public abstract class BaseBottomSheetDialog extends BottomSheetDialogFragment {

    protected BottomSheetBehavior<View> bottomSheetBehavior;
    protected boolean isAlreadyHidden;
    @Nullable private Unbinder unbinder;

    @LayoutRes protected abstract int layoutRes();

    protected abstract void onViewCreated(@NonNull View view);

    @Override public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && !savedInstanceState.isEmpty()) {
            Icepick.restoreInstanceState(this, savedInstanceState);
        }
    }

    @Override public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), layoutRes(), null);
        dialog.setContentView(contentView);
        View parent = ((View) contentView.getParent());
        bottomSheetBehavior = BottomSheetBehavior.from(parent);
        if (bottomSheetBehavior != null) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                @Override public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        isAlreadyHidden = true;
                        onHidden();
                    }
                }

                @Override public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                    if (slideOffset == -1.0) {
                        isAlreadyHidden = true;
                        onDismissedByScrolling();
                    }
                }
            });
        }
        unbinder = ButterKnife.bind(this, contentView);
        onViewCreated(contentView);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) unbinder.unbind();
    }

    @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (ViewHelper.isTablet(getContext())) {
            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override public void onShow(DialogInterface dialogINterface) {
                    if (dialog.getWindow() != null) dialog.getWindow().setLayout(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.MATCH_PARENT);
                }
            });
        }
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    isAlreadyHidden = true;
                    onDismissedByScrolling();
                }
                return false;
            }
        });

        return dialog;
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Logger.e();
    }

    @Override public void onDetach() {
        if (!isAlreadyHidden) {
            onDismissedByScrolling();
        }
        super.onDetach();
    }

    protected void onHidden() {
        dismiss();
    }

    protected void onDismissedByScrolling() {}

}