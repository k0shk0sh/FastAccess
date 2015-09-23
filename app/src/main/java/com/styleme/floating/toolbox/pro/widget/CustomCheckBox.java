package com.styleme.floating.toolbox.pro.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.AttributeSet;

import com.styleme.floating.toolbox.pro.R;
import com.styleme.floating.toolbox.pro.global.helper.AppHelper;

/**
 * Created by kosh20111 on 5/27/2015. CopyRights @ Innov8tif
 */
public class CustomCheckBox extends AppCompatCheckBox {

    private ColorStateList tint;

    public CustomCheckBox(Context context) {
        super(context);
        updateTintColor();
    }

    public CustomCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        updateTintColor();

    }

    public CustomCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        updateTintColor();

    }

    private void updateTintColor() {
        ColorStateList colorStateList = new ColorStateList(new int[][]{
                new int[]{-android.R.attr.state_checked},
                new int[]{android.R.attr.state_checked}
        }, new int[]{
                AppHelper.getPrimaryColor(getContext()),
                AppHelper.getAccentColor(getContext())
        });
        if (Build.VERSION.SDK_INT >= 21) {
            setButtonTintList(colorStateList);
            return;
        }
        Drawable wrap = DrawableCompat.wrap(ContextCompat.getDrawable(getContext(), R.drawable.abc_btn_check_material));
        DrawableCompat.setTintList(wrap, colorStateList);
        setButtonDrawable(wrap);
    }

    private int resolveColor(Context context, int i) {
        TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{i});
        try {
            return a.getColor(0, i);
        } finally {
            a.recycle();
        }
    }

}
