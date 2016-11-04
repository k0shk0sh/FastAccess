package com.fastaccess.helper;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.view.ViewGroup;

import com.fastaccess.BuildConfig;
import com.fastaccess.R;

/**
 * Created by Kosh on 16 Oct 2016, 4:48 AM
 */

public class PrefConstant {

    public static final String FLOATING_MODE = "floating_mode";
    public static final String FA_AUTO_SAVE_POSITION = "fa_auto_save_position";
    public static final String FA_EDGES_STICKY = "fa_edges_sticky";
    public static final String FA_ALWAYS_SHOWING = "fa_always_showing";
    public static final String STATUS_BAR_HIDDEN = "status_bar_hidden";
    public static final String FA_BACKGROUND = "fa_background";
    public static final String FA_BACKGROUND_ALPHA = "fa_background_alpha";
    public static final String ICON_PADDING = "icon_padding";
    public static final String ICON_PACK = "icon_pack";
    public static final String CUSTOM_ICON = "custom_icon";
    public static final String AUTO_TRANS = "auto_trans";
    public static final String ICON_ALPHA = "icon_alpha";
    public static final String ICON_SIZE = "icon_size";
    public static final String MANUAL_SIZE = "manual_size";
    public static final String POSITION_X = "floating_position_x";
    public static final String POSITION_Y = "floating_position_y";
    public static final String FA_AUTO_START = "fa_auto_start";
    public static final String FA_IS_HORIZONTAL = "fa_is_horizontal";

    public static final String WHATS_NEW_VERSION = "whats_new_version";

    public static void savePosition(int x, int y) {
        boolean isAutoSavePosition = PrefHelper.getBoolean(PrefConstant.FA_AUTO_SAVE_POSITION);
        if (isAutoSavePosition) {
            Logger.e(x, y);
            PrefHelper.set(POSITION_X, x);
            PrefHelper.set(POSITION_Y, y);
        }
    }

    public static int getFinalSize(@NonNull Context context) {
        int imageSize = ViewGroup.MarginLayoutParams.WRAP_CONTENT;
        int iconSize = PrefHelper.getInt(PrefConstant.MANUAL_SIZE);
        String size = PrefHelper.getString(PrefConstant.ICON_SIZE);
        if (iconSize > 0) {
            imageSize = ViewHelper.toPx(context, iconSize);
        } else {
            if (InputHelper.isEmpty(size)) size = "medium";
            if (size.equalsIgnoreCase("small")) {
                imageSize = context.getResources().getDimensionPixelSize(R.dimen.fa_size_small);
            } else if (size.equalsIgnoreCase("medium")) {
                imageSize = context.getResources().getDimensionPixelSize(R.dimen.fa_size_medium);
            } else if (size.equalsIgnoreCase("large")) {
                imageSize = context.getResources().getDimensionPixelSize(R.dimen.fa_size_large);
            }
        }
        return imageSize;
    }

    public static int getGapSize(@NonNull Resources resources) {
        String gap = PrefHelper.getString(ICON_PADDING);
        int gapSize = resources.getDimensionPixelSize(R.dimen.spacing_normal);
        if (!InputHelper.isEmpty(gap)) {
            if (gap.equalsIgnoreCase("small")) {
                gapSize = resources.getDimensionPixelSize(R.dimen.spacing_micro);
            } else if (gap.equalsIgnoreCase("medium")) {
                gapSize = resources.getDimensionPixelSize(R.dimen.spacing_normal);

            } else if (gap.equalsIgnoreCase("large")) {
                gapSize = resources.getDimensionPixelSize(R.dimen.spacing_xs_large);
            } else {
                gapSize = resources.getDimensionPixelSize(R.dimen.spacing_normal);
            }
        }
        return gapSize;
    }

    public static boolean isAutoStart() {
        return PrefHelper.getBoolean(FA_AUTO_START);
    }

    public static boolean isHorizontal() {
        return PrefHelper.getBoolean(FA_IS_HORIZONTAL);
    }

    public static boolean showWhatsNew() {
        return PrefHelper.getInt(WHATS_NEW_VERSION) != BuildConfig.VERSION_CODE;
    }

    public static void setWhatsNewVersion() {
        PrefHelper.set(WHATS_NEW_VERSION, BuildConfig.VERSION_CODE);
    }
}
