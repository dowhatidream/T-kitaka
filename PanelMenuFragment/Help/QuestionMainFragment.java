package com.example.tkitaka_fb.PanelMenuFragment.Help;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tkitaka_fb.MainMenuFragment.Planbook.PlanbookFragment;
import com.example.tkitaka_fb.R;

public class QuestionMainFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate( R.layout.fragment_question_main, container, false);

        //처음 childfragment 지정
        getFragmentManager().beginTransaction().add(R.id.fgQuestion, new QuestionFragment()).commit();

        return v;
    }
}