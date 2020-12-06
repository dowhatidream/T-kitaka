package com.example.tkitaka_fb.PanelMenuFragment.Help;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tkitaka_fb.LoginRegister.ViewPagerAdapter;
import com.example.tkitaka_fb.PanelMenuFragment.Admin.AdQuestionFragment;
import com.example.tkitaka_fb.R;

public class QuestionFragment extends Fragment {
    private TabLayout tabLayout;
    ViewPager viewPager;

    public static Context mContext;
    private static QuestionFragment instance = null;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_question, container, false );

        tabLayout = (TabLayout) view.findViewById( R.id.tab );
        tabLayout.addTab( tabLayout.newTab().setText( "문의 하기" ) );
        tabLayout.addTab( tabLayout.newTab().setText( "문의 내역" ) );
        tabLayout.setTabGravity( TabLayout.GRAVITY_FILL );

        viewPager = (ViewPager) view.findViewById( R.id.viewpager );

        final QuestionViewPagerAdapter adapter = new QuestionViewPagerAdapter( getFragmentManager(), tabLayout.getTabCount() );
        viewPager.setAdapter( adapter );
        viewPager.addOnPageChangeListener( new TabLayout.TabLayoutOnPageChangeListener( tabLayout ) );
        tabLayout.addOnTabSelectedListener( new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem( tab.getPosition() );
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        } );
        mContext = getActivity();
        instance = this;
        return view;
    }

    public static  QuestionFragment getInstance(){
        return instance;
    }

    //플랜북 리스트로 자동 이동
    public void jumpToPage() {
        viewPager.setCurrentItem(1, true);
    }

}
