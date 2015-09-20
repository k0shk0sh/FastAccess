package com.styleme.floating.toolbox.pro.widget;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by Kosh on 9/19/2015. copyrights are reserved
 */
public class Transformer implements ViewPager.PageTransformer {

    public void transformPage(View page, float position) {
        final float alpha;
        final float translationX;
        if (position > 0 && position < 1) {
            // moving to the right
            alpha = (1 - position);
            translationX = 0.8f * page.getWidth() * -position;
        } else {
            // use default for all other cases
            alpha = 1;
            translationX = 0;
        }
        page.setAlpha(alpha);
        page.setTranslationX(translationX);
    }
}