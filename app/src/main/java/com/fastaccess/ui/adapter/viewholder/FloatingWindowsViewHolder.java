package com.fastaccess.ui.adapter.viewholder;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.fastaccess.R;
import com.fastaccess.helper.PrefConstant;
import com.fastaccess.helper.PrefHelper;
import com.fastaccess.ui.modules.floating.BaseFloatingMvp;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import butterknife.Unbinder;

/**
 * Created by Kosh on 15 Oct 2016, 2:43 AM
 */

public class FloatingWindowsViewHolder {

    private BaseFloatingMvp.BaseView callback;
    private Unbinder unbinder;

    @BindView(R.id.toggleTapBar) public ImageView toggleTapBar;
    @BindView(R.id.recycler) public DynamicRecyclerView recycler;
    @BindView(R.id.tabBar) public View tabBar;

    @OnTouch(R.id.tabBar) boolean onTouch(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            if (callback != null) callback.onTouchedOutside();
        }
        return false;
    }

    @OnClick(R.id.toggleTapBar) void onToggle() {
        if (callback != null) callback.onToggleVisibility(true);
    }

    public FloatingWindowsViewHolder(@NonNull View view, @NonNull BaseFloatingMvp.BaseView callback) {
        this.callback = callback;
        unbinder = ButterKnife.bind(this, view);
        onSetupBackground();
    }

    public void onSetupBackground() {
        int bgColor = PrefHelper.getInt(PrefConstant.FA_BACKGROUND);
        int bgAlpha = PrefHelper.getInt(PrefConstant.FA_BACKGROUND_ALPHA);
        Drawable drawable = new ColorDrawable(bgColor == 0 ? Color.TRANSPARENT : bgColor);
        drawable.setAlpha(bgAlpha == 0 ? 255 : bgAlpha);
        tabBar.setBackground(drawable);
    }

    public void onDestroy() {
        callback = null;
        callback = null;
        if (unbinder != null) unbinder.unbind();
    }
}
