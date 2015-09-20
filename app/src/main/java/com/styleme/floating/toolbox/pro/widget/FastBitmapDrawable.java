package com.styleme.floating.toolbox.pro.widget;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/**
 * Created by Kosh on 9/5/2015. copyrights are reserved
 */
public class FastBitmapDrawable extends Drawable {
    private Bitmap mBitmap;
    private int mAlpha;
    private int mWidth;
    private int mHeight;
    private final Paint mPaint = new Paint(Paint.FILTER_BITMAP_FLAG);

    public FastBitmapDrawable(Bitmap b) {
        mAlpha = 255;
        mBitmap = b;
        if (b != null) {
            mWidth = mBitmap.getWidth();
            mHeight = mBitmap.getHeight();
        } else {
            mWidth = mHeight = 0;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        final Rect r = getBounds();
        canvas.drawBitmap(mBitmap, r.left, r.top, mPaint);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void setAlpha(int alpha) {
        mAlpha = alpha;
        mPaint.setAlpha(alpha);
    }

    public void setFilterBitmap(boolean filterBitmap) {
        mPaint.setFilterBitmap(filterBitmap);
    }

    public int getAlpha() {
        return mAlpha;
    }

    @Override
    public int getIntrinsicWidth() {
        return mWidth;
    }

    @Override
    public int getIntrinsicHeight() {
        return mHeight;
    }

    @Override
    public int getMinimumWidth() {
        return mWidth;
    }

    @Override
    public int getMinimumHeight() {
        return mHeight;
    }

    public void setBitmap(Bitmap b) {
        mBitmap = b;
        if (b != null) {
            mWidth = mBitmap.getWidth();
            mHeight = mBitmap.getHeight();
        } else {
            mWidth = mHeight = 0;
        }
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }
}