package com.fastaccess.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.fastaccess.R;
import com.fastaccess.ui.modules.intro.IntroPageView;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Kosh on 06 Nov 2016, 12:38 PM
 */

public class IntroAdapter extends FragmentStatePagerAdapter {

    private final List<IntroPageView> intros;

    public IntroAdapter(FragmentManager fm) {
        super(fm);
        intros = Arrays.asList(IntroPageView.newInstance(R.string.intro_one_title, R.drawable.intro_screen_one, R.string.intro_one_desc),
                IntroPageView.newInstance(R.string.intro_folder_title, R.drawable.intro_screen_folder, R.string.intro_folder_desc),
                IntroPageView.newInstance(R.string.intro_two_title, R.drawable.intro_screen_two, R.string.intro_two_desc),
                IntroPageView.newInstance(R.string.intro_three_title, R.drawable.intro_screen_three, R.string.intro_three_desc),
                IntroPageView.newInstance(R.string.intro_four_title, R.drawable.intro_screen_four, R.string.intro_four_desc));
    }

    @Override public Fragment getItem(int position) {
        return intros.get(position);
    }

    @Override public int getCount() {
        return intros.size();
    }
}
