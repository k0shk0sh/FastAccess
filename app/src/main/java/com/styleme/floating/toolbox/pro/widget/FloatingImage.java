package com.styleme.floating.toolbox.pro.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.widget.ImageView;

import com.styleme.floating.toolbox.pro.widget.impl.OnFloatingTouchListener;

/**
 * Created by Kosh on 10/10/2015. copyrights are reserved
 */
public class FloatingImage extends ImageView {

    private OnFloatingTouchListener onOrientationChanges;

    public FloatingImage(Context context) {
        super(context);
    }

    public FloatingImage(Context context, OnFloatingTouchListener onOrientationChanges) {
        super(context);
        this.onOrientationChanges = onOrientationChanges;
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (onOrientationChanges != null)
            onOrientationChanges.onOrientationChanged(newConfig.orientation);
    }
}
