package com.example.tkitaka_fb.MainMenuFragment.Planbook;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tkitaka_fb.R;

public class PlanbookFragment extends Fragment {

    public static Context mContext;
    private static PlanbookFragment instance = null;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_planbook, container, false );

        //탭 레이아웃 등록
        tabLayout = (TabLayout) view.findViewById( R.id.tab);
        tabLayout.addTab( tabLayout.newTab().setText( "일정 등록" ) );
        tabLayout.addTab( tabLayout.newTab().setText( "나의 플랜북" ) );
        tabLayout.setTabGravity( TabLayout.GRAVITY_FILL );

        //뷰 페이저 등록
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);

        //어댑터에 연결
        final PlanbookViewPagerAdapter adapter = new PlanbookViewPagerAdapter
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

    public static PlanbookFragment getInstance() {
        return instance;
    }

    //플랜북 리스트로 자동 이동
    public void jumpToPage() {
        viewPager.setCurrentItem(1, true);
    }

}


