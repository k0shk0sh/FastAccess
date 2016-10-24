package com.fastaccess.ui.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

import com.fastaccess.data.dao.AppsModel;
import com.fastaccess.ui.adapter.viewholder.SelectFolderAppsViewHolder;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Kosh on 30 Aug 2016, 11:42 PM
 */

public class SelectFolderAppsAdapter extends BaseRecyclerAdapter<AppsModel, SelectFolderAppsViewHolder,
        SelectFolderAppsViewHolder.OnItemClickListener<AppsModel>> {
    private Map<String, AppsModel> selection;

    public SelectFolderAppsAdapter(@NonNull List<AppsModel> data, @Nullable SelectFolderAppsViewHolder.OnItemClickListener<AppsModel> listener,
                                   Map<String, AppsModel> selection) {
        super(data, listener);
        this.selection = selection;
    }

    @Override protected SelectFolderAppsViewHolder viewHolder(ViewGroup parent, int viewType) {
        return SelectFolderAppsViewHolder.newInstance(parent, this);
    }

    @Override protected void onBindView(SelectFolderAppsViewHolder holder, int position) {
        AppsModel model = getItem(position);
        holder.bind(model, isSelected(model.getActivityInfoName()));
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
