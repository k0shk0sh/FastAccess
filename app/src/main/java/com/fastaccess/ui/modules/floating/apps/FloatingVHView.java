package com.fastaccess.ui.modules.floating.apps;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.Loader;

import com.fastaccess.data.dao.AppsModel;
import com.fastaccess.provider.loader.SelectedAppsLoader;
import com.fastaccess.ui.adapter.FloatingAppsAdapter;
import com.fastaccess.ui.adapter.viewholder.FloatingAppsViewHolder;
import com.fastaccess.ui.modules.floating.BaseFloatingView;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import java.util.ArrayList;

/**
 * Created by Kosh on 14 Oct 2016, 9:12 PM
 */


public class FloatingVHView extends BaseFloatingView<AppsModel> {
    private FloatingAppsAdapter adapter;
    private SelectedAppsLoader appsLoader;
    private FloatingVHPresenter presenter;

    protected FloatingVHView(@NonNull Context context, boolean isHorizontal) {
        super(context, isHorizontal);
    }

    public static FloatingVHView with(@NonNull Context context, boolean isHorizontal) {
        return new FloatingVHView(context, isHorizontal);
    }

    @Override public Loader getLoader() {
        if (appsLoader == null) {
            appsLoader = new SelectedAppsLoader(context);
            appsLoader.registerListener(10, getPresenter());
            appsLoader.startLoading();
        }
        return appsLoader;
    }

    @Override public BaseRecyclerAdapter<AppsModel, FloatingAppsViewHolder,
            BaseViewHolder.OnItemClickListener<AppsModel>> getAdapter() {
        if (adapter == null) {
            adapter = new FloatingAppsAdapter(new ArrayList<AppsModel>(), getPresenter(), isHorizontal);
        }
        return adapter;
    }

    @Override public FloatingVHPresenter getPresenter() {
        if (presenter == null) presenter = new FloatingVHPresenter(this);
        return presenter;
    }
}
