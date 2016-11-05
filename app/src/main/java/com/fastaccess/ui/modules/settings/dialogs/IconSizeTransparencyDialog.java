package com.fastaccess.ui.modules.settings.dialogs;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.helper.Bundler;
import com.fastaccess.helper.PrefConstant;
import com.fastaccess.helper.PrefHelper;
import com.fastaccess.helper.ViewHelper;
import com.fastaccess.ui.base.BaseBottomSheetDialog;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.ForegroundImageView;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Kosh on 16 Oct 2016, 6:46 PM
 */

public class IconSizeTransparencyDialog extends BaseBottomSheetDialog implements DiscreteSeekBar.OnProgressChangeListener {

    @BindView(R.id.done) ForegroundImageView done;
    @BindView(R.id.valueText) FontTextView valueText;
    @BindView(R.id.seekBar) DiscreteSeekBar seekBar;
    @BindView(R.id.toolbar) Toolbar toolbar;
    private boolean isBackground;
    private boolean isSize;

    public static IconSizeTransparencyDialog newInstance(boolean isBackground) {
        IconSizeTransparencyDialog dialog = new IconSizeTransparencyDialog();
        dialog.setArguments(Bundler.start().put("isBackground", isBackground).end());
        return dialog;
    }

    public static IconSizeTransparencyDialog newInstance(boolean isBackground, boolean isSize) {
        IconSizeTransparencyDialog dialog = new IconSizeTransparencyDialog();
        dialog.setArguments(Bundler.start().put("isBackground", isBackground).put("isSize", isSize).end());
        return dialog;
    }

    @OnClick(R.id.done) void onDone() {
        if (!isSize) {
            PrefHelper.set(isBackground ? PrefConstant.FA_BACKGROUND_ALPHA : PrefConstant.ICON_ALPHA, seekBar.getProgress());
        } else {
            PrefHelper.set(PrefConstant.MANUAL_SIZE, seekBar.getProgress());
        }
        dismiss();
    }

    @Override protected int layoutRes() {
        return R.layout.transparency_layout;
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isSize = getArguments().getBoolean("isSize");
        isBackground = getArguments().getBoolean("isBackground");
    }

    @Override protected void onViewCreated(@NonNull View view) {
        if (isSize) toolbar.setTitle(R.string.change_size);
        seekBar.setOnProgressChangeListener(this);
        if (isSize) {
            seekBar.setMin(100);
            seekBar.setMax(300);
            seekBar.setProgress(PrefHelper.getInt(PrefConstant.MANUAL_SIZE));
        } else {
            seekBar.setMin(isBackground ? 0 : 20);
            seekBar.setProgress(PrefHelper.getInt(isBackground ? PrefConstant.FA_BACKGROUND_ALPHA : PrefConstant.ICON_ALPHA));
        }
    }

    @Override public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
        changeIconAlpha(value);
    }

    private void changeIconAlpha(int value) {
        valueText.setText(String.format("%s: %s", getString(R.string.value), value));
        Drawable drawable = valueText.getCompoundDrawables()[2];//end drawable
        if (!isSize) {
            if (drawable != null) drawable.setAlpha(value);
        } else {
            if (drawable != null) {
                int height = ViewHelper.toPx(getContext(), value);
                drawable.setBounds(0, 0, height, height);
                valueText.getLayoutParams().height = height;
            }
        }
    }

    @Override public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

    }

    @Override public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

    }
}
