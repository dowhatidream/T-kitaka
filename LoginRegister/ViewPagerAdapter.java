package com.example.tkitaka_fb.LoginRegister;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private int tabCount;

    public ViewPagerAdapter(FragmentManager fm, int tabCount){

        super(fm);
        this.tabCount = tabCount;

    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                IDFragment tab1 = new IDFragment();
                return tab1;
            case 1:
                PWFragment tab2 = new PWFragment();
                return tab2;
            default:
                return null;
        }
    }


    @Override
    public int getCount() {
        return tabCount;
    }
}
