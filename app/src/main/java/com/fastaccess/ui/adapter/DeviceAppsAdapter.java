package com.fastaccess.ui.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.fastaccess.R;
import com.fastaccess.data.dao.AppsModel;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.ui.adapter.viewholder.DeviceAppsViewHolder;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Kosh on 30 Aug 2016, 11:42 PM
 */

public class DeviceAppsAdapter extends BaseRecyclerAdapter<AppsModel, DeviceAppsViewHolder,
        DeviceAppsViewHolder.OnItemClickListener<AppsModel>> implements Filterable {
    private Map<String, AppsModel> selection;
    private boolean selectedApps;

    public DeviceAppsAdapter(@NonNull List<AppsModel> data, @Nullable DeviceAppsViewHolder.OnItemClickListener<AppsModel> listener,
                             Map<String, AppsModel> selection) {
        this(data, listener, selection, false);
    }

    public DeviceAppsAdapter(@NonNull List<AppsModel> data, @Nullable DeviceAppsViewHolder.OnItemClickListener<AppsModel> listener,
                             Map<String, AppsModel> selection, boolean selectedApps) {
        super(data, listener);
        this.selection = selection;
        this.selectedApps = selectedApps;
    }

    @Override protected DeviceAppsViewHolder viewHolder(ViewGroup parent, int viewType) {
        return new DeviceAppsViewHolder(BaseViewHolder.getView(parent, R.layout.app_row_item), this);
    }

    @Override protected void onBindView(DeviceAppsViewHolder holder, int position) {
        AppsModel model = getItem(position);
        if (model != null) {
            holder.bind(model, isSelected(model.getComponentName().toShortString()), selectedApps);
        }
    }

    @Override public Filter getFilter() {
        return new Filter() {
            @Override protected FilterResults performFiltering(CharSequence charSequence) {
                final FilterResults oReturn = new FilterResults();
                final List<AppsModel> results = new ArrayList<>();
                if (!InputHelper.isEmpty(charSequence)) {
                    if (!getData().isEmpty()) {
                        for (AppsModel appInfo : getData()) {
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

            @SuppressWarnings("unchecked") @Override protected void publishResults(CharSequence constraint, FilterResults results) {
                insertItems((List<AppsModel>) results.values);
            }
        };
    }

    public void select(String packageName, int position, boolean select) {
        if (select) selection.put(packageName, getItem(position));
        else selection.remove(packageName);
        notifyItemChanged(position);
    }

    public boolean isSelected(@NonNull String packageName) {
        return selection.get(packageName) != null;
    }

    public boolean hasSelection() {
        return selection != null && !selection.isEmpty();
    }

    public void clearSelection() {
        if (hasSelection()) selection.clear();
        notifyDataSetChanged();
    }

    public int selectionSize() {
        return selection != null ? selection.size() : 0;
    }

    public List<AppsModel> getSelections() {
        return new ArrayList<>(selection.values());
    }

}
