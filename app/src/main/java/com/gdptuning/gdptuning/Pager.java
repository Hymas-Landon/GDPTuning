package com.gdptuning.gdptuning;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class Pager extends FragmentStatePagerAdapter {

    private int tabCount;

    Pager(FragmentManager fm, int mTabCount) {
        super(fm);
        this.tabCount = mTabCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new LiveDataNeedleFragment();
            case 1:
                return new LiveDataNeedleFragment2();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
