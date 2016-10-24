package com.fastaccess.ui.widgets;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

/**
 * Created by Kosh on 29 Apr 2016, 5:47 PM
 */
public class FontFitTextView extends FontTextView {
    private Paint paint;

    public FontFitTextView(Context var1) {
        super(var1);
        this.init();
    }

    public FontFitTextView(Context var1, AttributeSet var2) {
        super(var1, var2);
        this.init();
    }

    @Override protected void onMeasure(int var1, int var2) {
        super.onMeasure(var1, var2);
        var1 = MeasureSpec.getSize(var1);
        var2 = this.getMeasuredHeight();
        this.resize(this.getText().toString(), var1);
        this.setMeasuredDimension(var1, var2);
    }

    @Override protected void onTextChanged(CharSequence var1, int var2, int var3, int var4) {
        this.resize(var1.toString(), this.getWidth());
    }

    @Override protected void onSizeChanged(int var1, int var2, int var3, int var4) {
        if (var1 != var3) {
            this.resize(this.getText().toString(), var1);
        }

    }

    private void init() {
        this.paint = new Paint();
        this.paint.set(this.getPaint());
    }

    private void resize(String var1, int var2) {
        if (var2 > 0) {
            var2 = var2 - this.getPaddingLeft() - this.getPaddingRight();
            DisplayMetrics var3 = this.getResources().getDisplayMetrics();
            float var4 = 32.0F * var3.density;
            float var11 = 6.0F * var3.density;
            this.paint.set(this.getPaint());

            while (var4 - var11 > 0.5F) {
                float var5 = (var4 + var11) / 2.0F;
                this.paint.setTextSize(var5);
                FontFitTextView var6 = this;
                float var7;
                String[] var8;
                if ((var8 = var1.split("\n")) != null && var8.length != 0) {
                    var7 = this.paint.measureText(var8[0]);

                    for (int var9 = 1; var9 < var8.length; ++var9) {
                        float var10;
                        if ((var10 = var6.paint.measureText(var8[var9])) > var7) {
                            var7 = var10;
                        }
                    }
                } else {
                    var7 = this.paint.measureText(var1);
                }

                if (var7 >= (float) var2) {
                    var4 = var5;
                } else {
                    var11 = var5;
                }
            }

            this.setTextSize(0, var11);
        }
    }

}
