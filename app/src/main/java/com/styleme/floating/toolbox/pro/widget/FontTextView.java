package com.styleme.floating.toolbox.pro.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.styleme.floating.toolbox.pro.R;
import com.styleme.floating.toolbox.pro.global.helper.AppHelper;

/**
 * Created by Kosh on 8/18/2015. copyrights are reserved
 */
public class FontTextView extends AppCompatTextView {

    public FontTextView(Context context) {
        super(context);
        init(null);
    }

    public FontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public FontTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (isInEditMode()) return;
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.FontTextView);
            boolean isColorful = a.getBoolean(R.styleable.FontTextView_colorful, false);
            int color = AppHelper.getAccentColor(getContext());
            if (isColorful) {
                setTextColor(color);
            }
            for (Drawable drawable : getCompoundDrawables()) { // tint any icon assigned to the textview
                if (drawable != null) {
                    ColorFilter colorFilter = new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN);
                    drawable.setColorFilter(colorFilter);
                }
            }
            a.recycle();
        }
        Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/app_font.ttf");
        setTypeface(myTypeface);
    }
}
