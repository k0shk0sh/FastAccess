package com.styleme.floating.toolbox.pro.global.adapter;

import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.styleme.floating.toolbox.pro.R;
import com.styleme.floating.toolbox.pro.global.helper.AppHelper;
import com.styleme.floating.toolbox.pro.global.model.AppsModel;
import com.styleme.floating.toolbox.pro.widget.impl.OnFloatingTouchListener;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Kosh on 9/4/2015. copyrights are reserved
 */
public class FloatingAdapter extends BaseAdapter {

    private List<AppsModel> appsModels;
    private OnFloatingTouchListener onFloatingTouchListener;

    public FloatingAdapter(List<AppsModel> appsModels, OnFloatingTouchListener onFloatingTouchListener) {
        this.appsModels = appsModels;
        this.onFloatingTouchListener = onFloatingTouchListener;
    }

    @Override
    public int getCount() {
        return appsModels.size();
    }

    @Override
    public Object getItem(int position) {
        return appsModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        Holder holder;
        if (view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.floating_layout_items, parent, false);
            holder = new Holder(view);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.appIcon.getLayoutParams();
        String gap = AppHelper.getGapSize(holder.appHolder.getContext());
        if (gap.equalsIgnoreCase("small")) {
            int gapSize = holder.appHolder.getResources().getDimensionPixelSize(R.dimen.gap_small);
            params.setMargins(0, 0, 0, gapSize);

        } else if (gap.equalsIgnoreCase("medium")) {
            int gapSize = holder.appHolder.getResources().getDimensionPixelSize(R.dimen.gap_medium);
            params.setMargins(0, 0, 0, gapSize);
        } else if (gap.equalsIgnoreCase("large")) {
            int gapSize = holder.appHolder.getResources().getDimensionPixelSize(R.dimen.gap_large);
            params.setMargins(0, 0, 0, gapSize);
        } else {
            int gapSize = holder.appHolder.getResources().getDimensionPixelSize(R.dimen.gap_medium);
            params.setMargins(0, 0, 0, gapSize);
        }
        final AppsModel app = appsModels.get(position);
        holder.appIcon.setImageDrawable(new BitmapDrawable(view.getResources(), app.getBitmap()));
        holder.appHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFloatingTouchListener.onAppClick(app);
            }
        });
        return view;
    }

    public void clear() {
        appsModels.clear();
        notifyDataSetChanged();
    }

    public void insert(List<AppsModel> data) {
        appsModels.clear();
        appsModels.addAll(data);
        notifyDataSetChanged();
    }

    static class Holder {
        @Bind(R.id.appIcon)
        ImageView appIcon;
        @Bind(R.id.appHolder)
        View appHolder;

        Holder(View view) {ButterKnife.bind(this, view);}
    }
}
