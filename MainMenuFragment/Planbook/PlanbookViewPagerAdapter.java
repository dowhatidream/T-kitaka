package com.example.tkitaka_fb.MainMenuFragment.Planbook;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PlanbookViewPagerAdapter extends FragmentStatePagerAdapter {

    private int tabCount;

    public PlanbookViewPagerAdapter(FragmentManager fm, int tabCount){

        super(fm);
        this.tabCount = tabCount;

    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                AddPlanbookFragment tab1 = new AddPlanbookFragment();
                return tab1;
            case 1:
                PlanbookListFragment tab2 = new PlanbookListFragment();
                return tab2;
            case 2:
                UpdatePlanbookFragment tab3 = new UpdatePlanbookFragment();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
