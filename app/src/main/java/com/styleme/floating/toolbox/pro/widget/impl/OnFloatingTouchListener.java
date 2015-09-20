package com.styleme.floating.toolbox.pro.widget.impl;

import android.view.MotionEvent;
import android.view.View;

import com.styleme.floating.toolbox.pro.global.model.AppsModel;

/**
 * Created by Kosh on 9/4/2015. copyrights are reserved
 */
public interface OnFloatingTouchListener {

    void onLongClick();

    void onDoubleClick();

    void onClick();

    boolean onTouch(View v, MotionEvent event);

    void onAppClick(AppsModel appsModel);

    void onReset();
}
