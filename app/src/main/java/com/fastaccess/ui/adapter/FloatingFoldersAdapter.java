package com.fastaccess.ui.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

import com.fastaccess.data.dao.FolderModel;
import com.fastaccess.ui.adapter.viewholder.FloatingFoldersViewHolder;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;

import java.util.List;

/**
 * Created by Kosh on 30 Aug 2016, 11:42 PM
 */

public class FloatingFoldersAdapter extends BaseRecyclerAdapter<FolderModel, FloatingFoldersViewHolder,
        FloatingFoldersViewHolder.OnItemClickListener<FolderModel>> {

    private boolean isHorizontal;

    public FloatingFoldersAdapter(@NonNull List<FolderModel> data, @Nullable FloatingFoldersViewHolder.OnItemClickListener<FolderModel> listener,
                                  boolean isHorizontal) {
        super(data, listener);
        this.isHorizontal = isHorizontal;
    }

    @Override protected FloatingFoldersViewHolder viewHolder(ViewGroup parent, int viewType) {
        return FloatingFoldersViewHolder.newInstance(parent, this);
    }

    @Override protected void onBindView(FloatingFoldersViewHolder holder, int position) {
        FolderModel model = getItem(position);
        if (model != null) {
            holder.bind(model, isHorizontal);
        }
    }

}
