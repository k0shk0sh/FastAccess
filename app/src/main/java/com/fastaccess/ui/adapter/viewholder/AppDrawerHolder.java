package com.fastaccess.ui.adapter.viewholder;

import android.view.MotionEvent;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.ui.modules.floating.folders.drawer.FloatingDrawerMvp;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.floating.FloatingLayout;
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTouch;
import butterknife.Unbinder;

public class AppDrawerHolder {
    @BindView(R.id.appDrawer) public FloatingLayout appDrawer;
    @BindView(R.id.recycler) public DynamicRecyclerView recycler;
    @BindView(R.id.empty_text) public FontTextView emptyText;
    private FloatingDrawerMvp.View viewCallback;
    private Unbinder unbinder;

    @OnTouch(R.id.appDrawer) boolean onTouch(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
            if (viewCallback != null) viewCallback.onTouchedOutside();
        }
        return false;
    }

    public AppDrawerHolder(View view, FloatingDrawerMvp.View viewCallback) {
        this.viewCallback = viewCallback;
        unbinder = ButterKnife.bind(this, view);
        appDrawer.setViewCallback(viewCallback);
    }

    public void onDestroy() {
        appDrawer.setViewCallback(null);
        viewCallback = null;
        if (unbinder != null) unbinder.unbind();
    }

}