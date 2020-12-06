package com.example.tkitaka_fb;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.tkitaka_fb.LoginRegister.LoginActivity;
import com.example.tkitaka_fb.LoginRegister.RegisterActivity;
import com.example.tkitaka_fb.MainMenuFragment.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartActivity extends AppCompatActivity {

    Button btnLogin, btnRegister;

    FirebaseUser user;
    FirebaseAuth auth;

    @Override
    protected void onStart() {
        super.onStart();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        // 유저가 null인지 체크
        if (user != null && user.isEmailVerified()){
            Toast.makeText(StartActivity.this, "안녕하세요", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(StartActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            return;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegister = (Button) findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, LoginActivity.class));
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, RegisterActivity.class));
            }
        });
    }
}