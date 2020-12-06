package com.example.tkitaka_fb.PanelMenuFragment.Help;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.tkitaka_fb.R;


public class TermsFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate( R.layout.fragment_terms, container, false );
        View view = inflater.inflate( R.layout.fragment_terms, container, false );

//        TextView tvTerms = (TextView) view.findViewById( R.id.tvTerms );

        return view;
    }


}
