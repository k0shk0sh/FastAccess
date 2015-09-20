package com.styleme.floating.toolbox.pro.widget.impl;

import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Created by Kosh on 9/4/2015. copyrights are reserved
 */
public class GestureListener extends GestureDetector.SimpleOnGestureListener {

    private OnFloatingTouchListener onFloatingTouchListener;

    public GestureListener(OnFloatingTouchListener onFloatingTouchListener) {this.onFloatingTouchListener = onFloatingTouchListener;}

    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        onFloatingTouchListener.onDoubleClick();
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        onFloatingTouchListener.onClick();
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        onFloatingTouchListener.onLongClick();
    }
}
