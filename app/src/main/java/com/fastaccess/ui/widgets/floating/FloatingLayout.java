package com.fastaccess.ui.widgets.floating;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.KeyEvent;

import com.fastaccess.ui.modules.floating.folders.drawer.FloatingDrawerMvp;

import io.codetail.widget.RevealLinearLayout;

/**
 * Created by Kosh on 22 Oct 2016, 12:30 PM
 */

public class FloatingLayout extends RevealLinearLayout {

    private FloatingDrawerMvp.View viewCallback;

    public FloatingLayout(Context context) {
        this(context, null);
    }

    public FloatingLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (viewCallback != null && isShown()) {
                viewCallback.onBackPressed();
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (viewCallback != null) viewCallback.onConfigChanged(newConfig.orientation);
    }

    public void setViewCallback(FloatingDrawerMvp.View viewCallback) {
        this.viewCallback = viewCallback;
    }
}
