package com.fastaccess.ui.adapter.viewholder;

import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;

import com.fastaccess.R;
import com.fastaccess.data.dao.AppsModel;
import com.fastaccess.helper.ViewHelper;
import com.fastaccess.ui.widgets.FastBitmapDrawable;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;
import com.fastaccess.ui.widgets.recyclerview.touch.ItemTouchHelperViewHolder;

import butterknife.BindView;

/**
 * Created by Kosh on 30 Aug 2016, 11:42 PM
 */

public class DeviceAppsViewHolder extends BaseViewHolder<AppsModel> implements ItemTouchHelperViewHolder {
    @BindView(R.id.appIcon) ImageView appIcon;
    @BindView(R.id.cardView) CardView cardView;
    @ColorInt private final int selectedColor;
    @ColorInt private final int normalColor;
    private boolean selected;
    private boolean selectedApps;

    public DeviceAppsViewHolder(@NonNull View itemView, @Nullable BaseRecyclerAdapter adapter) {
        super(itemView, adapter);
        selectedColor = ActivityCompat.getColor(itemView.getContext(), R.color.light_gray);
        normalColor = ActivityCompat.getColor(itemView.getContext(), R.color.cardview_light_background);
        appIcon.setOnClickListener(this);
        appIcon.setOnLongClickListener(this);
    }

    public void bind(@NonNull AppsModel model, boolean selected, boolean selectedApps) {
        this.selected = selected;
        this.selectedApps = selectedApps;
        bind(model);
    }

    @Override public void bind(@NonNull AppsModel model) {
        FastBitmapDrawable drawable = new FastBitmapDrawable(model.getBitmap());
        appIcon.setImageDrawable(drawable);
        appIcon.setContentDescription(model.getAppName());
        drawable.setGhostModeEnabled(selected);
        drawable.setPressed(selected);
        cardView.setCardBackgroundColor(selected ? selectedColor : normalColor);
        if (getAdapterPosition() == 0) {
            if (selectedApps) {
                ViewHelper.showTooltip(itemView, R.string.drag_and_drop_hint);
            } else {
                ViewHelper.showTooltip(itemView, R.string.single_click_to_select_hint);
            }
        }
    }

    @Override public void onItemSelected() {
        cardView.setAlpha(0.5f);
    }

    @Override public void onItemClear() {
        cardView.setAlpha(1);
    }
}
