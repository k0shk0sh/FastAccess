package com.fastaccess.ui.widgets;

import android.support.v4.view.ViewPager;
import android.view.View;

import com.fastaccess.R;

public class IntroTransformer implements ViewPager.PageTransformer {

    private void setAlpha(View view, float value) {
        view.animate().alpha(value);
    }

    private void setTranslationX(View view, float value) {
        view.animate().translationX(value);
    }

    @Override public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();
        View message = view.findViewById(R.id.introTitle);
        View title = view.findViewById(R.id.introDescription);
        View image = view.findViewById(R.id.introImage);
        if (position >= -1) {
            if (position <= 0) {
                setTranslationX(view, -position);
                setTranslationX(message, pageWidth * position);
                setTranslationX(title, pageWidth * position);
                image.animate().scaleX(1 + position).scaleY(1 + position);
                setAlpha(message, 1 + position);
                setAlpha(title, 1 + position);
                setAlpha(image, 1 + position);
            } else if (position <= 1) { // (0,1]
                setTranslationX(view, position);
                setTranslationX(message, pageWidth * position);
                setTranslationX(title, pageWidth * position);
                image.animate().scaleX(1 - position).scaleY(1 - position);
                setAlpha(image, 1 - position);
                setAlpha(message, 1 - position);
                setAlpha(title, 1 - position);
                setAlpha(image, 1 - position);
            }
        }
    }
}
