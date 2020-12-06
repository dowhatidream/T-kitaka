package com.example.tkitaka_fb.LoginRegister;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tkitaka_fb.MainMenuFragment.MainActivity;
import com.example.tkitaka_fb.R;
import com.example.tkitaka_fb.StartActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    EditText etUserEmail, etUserPassword;
    Button btnLogin, btnFind;
    Boolean valid;
    TextView tvAlert;

    FirebaseAuth auth;
    Toolbar toolbar;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE); // 이메일

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        // 툴바
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // 프로젝트명 가리기

        etUserEmail = (EditText) findViewById(R.id.etUserEmail);
        etUserPassword = (EditText) findViewById(R.id.etUserPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnFind = (Button) findViewById(R.id.btnFind);
        tvAlert = (TextView) findViewById(R.id.tvAlert);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sUserEmail = etUserEmail.getText().toString().trim();
                String sUserPassword = etUserPassword.getText().toString().trim();
                final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.setMessage("로그인 중...");
                progressDialog.show();

                if (!validateForm()){ // 이메일, 비밀번호 기입했는지
                    return;
                }

                auth.signInWithEmailAndPassword(sUserEmail, sUserPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        final FirebaseUser user = auth.getCurrentUser();
                        tvAlert.setText("");

                        if (task.isSuccessful()){
                            if(user!=null && user.isEmailVerified()){
                                Toast.makeText(LoginActivity.this, "로그인 완료!", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();

                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                            else if(user!=null && !user.isEmailVerified()){
                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                Dialog dialog = builder.setMessage("이메일 인증이 완료되지 않았습니다. 메일함을 확인해주세요.")
                                        .setPositiveButton("확인", null)
                                        .create();
                                dialog.show();
                            }
                        }
                        else  {
                            tvAlert.setText("이메일 혹은 비밀번호가 일치하지 않습니다!");
                            etUserEmail.requestFocus();
                        }
                        progressDialog.dismiss();
                    }
                });
            }
        });

        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, FindIdPwActivity.class);
                startActivity(intent);
            }
        });
    }

    // 이메일, 비밀번호 확인
    private boolean validateForm() {
        valid = true;
        String sUserEmail = etUserEmail.getText().toString();
        String sUserPassword = etUserPassword.getText().toString();
        tvAlert.setText("");

        if (TextUtils.isEmpty(sUserEmail)) {
            etUserEmail.setError("이메일을 입력해주세요");
            etUserEmail.requestFocus();
            valid = false;
        }
        else if (TextUtils.isEmpty(sUserPassword)) {
            etUserPassword.setError("비밀번호를 입력해주세요");
            etUserPassword.requestFocus();
            valid = false;
        }
        else if (!EMAIL_PATTERN.matcher(sUserEmail).matches()) {
            etUserEmail.setError("이메일 형식에 맞게 입력해주세요");
            etUserEmail.requestFocus();
            valid = false;
        }
        else {
            etUserEmail.setError(null);
            etUserPassword.setError(null);
            valid = true;
        }
        Log.d("로그인", "상태   "+valid);
        return valid;
    }
}
