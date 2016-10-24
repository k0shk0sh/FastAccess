package com.fastaccess.ui.modules.settings.dialogs;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.FrameLayout;

import com.fastaccess.R;
import com.fastaccess.ui.base.BaseBottomSheetDialog;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Kosh on 19 Oct 2016, 8:08 PM
 */

public class CustomIconChooserDialog extends BaseBottomSheetDialog {

    @BindView(R.id.fromIconPack) FrameLayout fromIconPack;
    @BindView(R.id.fromGallery) FrameLayout fromGallery;

    public interface OnCustomIconChooseCallback {
        void onUserChoose(boolean isFromGallery);
    }

    private OnCustomIconChooseCallback callback;

    @OnClick(value = {R.id.fromIconPack, R.id.fromGallery}) void onClick(View view) {
        callback.onUserChoose(view.getId() == R.id.fromGallery);
        dismiss();
    }

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        if (!(getParentFragment() instanceof OnCustomIconChooseCallback)) {
            throw new RuntimeException("Parent Fragment must implement OnCustomIconChooseCallback");
        }
        callback = (OnCustomIconChooseCallback) getParentFragment();
    }

    @Override public void onDetach() {
        super.onDetach();
        callback = null;
    }

    @Override protected int layoutRes() {
        return R.layout.icon_chooser_layout;
    }

    @Override protected void onViewCreated(@NonNull View view) {

    }
}
