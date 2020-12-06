package com.example.tkitaka_fb.PanelMenuFragment.Help;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.tkitaka_fb.LoginRegister.IDFragment;
import com.example.tkitaka_fb.LoginRegister.PWFragment;
import com.example.tkitaka_fb.MainMenuFragment.Planbook.UpdatePlanbookFragment;

public class QuestionViewPagerAdapter extends FragmentStatePagerAdapter {
    private int tabCount;

    public QuestionViewPagerAdapter(FragmentManager fm, int tabCount){
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                QuestionOneFragment tab1 = new QuestionOneFragment();
                return tab1;
            case 1:
                QuestionTwoFragment tab2 = new QuestionTwoFragment();
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
