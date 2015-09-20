package com.styleme.floating.toolbox.pro.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.styleme.floating.toolbox.pro.R;
import com.styleme.floating.toolbox.pro.activities.base.BaseActivity;
import com.styleme.floating.toolbox.pro.global.helper.AppHelper;
import com.styleme.floating.toolbox.pro.widget.Transformer;
import com.styleme.floating.toolbox.pro.widget.intro.CirclePageIndicator;

import butterknife.Bind;
import butterknife.OnClick;


public class IntroActivity extends BaseActivity {

    @Bind(R.id.pager)
    ViewPager viewPager;
    @Bind(R.id.indicator)
    CirclePageIndicator mIndicator;
    @Bind(R.id.done)
    ImageButton done;


    @OnClick(R.id.done)
    public void onDone() {
        AppHelper.setHasSeenWhatsNew(this);
        finish();
    }

    @Override
    protected int layout() {
        return R.layout.intro_layout;
    }

    @Override
    protected boolean canBack() {
        return true;
    }

    @Override
    protected boolean hasMenu() {
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewPager.setAdapter(new ViewPagerAdapter(R.array.icons));
        mIndicator.setViewPager(viewPager);
        viewPager.setPageTransformer(true, new Transformer());
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrolled(final int position, float positionOffset, int positionOffsetPixels) {
                if (position == viewPager.getAdapter().getCount() - 1) {
                    done.setVisibility(View.VISIBLE);
                    done.animate().alpha(1).setInterpolator(new AccelerateInterpolator());
                } else {
                    done.animate().alpha(0).setInterpolator(new AccelerateInterpolator());
                    done.setVisibility(View.GONE);
                }
            }
        });
        viewPager.setOffscreenPageLimit(4);
    }

    public class ViewPagerAdapter extends PagerAdapter {
        private int iconResId;

        public ViewPagerAdapter(int iconResId) {
            this.iconResId = iconResId;
        }

        @Override
        public int getCount() {
            return getResources().getIntArray(iconResId).length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Drawable icon = getResources().obtainTypedArray(iconResId).getDrawable(position);
            View itemView = getLayoutInflater().inflate(R.layout.viewpager_item, container, false);
            ImageView iconView = (ImageView) itemView.findViewById(R.id.landing_img_slide);
            iconView.setImageDrawable(icon);
            container.addView(itemView);
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((RelativeLayout) object);
        }
    }

    @Override
    public void onBackPressed() {

    }
}
