package com.gdptuning.gdptuning;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerFeatures3 extends FragmentStatePagerAdapter {

    private int tabCount;

    PagerFeatures3(FragmentManager fm, int mTabCount) {
        super(fm);
        this.tabCount = mTabCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new FeaturesFragment3();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }

}
