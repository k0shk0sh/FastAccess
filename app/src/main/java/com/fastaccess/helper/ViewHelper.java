package com.fastaccess.helper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.support.annotation.ColorInt;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fastaccess.R;
import com.tooltip.OnDismissListener;
import com.tooltip.Tooltip;

import java.util.Arrays;


/**
 * Created by kosh20111 on 10/7/2015 10:42 PM
 */
public class ViewHelper {

    public static int getAccentColor(Context context) {
        TypedValue typedValue = new TypedValue();
        TypedArray a = context.obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorAccent});
        int color = a.getColor(0, 0);
        a.recycle();
        return color;
    }

    public static int getPrimaryColor(Context context) {
        TypedValue typedValue = new TypedValue();
        TypedArray a = context.obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorPrimary});
        int color = a.getColor(0, 0);
        a.recycle();
        return color;
    }

    public static int getPrimaryDarkColor(Context context) {
        TypedValue typedValue = new TypedValue();
        TypedArray a = context.obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorPrimaryDark});
        int color = a.getColor(0, 0);
        a.recycle();
        return color;
    }

    public static int toPx(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, dp, context.getResources().getDisplayMetrics());
    }

    public static int toDp(Context context, int px) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px, context.getResources().getDisplayMetrics());
    }

    public static Drawable tintDrawable(Drawable drawable, int color) {
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, color);
        return drawable;
    }

    public static Drawable getDrawableSelector(int normalColor, int pressedColor) {
        if (AppHelper.isLollipopOrHigher()) {
            return new RippleDrawable(ColorStateList.valueOf(pressedColor), getRippleMask(normalColor), getRippleMask(normalColor));
        } else {
            return getStateListDrawable(normalColor, pressedColor);
        }
    }

    private static Drawable getRippleMask(int color) {
        float[] outerRadii = new float[8];
        Arrays.fill(outerRadii, 3);
        RoundRectShape r = new RoundRectShape(outerRadii, null, null);
        ShapeDrawable shapeDrawable = new ShapeDrawable(r);
        shapeDrawable.getPaint().setColor(color);
        return shapeDrawable;
    }

    private static StateListDrawable getStateListDrawable(int normalColor, int pressedColor) {
        StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{android.R.attr.state_pressed}, new ColorDrawable(pressedColor));
        states.addState(new int[]{android.R.attr.state_focused}, new ColorDrawable(pressedColor));
        states.addState(new int[]{android.R.attr.state_activated}, new ColorDrawable(pressedColor));
        states.addState(new int[]{android.R.attr.state_selected}, new ColorDrawable(pressedColor));
        states.addState(new int[]{}, new ColorDrawable(normalColor));
        return states;
    }

    public static ColorStateList textSelector(int normalColor, int pressedColor) {
        return new ColorStateList(
                new int[][]{
                        new int[]{android.R.attr.state_pressed},
                        new int[]{android.R.attr.state_focused},
                        new int[]{android.R.attr.state_activated},
                        new int[]{android.R.attr.state_selected},
                        new int[]{}
                },
                new int[]{
                        pressedColor,
                        pressedColor,
                        pressedColor,
                        pressedColor,
                        normalColor
                }
        );
    }

    public static int generateTextColor(int color) {
        return Color.rgb(255 - Color.red(color),
                255 - Color.green(color),
                255 - Color.blue(color));
    }

    public static int getDarkColor(@ColorInt int color) {
        if (color == 0) return color;
        float cl = 0.9f;
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= cl;
        return Color.HSVToColor(hsv);
    }

    private static boolean isTablet(Resources resources) {
        return (resources.getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static boolean isTablet(Context context) {
        return isTablet(context.getResources());
    }

    public static void setMenuCount(@NonNull NavigationView navigationView, @IdRes int itemId, @IdRes int txtId, int count) {
        if (navigationView.getMenu().findItem(itemId).getActionView() != null) {
            if (navigationView.getMenu().findItem(itemId).getActionView() instanceof TextView) {
                setTextViewMenuCounter(navigationView, itemId, count);
                return;
            }
            LinearLayout view = (LinearLayout) navigationView.getMenu().findItem(itemId).getActionView();
            ((TextView) view.findViewById(txtId)).setText(String.format("%s", count));
        }
    }

    private static void setTextViewMenuCounter(@NonNull NavigationView navigationView, @IdRes int itemId, int count) {
        TextView view = (TextView) navigationView.getMenu().findItem(itemId).getActionView();
        view.setText(String.format("%s", count));
    }

    public static boolean isLandscape(Resources resources) {
        return resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    public static void setFullSpan(@NonNull RecyclerView.ViewHolder holder) {
        StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
        if (params == null) {
            params = new StaggeredGridLayoutManager.LayoutParams(StaggeredGridLayoutManager.LayoutParams.MATCH_PARENT,
                    StaggeredGridLayoutManager.LayoutParams.WRAP_CONTENT);
        }
        params.setFullSpan(true);
        holder.itemView.setLayoutParams(params);
    }

    public static float getDeviceWidth(Context context) {
        Point size = new Point();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getSize(size);
        return size.x;
    }

    public static float getDeviceHeight(Context context) {
        Point size = new Point();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getSize(size);
        return size.y;
    }

    @SuppressWarnings("ConstantConditions") @SuppressLint("PrivateResource")
    public static void showTooltip(@NonNull final View view, @StringRes int titleResId, @NonNull final String tag, int gravity) {
        if (view != null && view.getContext() != null) {
            if (!PrefHelper.getBoolean(tag)) {
                new Tooltip.Builder(view)
                        .setText(titleResId)
                        .setTypeface(TypeFaceHelper.getTypeface())
                        .setTextColor(Color.WHITE)
                        .setGravity(gravity)
                        .setPadding(R.dimen.spacing_xs_large)
                        .setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.primary))
                        .setDismissOnClick(true)
                        .setCancelable(true)
                        .setTextStyle(android.support.v7.appcompat.R.style.Base_TextAppearance_AppCompat_Title_Inverse)
                        .setOnDismissListener(new OnDismissListener() {
                            @Override public void onDismiss() {
                                PrefHelper.set(tag, true);
                            }
                        })
                        .show();
            }
        }
    }

    @SuppressLint("PrivateResource") public static void showTooltip(@NonNull final View view, @StringRes int titleResId,
                                                                    @NonNull final String tag) {
        showTooltip(view, titleResId, tag, Gravity.BOTTOM);
    }

    public static Rect getLayoutPosition(@NonNull View view) {
        Rect myViewRect = new Rect();
        view.getGlobalVisibleRect(myViewRect);
        return myViewRect;
    }

    public static int getWidthFromRecyclerView(@NonNull RecyclerView recyclerView, @NonNull WindowManager windowManager) {
        int iconSize = PrefConstant.getFinalSize(recyclerView.getContext());
        int padding = PrefConstant.getGapSize(recyclerView.getResources());
        int count = recyclerView.getAdapter().getItemCount();
        int width = (count * (iconSize + padding)) + iconSize;
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        Logger.e(width <= metrics.widthPixels);
        return width <= metrics.widthPixels ? width : metrics.widthPixels;
    }
}
