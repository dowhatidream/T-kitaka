package com.example.tkitaka_fb.LoginRegister;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Spinner;

import com.example.tkitaka_fb.R;


public class FindIdPwActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_find_id_pw);

        tabLayout = (TabLayout) findViewById( R.id.tab );
        tabLayout.addTab( tabLayout.newTab().setText( "아이디 찾기" ) );
        tabLayout.addTab( tabLayout.newTab().setText( "비밀번호 찾기" ) );
        tabLayout.setTabGravity( TabLayout.GRAVITY_FILL );


        viewPager = (ViewPager) findViewById( R.id.viewpager );

        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter( getSupportFragmentManager(), tabLayout.getTabCount() );
        viewPager.setAdapter( pagerAdapter );
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

    }
}
