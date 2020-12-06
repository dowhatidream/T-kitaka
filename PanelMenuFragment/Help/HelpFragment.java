package com.example.tkitaka_fb.PanelMenuFragment.Help;

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

public class HelpFragment extends Fragment
        implements View.OnClickListener{

    private FaqFragment faqFragment = new FaqFragment();
    private QuestionMainFragment questionFragment = new QuestionMainFragment();
    private TermsFragment termsFragment = new TermsFragment();

    TextView tvFaq;
    TextView tvQuestion;
    TextView tvTerms;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_help, container, false);
        View view = inflater.inflate( R.layout.fragment_help, container, false );

        tvFaq = (TextView) view.findViewById( R.id.tvFaq );
        tvFaq.setOnClickListener( this );

        tvQuestion = (TextView) view.findViewById( R.id.tvQuestion );
        tvQuestion.setOnClickListener( this );

        tvTerms = (TextView) view.findViewById( R.id.tvTerms );
        tvTerms.setOnClickListener( this );


        return view;
    }

    @Override
    public void onClick(View view) {
        if(view == tvFaq){
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace( R.id.frameLayout, faqFragment );
            fragmentTransaction.commit();
        } else if (view == tvQuestion) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace( R.id.frameLayout, questionFragment ).commitAllowingStateLoss();
        } else if (view == tvTerms) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace( R.id.frameLayout, termsFragment ).commitAllowingStateLoss();
        }
    }
}


