package com.fastaccess.ui.widgets;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;

import com.fastaccess.R;


/**
 * Created by kosh on 7/30/2015. CopyRights @ Innov8tif
 */
public class AppbarRefreshLayout extends SwipeRefreshLayout implements AppBarLayout.OnOffsetChangedListener {
    private AppBarLayout appBarLayout;
    private boolean isReallyDisabled = false;

    public AppbarRefreshLayout(Context context) {
        super(context, null);
    }

    public AppbarRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setColorSchemeResources(R.color.primary, R.color.primary_dark, R.color.primary_light, R.color.accent);
    }

    public void setReallyDisable() {
        this.setEnabled(false);
        isReallyDisabled = true;
    }

    @Override protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (getContext() instanceof Activity) {
            appBarLayout = (AppBarLayout) ((Activity) getContext()).findViewById(R.id.appbar);
            if (appBarLayout != null) {
                appBarLayout.addOnOffsetChangedListener(this);
            }
        }
    }

    @Override protected void onDetachedFromWindow() {
        if (appBarLayout != null) {
            appBarLayout.removeOnOffsetChangedListener(this);
            appBarLayout = null;
        }
        super.onDetachedFromWindow();
    }

    @Override public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        if (!isReallyDisabled) {
            this.setEnabled(i == 0);
        }
    }
}
