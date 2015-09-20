package com.styleme.floating.toolbox.pro.widget.impl;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Kosh on 8/16/2015. copyrights are reserved
 */
public interface MyAppsOnItemClickListener {

    void onItemClickListener(View view, int position);

    void onItemLongClickListener(RecyclerView.ViewHolder viewHolder, View view, int position);

    void onItemDismissed(int position);

    void onItemPositionChange(int fromPosition, int toPosition);
}