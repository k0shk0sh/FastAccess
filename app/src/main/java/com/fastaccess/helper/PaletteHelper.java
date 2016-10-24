package com.fastaccess.helper;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;

/**
 * Created by Kosh on 11 May 2016, 8:42 PM
 */
public class PaletteHelper {

    public static final int PRIMARY_COLOR = Color.parseColor("#FF2A456B");

    public interface OnColorExtraction {
        void onColorExtracted(int color, int textColor);
    }

    public static void extractColor(@Nullable Bitmap bitmap, @NonNull final OnColorExtraction onColorExtraction) {
        if (bitmap == null) {
            onColorExtraction.onColorExtracted(PRIMARY_COLOR, Color.WHITE);
            return;
        }
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override public void onGenerated(Palette palette) {
                if (palette == null) {
                    onColorExtraction.onColorExtracted(PRIMARY_COLOR, Color.WHITE);
                    return;
                }
                int color = PRIMARY_COLOR;
                int textColor = Color.WHITE;
                if (palette.getVibrantSwatch() != null) {
                    Palette.Swatch muted = palette.getVibrantSwatch();
                    textColor = muted.getTitleTextColor();
                    color = muted.getRgb();
                } else if (palette.getLightVibrantSwatch() != null) {
                    Palette.Swatch muted = palette.getLightVibrantSwatch();
                    color = muted.getRgb();
                    textColor = muted.getTitleTextColor();
                } else if (palette.getDarkVibrantSwatch() != null) {
                    Palette.Swatch muted = palette.getDarkVibrantSwatch();
                    color = muted.getRgb();
                    textColor = muted.getTitleTextColor();
                } else if (palette.getLightMutedSwatch() != null) {
                    Palette.Swatch muted = palette.getLightMutedSwatch();
                    color = muted.getRgb();
                    textColor = muted.getTitleTextColor();
                } else if (palette.getMutedSwatch() != null) {
                    Palette.Swatch muted = palette.getMutedSwatch();
                    color = muted.getRgb();
                    textColor = muted.getTitleTextColor();
                }
                onColorExtraction.onColorExtracted(color, textColor);
            }
        });
    }

    public static void extractColor(@Nullable Drawable drawable, @NonNull OnColorExtraction onColorExtraction) {
        if (drawable == null) {
            onColorExtraction.onColorExtracted(PRIMARY_COLOR, Color.WHITE);
            return;
        }
        extractColor(((BitmapDrawable) drawable).getBitmap(), onColorExtraction);
    }

}
