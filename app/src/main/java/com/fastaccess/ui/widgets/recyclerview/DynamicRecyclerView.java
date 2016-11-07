package com.fastaccess.ui.widgets.recyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.fastaccess.R;
import com.fastaccess.ui.widgets.recyclerview.layout_manager.GridManager;

import static android.R.attr.columnWidth;


/**
 * Created by Kosh on 9/24/2015. copyrights are reserved
 * <p>
 * recyclerview which will showParentOrSelf/showParentOrSelf itself base on adapter
 */
public class DynamicRecyclerView extends RecyclerView {

    private View emptyView;
    private int iconSize;
    @Nullable private View parentView;

    @NonNull private AdapterDataObserver observer = new AdapterDataObserver() {
        @Override public void onChanged() {
            showEmptyView();
        }

        @Override public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            showEmptyView();
        }

        @Override public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            showEmptyView();
        }
    };

    public DynamicRecyclerView(Context context) {
        this(context, null);
    }

    public DynamicRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DynamicRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (attrs != null) {
            int[] attrsArray = {columnWidth};
            TypedArray array = context.obtainStyledAttributes(attrs, attrsArray);
            iconSize = array.getDimensionPixelSize(0, -1);
            if (iconSize > 0) {
                setHasFixedSize(true);
                iconSize += getResources().getDimensionPixelSize(R.dimen.spacing_micro);
                if (getLayoutManager() instanceof GridManager) {
                    ((GridManager) getLayoutManager()).setIconSize(iconSize);
                }
            }
            array.recycle();
        }
    }

    @Override public void setAdapter(@Nullable Adapter adapter) {
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(observer);
            observer.onChanged();
        }
    }

//    @Override protected void onMeasure(int widthSpec, int heightSpec) {
//        super.onMeasure(widthSpec, heightSpec);
//        int width = MeasureSpec.getSize(widthSpec);
//        if (iconSize > 0 && width != 0) {
//            int spanCount = Math.max(1, width / iconSize);
//            if (getLayoutManager() instanceof GridLayoutManager) {
//                ((GridLayoutManager) getLayoutManager()).setSpanCount(spanCount);
//                getLayoutManager().requestLayout();
//            }
//        }
//    }

    public void showEmptyView() {
        Adapter<?> adapter = getAdapter();
        if (adapter != null) {
            if (emptyView != null) {
                if (adapter.getItemCount() == 0) {
                    showParentOrSelf(false);
                } else {
                    showParentOrSelf(true);
                }
            }
        } else {
            if (emptyView != null) {
                showParentOrSelf(false);
            }
        }
    }

    private void showParentOrSelf(boolean show) {
        if (parentView == null) {
            setVisibility(show ? VISIBLE : GONE);
        } else {
            parentView.setVisibility(show ? VISIBLE : GONE);
        }
        emptyView.setVisibility(!show ? VISIBLE : GONE);
    }

    public void setEmptyView(@NonNull View emptyView, @Nullable View parentView) {
        this.emptyView = emptyView;
        this.parentView = parentView;
        showEmptyView();
    }

    public void setEmptyView(@NonNull View emptyView) {
        setEmptyView(emptyView, null);
    }

    public void hideProgress(@NonNull View view) {
        view.setVisibility(GONE);
    }

    public void showProgress(@NonNull View view) {
        view.setVisibility(VISIBLE);
    }

    public void setIconSize(int iconSize) {
        this.iconSize = iconSize;
        invalidate();
    }
}
