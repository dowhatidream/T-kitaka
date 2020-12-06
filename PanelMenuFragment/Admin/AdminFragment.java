package com.example.tkitaka_fb.PanelMenuFragment.Admin;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tkitaka_fb.R;

public class AdminFragment extends Fragment implements View.OnClickListener{

    private AdQnaFragment adFaqFragment = new AdQnaFragment();
    private AdQuestionFragment adQuestionFragment = new AdQuestionFragment();
    private AdAdminPostFragment adBlackListFragment = new AdAdminPostFragment();

    TextView tvAdQuestion;
    TextView tvAdAdminPost;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_admin, container, false );

//        tvAdFaq = (TextView) view.findViewById( R.id.tvAdFaq );
//        tvAdFaq.setOnClickListener( this );

        tvAdQuestion = (TextView) view.findViewById( R.id.tvAdQuestion );
        tvAdQuestion.setOnClickListener( this );

        tvAdAdminPost = (TextView) view.findViewById( R.id.tvAdAdminPost);
        tvAdAdminPost.setOnClickListener( this );

        return  view;
    }

    @Override
    public void onClick(View view) {
//        if(view == tvAdFaq){
//            FragmentManager fragmentManager = getFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.replace( R.id.frameLayout, adFaqFragment );
//            fragmentTransaction.commit();
       if (view == tvAdQuestion) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace( R.id.frameLayout, adQuestionFragment ).commitAllowingStateLoss();
        } else if (view == tvAdAdminPost) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace( R.id.frameLayout, adBlackListFragment ).commitAllowingStateLoss();
        }
    }
}
