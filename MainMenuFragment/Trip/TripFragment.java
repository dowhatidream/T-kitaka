package com.example.tkitaka_fb.MainMenuFragment.Trip; // 이승연

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tkitaka_fb.R;

public class TripFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trip, container, false);

        getFragmentManager().beginTransaction().add(R.id.fgTripInfo, new TripListFragment()).commit();

        return view;
    }
}
