package com.example.tkitaka_fb.LoginRegister; // 이승연

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tkitaka_fb.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class IDFragment extends Fragment implements View.OnClickListener {

    EditText etName;
    EditText etPhone;
    Button btnSearch;
    Button btnGo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_id, container, false);

        etName = (EditText) view.findViewById(R.id.etName);
        etPhone = (EditText) view.findViewById(R.id.etPhone);
        btnSearch = (Button) view.findViewById(R.id.btnSearch);
        btnGo = (Button) view.findViewById(R.id.btnGo);

        btnSearch.setOnClickListener( this );

        // 로그인하러 가기
        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == btnSearch) {

        }
    }
}
