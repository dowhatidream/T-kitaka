package com.example.tkitaka_fb.PanelMenuFragment.Admin;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class AdQuestionViewPagerAdapter extends FragmentStatePagerAdapter {
    private int tabCount;

    public AdQuestionViewPagerAdapter(FragmentManager fm, int tabCount){
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                AdQTripListFragment tab1 = new AdQTripListFragment();
                return tab1;
            case 1:
                AdQProfileListFragment tab2 = new AdQProfileListFragment();
                return tab2;
            case 2:
                AdQSystemListFragment tab3 = new AdQSystemListFragment();
                return tab3;
            case 3:
                AdQEtcListFragment tab4 = new AdQEtcListFragment();
                return tab4;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
