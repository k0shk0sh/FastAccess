package com.styleme.floating.toolbox.pro.global.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.styleme.floating.toolbox.pro.R;
import com.styleme.floating.toolbox.pro.global.helper.AppHelper;
import com.styleme.floating.toolbox.pro.global.model.AppsModel;
import com.styleme.floating.toolbox.pro.widget.EmptyHolder;
import com.styleme.floating.toolbox.pro.widget.FastBitmapDrawable;
import com.styleme.floating.toolbox.pro.widget.impl.ItemTouchHelperAdapter;
import com.styleme.floating.toolbox.pro.widget.impl.ItemTouchHelperViewHolder;
import com.styleme.floating.toolbox.pro.widget.impl.MyAppsOnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Kosh on 8/16/2015. copyrights are reserved
 */
public class MyAppsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable, ItemTouchHelperAdapter {

    private List<AppsModel> searchableList;
    private List<AppsModel> modelList;
    private MyAppsOnItemClickListener onClick;

    public MyAppsAdapter(MyAppsOnItemClickListener onClick, List<AppsModel> modelList) {
        this.modelList = modelList;
        this.onClick = onClick;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if (viewType == EmptyHolder.EMPTY_TYPE) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_view, parent, false);
            return new EmptyHolder(v);
        }
        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_apps_list_items, parent, false);
        return new AppsHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (getItemViewType(position) != EmptyHolder.EMPTY_TYPE) {
            final AppsHolder h = (AppsHolder) holder;
            AppsModel app = modelList.get(position);
            if (app != null) {
                h.appName.setText(app.getAppName());
                h.countEntry.setText(String.format("%d", app.getCountEntry()));
                h.appIcon.setImageDrawable(new FastBitmapDrawable(app.getBitmap()));
                h.appIcon.setContentDescription(app.getAppName());
                h.iconHolder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onClick.onItemClickListener(v, position);
                    }
                });
                h.iconHolder.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        onClick.onItemLongClickListener(h, v, position);
                        return true;
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return modelList == null || modelList.size() == 0 ? 1 : modelList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (modelList == null || modelList.size() == 0) {
            return EmptyHolder.EMPTY_TYPE;
        }
        return super.getItemViewType(position);
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

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            final FilterResults oReturn = new FilterResults();
            final List<AppsModel> results = new ArrayList<>();
            if (searchableList == null) {
                searchableList = modelList;
            }
            if (charSequence != null) {
                if (searchableList != null && searchableList.size() > 0) {
                    for (final AppsModel appInfo : searchableList) {
                        if (appInfo.getAppName().toLowerCase().contains(charSequence.toString())) {
                            results.add(appInfo);
                        }
                    }
                }
                oReturn.values = results;
                oReturn.count = results.size();
            }
            return oReturn;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            modelList = (List<AppsModel>) filterResults.values;
            notifyDataSetChanged();
        }
    };

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (modelList.get(fromPosition) != null && modelList.get(toPosition) != null) {
            onClick.onItemPositionChange(fromPosition, toPosition);
        }
    }

    @Override
    public void onItemDismiss(int position) {
        onClick.onItemDismissed(position);
    }

    class AppsHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
        @Bind(R.id.appIcon)
        ImageView appIcon;
        @Bind(R.id.iconHolder)
        View iconHolder;
        @Bind(R.id.appName)
        TextView appName;
        @Bind(R.id.countEntry)
        TextView countEntry;

        AppsHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(AppHelper.getAlpha(AppHelper.getAccentColor(itemView.getContext())));
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundResource(0);
        }
    }

}
