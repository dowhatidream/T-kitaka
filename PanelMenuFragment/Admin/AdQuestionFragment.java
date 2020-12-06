package com.example.tkitaka_fb.PanelMenuFragment.Admin;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tkitaka_fb.MainMenuFragment.Planbook.PlanbookFragment;
import com.example.tkitaka_fb.R;

public class AdQuestionFragment extends Fragment {

    public static Context mContext;
    private static AdQuestionFragment instance = null;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_ad_question, container, false );

        //탭 레이아웃 등록
        tabLayout = (TabLayout) view.findViewById( R.id.tab);
        tabLayout.addTab( tabLayout.newTab().setText( "동행찾기" ) );
        tabLayout.addTab( tabLayout.newTab().setText( "개인정보" ) );
        tabLayout.addTab( tabLayout.newTab().setText( "시스템" ) );
        tabLayout.addTab( tabLayout.newTab().setText( "기타" ) );
        tabLayout.setTabGravity( TabLayout.GRAVITY_FILL );

        //뷰 페이저 등록
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);

        //어댑터에 연결
        final AdQuestionViewPagerAdapter adapter = new AdQuestionViewPagerAdapter
                (getFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        mContext = getActivity();
        instance = this;
        return view;
    }

    public static AdQuestionFragment getInstance() {
        return instance;
    }

    //플랜북 리스트로 자동 이동
    public void jumpToPage() {
        viewPager.setCurrentItem(1, true);
    }
}
