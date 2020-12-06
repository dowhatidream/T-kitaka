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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PWFragment extends Fragment implements View.OnClickListener {

    EditText etEmail;
    EditText etName;
    EditText etPhone;
    Button btnSearch;
    Button btnGo;

    private FirebaseAuth firebaseAuth;

    private AlertDialog dialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_pw, container, false);

        etEmail = (EditText) view.findViewById(R.id.etEmail);
        etName = (EditText) view.findViewById(R.id.etName);
        etPhone = (EditText) view.findViewById(R.id.etPhone);
        btnSearch = (Button) view.findViewById(R.id.btnSearch);
        btnGo = (Button) view.findViewById(R.id.btnGo);
        btnSearch.setOnClickListener( this );

        firebaseAuth = FirebaseAuth.getInstance();

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
        if (v == btnSearch){
            String email = etEmail.getText().toString().trim();
            firebaseAuth.sendPasswordResetEmail( email )
                    .addOnCompleteListener( new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText( getActivity(), "입력된 주소로 메일을 전송하였습니다", Toast.LENGTH_SHORT ).show();
                                startActivity( new Intent( getActivity(), LoginActivity.class ) );
                            } else {
                                Toast.makeText( getActivity(), "등록되지 않은 사용자입니다", Toast.LENGTH_SHORT ).show();
                            }
                        }
                    } );
        }
    }
}
