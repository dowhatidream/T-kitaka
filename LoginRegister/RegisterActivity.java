package com.example.tkitaka_fb.LoginRegister;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.tkitaka_fb.MainMenuFragment.MainActivity;
import com.example.tkitaka_fb.Model.PBMS;
import com.example.tkitaka_fb.Model.User;
import com.example.tkitaka_fb.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "회원가입액티비티";

    EditText etUserName, etUserEmail, etUserPassword, etUserRePassword, etUserBirth, etUserPhone, etPhoneVari, etCode;
    Button btnRegister, btnSend, btnPhoneVariCheck;

    private String verificationId;

    FirebaseAuth auth;

    Toolbar toolbar;

    Boolean isVarifyPhone = false;
    Boolean isSendPhone = false;

    // 정규식
    private static final Pattern NAME_PATTERN = Pattern.compile("^[ㄱ-ㅎ가-힣]*$", Pattern.CASE_INSENSITIVE); // 이름
    private static final Pattern BIRTH_PATTERN = Pattern.compile("^(19[0-9][0-9]|20\\d{2})(0[0-9]|1[0-2])(0[1-9]|[1-2][0-9]|3[0-1])$"); // 생년월일
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE); // 이메일
    private static final Pattern PHONE_PATTERN = Pattern.compile("^01(?:0|1|[6-9])(\\d{3}|\\d{4})(\\d{4})$"); // 전화번호
    private static final Pattern NUMBER_PATTERN = Pattern.compile("^[0-9][0-9][0-9][0-9][0-9][0-9]$"); // 인증번호

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();

        // 툴바
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // 프로젝트명 가리기

        etUserName = (EditText) findViewById(R.id.etUserName);
        etUserEmail = (EditText) findViewById(R.id.etUserEmail);
        etUserPassword = (EditText) findViewById(R.id.etUserPassword);
        etUserRePassword = (EditText) findViewById(R.id.etUserRePassword);
        etUserBirth = (EditText) findViewById(R.id.etUserBirth);
        etUserPhone = (EditText) findViewById(R.id.etUserPhone);
        etPhoneVari = (EditText) findViewById(R.id.etPhoneVari);
        etCode = (EditText) findViewById(R.id.etCode);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnSend = (Button) findViewById(R.id.btnSend);
        btnPhoneVariCheck = (Button) findViewById(R.id.btnPhoneVariCheck);

        // 비밀번호 재입력 일치하는지 이미지로 나타내는 코드
        etUserRePassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etUserPassword.getText().toString().equals(etUserRePassword.getText().toString())) {
                    etUserRePassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check, 0);
                } else if (etUserPassword.getText().toString().isEmpty() || !etUserPassword.getText().toString().equals(etUserRePassword.getText().toString())) {
                    etUserRePassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_not_matched, 0);
                } else {
                    etUserRePassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_not_matched, 0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // 문자 인증번호 발송
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etUserPhone.setError(null);
                String userPhone = "+82" + etUserPhone.getText().toString().trim();
//                etUserPhone.setFocusable(false);

                try {
                    if (etUserPhone.getText().toString().trim().isEmpty()) {
                        etUserPhone.setError("휴대폰 번호를 입력해주세요");
                        etUserPhone.requestFocus();
                        return;
                    } else if (!PHONE_PATTERN.matcher(etUserPhone.getText().toString()).matches()) {
                        etUserPhone.setError("휴대폰 번호 형식에 맞게 입력해주세요");
                        etUserPhone.requestFocus();
                        return;
                    }
                    isSendPhone = true;
                    sendPhoneVerification(userPhone); // 문자로 인증번호 전송
                    Toast.makeText(getApplicationContext(), "인증 번호를 전송했습니다", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "뭔가 이상해요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 문자 인증번호 확인
        btnPhoneVariCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = etPhoneVari.getText().toString().trim();

                if (code.isEmpty()) {
                    etPhoneVari.setError("인증번호를 입력해주세요");
                    etPhoneVari.requestFocus();
                } else if (!NUMBER_PATTERN.matcher(code).matches()) {
                    etPhoneVari.setError("인증번호는 6자리 숫자입니다");
                    etPhoneVari.requestFocus();
                } else {
                    verifyCode(code);
                    Toast.makeText(getApplicationContext(), "인증이 완료되었습니다", Toast.LENGTH_SHORT).show();
                    etUserPhone.setFocusable(false);
                    etPhoneVari.setFocusable(false);
                    etPhoneVari.setError(null);
                    isVarifyPhone = true;
                }
            }
        });

        // 회원가입 등록 버튼
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sUserName = etUserName.getText().toString().trim();
                String sUserEmail = etUserEmail.getText().toString().trim();
                String sUserPassword = etUserPassword.getText().toString().trim();
                String sUserRePassword = etUserRePassword.getText().toString().trim();
                String sUserBirth = etUserBirth.getText().toString().trim();
                String sUserPhone = etUserPhone.getText().toString();

                // 빈칸 여부 체크
                if (TextUtils.isEmpty(sUserName) || TextUtils.isEmpty(sUserEmail) || TextUtils.isEmpty(sUserPassword) || TextUtils.isEmpty(sUserBirth)) {
                    Toast.makeText(RegisterActivity.this, "빈칸 없이 기입해주세요", Toast.LENGTH_SHORT).show();
                }
                // 이름 형식 체크
                else if (!NAME_PATTERN.matcher(sUserName).matches()) {
                    etUserName.setError("이름은 한글로 기입해주세요");
                    etUserName.requestFocus();
                }
                // 이메일 형식 체크
                else if (!EMAIL_PATTERN.matcher(sUserEmail).matches()) {
                    etUserEmail.setError("이메일 형식에 맞게 입력해주세요");
                    etUserEmail.requestFocus();
                }
                // 비밀번호 길이 체크
                else if (sUserPassword.length() < 8) {
                    etUserPassword.setError("비밀번호는 최소 8자리 이상이어야 합니다");
                    etUserPassword.requestFocus();
                }
                // 비밀번호 재입력값 일치 여부
                else if (!sUserPassword.matches(sUserRePassword)) {
                    etUserRePassword.setError("비밀번호가 일치하지 않습니다");
                    etUserRePassword.setText("");
                    etUserRePassword.requestFocus();
                }
                // 생년월일 형식 체크
                else if (!BIRTH_PATTERN.matcher(sUserBirth).matches()) {
                    etUserBirth.setError("생년월일 형식에 맞게 입력해주세요");
                    etUserBirth.requestFocus();
                }
                // 인증번호 전송 체크
                else if (!isSendPhone) {
                    etUserPhone.setError("전화번호 인증을 완료해주세요");
                    etUserPhone.requestFocus();
                }
                // 인증번호 누락
                else if (!isVarifyPhone) {
                    etPhoneVari.setError("인증번호를 다시 한 번 확인해주세요");
                    etPhoneVari.requestFocus();
                } else {
                    register(sUserName, sUserEmail, sUserPassword, sUserBirth, sUserPhone);
                }
            }
        });
    }

    // 회원가입
    private void register(final String sUserName, String sUserEmail, String sUserPassword, final String sUserBirth, final String sUserPhone) {
        final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setMessage("잠시만 기다려주세요...");
        progressDialog.show();

        auth.createUserWithEmailAndPassword(sUserEmail, sUserPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "회원가입 성공");

                    FirebaseUser user = auth.getCurrentUser();

                    String userID = user.getUid();
                    String userEmail = user.getEmail();
                    String sCode = etCode.getText().toString(); // 추천인 혹은 관리자 코드
                    assert user != null;


                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User").child(userID); // User 테이블 밑에

                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("userName", sUserName);
                    hashMap.put("userID", userID); // 파이어베이스
                    hashMap.put("userEmail", userEmail); // 파이어베이스
                    hashMap.put("userBirth", sUserBirth);
                    hashMap.put("userPhone", sUserPhone);
                    hashMap.put("userProfile", "default");
                    hashMap.put("userIntroduction", "");
                    hashMap.put("userStatus", "offline");

                    if (sCode.matches("tryangleTeam")) {
                        hashMap.put("userGrade", "admin");
                    } else {
                        hashMap.put("userGrade", "member");
                    }

                    // 회원가입 성공 후 이메일 발송
                    reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                sendEmailVerification(); // 이메일 보내기
                                etUserEmail.setText("");
                                etUserPassword.setText("");

                                Intent intent = new Intent(RegisterActivity.this, PBMSActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                    progressDialog.dismiss();
                } else {
                    Log.d(TAG, "회원가입 실패");
                    Toast.makeText(RegisterActivity.this, "이미 존재하는 이메일입니다", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }

    // 이메일 인증
    public void sendEmailVerification() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "메일 전송 성공", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(RegisterActivity.this, "메일 전송 실패", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // 폰번호 인증
    public void sendPhoneVerification(String userPhone) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(userPhone, 60, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD, mCallBack);
    }

    // 폰번호 인증2
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(RegisterActivity.this, "인증 실패", Toast.LENGTH_SHORT).show();
        }
    };

    private void verifyCode(String code) {
        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
//            signInWithCredential(credential);
        } catch (Exception e) {
            Toast toast = Toast.makeText(this, "잘못된 인증번호입니다", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }
}
