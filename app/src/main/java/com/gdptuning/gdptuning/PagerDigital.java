package com.gdptuning.gdptuning;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerDigital extends FragmentStatePagerAdapter {

    private int tabCount;

    PagerDigital(FragmentManager fm, int mTabCount) {
        super(fm);
        this.tabCount = mTabCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new LiveDataDigitalFragment2();
            case 1:
                return new LiveDataDigitalFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
