package com.fastaccess.ui.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.fastaccess.R;


/**
 * Created by kosh20111 on 10/8/2015.
 * <p/>
 * Viewpager that has scrolling animation by default
 */
public class ViewPagerView extends ViewPager {

    private boolean isEnabled;

    public ViewPagerView(Context context) {
        super(context, null);
    }

    public ViewPagerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ViewPagerView);
        isEnabled = a.getBoolean(R.styleable.ViewPagerView_isEnabled, true);
        a.recycle();
    }

    @Override public boolean isEnabled() {
        return isEnabled;
    }

    @Override public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
        requestLayout();
    }

    @Override public boolean onTouchEvent(MotionEvent event) {
        return !isEnabled() || super.onTouchEvent(event);
    }

    @Override public boolean onInterceptTouchEvent(MotionEvent event) {
        return isEnabled() && super.onInterceptTouchEvent(event);
    }
}
