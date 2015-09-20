package com.styleme.floating.toolbox.pro.global.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.styleme.floating.toolbox.pro.fragments.MyAppsList;
import com.styleme.floating.toolbox.pro.fragments.PhoneAppsList;

/**
 * Created by Kosh on 9/3/2015. copyrights are reserved
 */
public class PagerAdapter extends FragmentStatePagerAdapter {

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new PhoneAppsList();
            case 1:
                return new MyAppsList();
            default:
                return new PhoneAppsList();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
