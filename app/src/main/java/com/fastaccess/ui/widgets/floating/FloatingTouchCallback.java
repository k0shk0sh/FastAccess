package com.fastaccess.ui.widgets.floating;

/**
 * Created by Kosh on 14 Oct 2016, 7:31 PM
 */

public interface FloatingTouchCallback {
    void onViewMoving(int x, int y);

    void onSingleTapped();

    void onDoubleTapped();

    void onLongPressed();

    void onSwipe(int swipeDirection);

    void onBackPressed();

    void onTouchOutside();

    void onStoppedMoving();

    void onConfigChanged(int orientation);
}
