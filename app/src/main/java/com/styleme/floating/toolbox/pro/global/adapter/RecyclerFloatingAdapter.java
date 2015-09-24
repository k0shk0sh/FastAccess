package com.styleme.floating.toolbox.pro.global.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.styleme.floating.toolbox.pro.R;
import com.styleme.floating.toolbox.pro.global.helper.AppHelper;
import com.styleme.floating.toolbox.pro.global.model.AppsModel;
import com.styleme.floating.toolbox.pro.widget.FastBitmapDrawable;
import com.styleme.floating.toolbox.pro.widget.impl.OnItemClickListener;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Kosh on 8/16/2015. copyrights are reserved
 */
public class RecyclerFloatingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<AppsModel> modelList;
    private OnItemClickListener onClick;

    public RecyclerFloatingAdapter(OnItemClickListener onClick, List<AppsModel> modelList) {
        this.modelList = modelList;
        this.onClick = onClick;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.floating_items, parent, false);
        return new AppsHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final AppsHolder h = (AppsHolder) holder;
        AppsModel app = modelList.get(position);
        if (app != null) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) h.appIcon.getLayoutParams();
            String gap = AppHelper.getGapSize(h.iconHolder.getContext());
            if (gap.equalsIgnoreCase("small")) {
                params.rightMargin = h.iconHolder.getResources().getDimensionPixelSize(R.dimen.gap_small);
            } else if (gap.equalsIgnoreCase("medium")) {
                params.rightMargin = h.iconHolder.getResources().getDimensionPixelSize(R.dimen.gap_medium);
            } else if (gap.equalsIgnoreCase("large")) {
                params.rightMargin = h.iconHolder.getResources().getDimensionPixelSize(R.dimen.gap_large);
            } else {
                params.rightMargin = h.iconHolder.getResources().getDimensionPixelSize(R.dimen.gap_medium);
            }
            h.appIcon.setImageDrawable(new FastBitmapDrawable(app.getBitmap()));
            h.appIcon.setContentDescription(app.getAppName());
            h.iconHolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClick.onItemClickListener(v, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public void insert(List<AppsModel> apps) {
        modelList.clear();
        modelList.addAll(apps);
        notifyDataSetChanged();
    }

    public void insert(AppsModel model) {
        modelList.add(model);
        if (getItemCount() < 2) { // we always going to have the first view as an empty after each restart of app.
            notifyItemInserted(modelList.size());
        } else {
            notifyItemInserted(modelList.size() - 1);
        }
    }

    public void clearAll() {
        modelList.clear();
        notifyDataSetChanged();
    }

    public void remove(AppsModel m) {
        modelList.remove(m);
        notifyDataSetChanged();
    }

    public List<AppsModel> getModelList() {
        return modelList;
    }

    public void clear() {
        modelList.clear();
        notifyDataSetChanged();
    }


    class AppsHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.appIcon)
        ImageView appIcon;
        @Bind(R.id.iconHolder)
        View iconHolder;

        AppsHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
