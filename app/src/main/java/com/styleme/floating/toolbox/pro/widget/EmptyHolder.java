package com.styleme.floating.toolbox.pro.widget;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.styleme.floating.toolbox.pro.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by kosh20111 on 7/30/2015. CopyRights @ Innov8tif
 */
public class EmptyHolder extends RecyclerView.ViewHolder {

    public static final int EMPTY_TYPE = 1;

    @Bind(R.id.emptyText)
    public TextView emptyText;

    public EmptyHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
