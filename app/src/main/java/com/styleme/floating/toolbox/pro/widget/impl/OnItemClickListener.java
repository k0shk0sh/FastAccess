package com.styleme.floating.toolbox.pro.widget.impl;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Kosh on 8/16/2015. copyrights are reserved
 */
public interface OnItemClickListener {

    void onItemClickListener(View view, int position);

    void onItemLongClickListener(RecyclerView.ViewHolder viewHolder, View view, int position);
}