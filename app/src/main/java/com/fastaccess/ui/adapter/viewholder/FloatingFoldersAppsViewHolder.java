package com.fastaccess.ui.adapter.viewholder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.fastaccess.R;
import com.fastaccess.data.dao.AppsModel;
import com.fastaccess.helper.PrefConstant;
import com.fastaccess.ui.widgets.FastBitmapDrawable;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import butterknife.BindView;
import butterknife.OnTouch;

/**
 * Created by Kosh on 30 Aug 2016, 11:42 PM
 */

public class FloatingFoldersAppsViewHolder extends BaseViewHolder<AppsModel> {

    @BindView(R.id.imageIcon) ImageView imageIcon;
    @BindView(R.id.iconHolder) RelativeLayout iconHolder;
    private boolean isHorizontal;

    @OnTouch(R.id.imageIcon) boolean onTouch(MotionEvent event) {
        FastBitmapDrawable drawable = (FastBitmapDrawable) imageIcon.getDrawable();
        if (drawable != null) {
            if (event.getAction() == MotionEvent.ACTION_UP ||
                    event.getAction() == MotionEvent.ACTION_CANCEL) {
                drawable.setPressed(false);
            } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                drawable.setPressed(true);
            }
        }
        return false;
    }

    public static FloatingFoldersAppsViewHolder newInstance(@NonNull ViewGroup parent, @NonNull BaseRecyclerAdapter adapter) {
        return new FloatingFoldersAppsViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.floating_folder_apps_row_item, parent, false), adapter);
    }

    public FloatingFoldersAppsViewHolder(@NonNull View itemView, @Nullable BaseRecyclerAdapter adapter) {
        super(itemView, adapter);
        iconHolder.setOnClickListener(null);
        iconHolder.setOnLongClickListener(null);
        imageIcon.setOnClickListener(this);
        imageIcon.setOnLongClickListener(this);
    }

    public void bind(@NonNull AppsModel model, boolean isHorizontal) {
        this.isHorizontal = isHorizontal;
        bind(model);
    }

    @Override public void bind(@NonNull AppsModel model) {
        imageIcon.setImageDrawable(new FastBitmapDrawable(model.getBitmap()));
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageIcon.getLayoutParams();
        int gap = PrefConstant.getGapSize(imageIcon.getResources());
        if (!isHorizontal) params.setMargins(0, 0, 0, gap);
        else params.setMargins(0, 0, gap, 0);
    }
}
