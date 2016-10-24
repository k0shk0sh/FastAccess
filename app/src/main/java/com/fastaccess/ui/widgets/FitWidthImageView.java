package com.fastaccess.ui.widgets;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by kosh on 12/7/2014. CopyRights @ innov8tif.com
 */
public class FitWidthImageView extends ImageView {

    /**
     * Instantiates a new Fit width image.
     *
     * @param paramContext
     *         the param context
     */
    public FitWidthImageView(Context paramContext) {
        super(paramContext);

    }

    /**
     * Instantiates a new Fit width image.
     *
     * @param paramContext
     *         the param context
     * @param paramAttributeSet
     *         the param attribute set
     */
    public FitWidthImageView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
    }

    /**
     * Instantiates a new Fit width image.
     *
     * @param paramContext
     *         the param context
     * @param paramAttributeSet
     *         the param attribute set
     * @param paramInt
     *         the param int
     */
    public FitWidthImageView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
    }

    @Override protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        final Drawable d = this.getDrawable();
        if (d != null) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = width * d.getIntrinsicHeight() / d.getIntrinsicWidth();
            setMeasuredDimension(width, height);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
