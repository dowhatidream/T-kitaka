package com.example.tkitaka_fb.LoginRegister; // 이승연

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.example.tkitaka_fb.MainMenuFragment.MainActivity;
import com.example.tkitaka_fb.Model.PBMS;
import com.example.tkitaka_fb.Model.User;
import com.example.tkitaka_fb.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PBMSActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;

    RadioGroup rgQ1, rgQ2, rgQ3, rgQ4, rgQ5, rgQ6, rgQ7, rgQ8, rgQ9, rgQ10, rgQ11;
    RadioButton rbQ1a, rbQ1b, rbQ1c, rbQ2a, rbQ2b, rbQ2c, rbQ3a, rbQ3b, rbQ3c, rbQ4a, rbQ4b, rbQ4c, rbQ5a, rbQ5b, rbQ5c;
    RadioButton rbQ6a, rbQ6b, rbQ6c, rbQ7a, rbQ7b, rbQ7c, rbQ8a, rbQ8b, rbQ8c, rbQ9a, rbQ9b, rbQ9c, rbQ10a, rbQ10b, rbQ10c;
    Button btnRegister;

    EditText etQ11;
    String Q1, Q2, Q3, Q4, Q5, Q6, Q7, Q8, Q9, Q10, Q11;

    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pbms);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        rgQ1 = (RadioGroup) findViewById(R.id.rgQ1);
        rgQ2 = (RadioGroup) findViewById(R.id.rgQ2);
        rgQ3 = (RadioGroup) findViewById(R.id.rgQ3);
        rgQ4 = (RadioGroup) findViewById(R.id.rgQ4);
        rgQ5 = (RadioGroup) findViewById(R.id.rgQ5);
        rgQ6 = (RadioGroup) findViewById(R.id.rgQ6);
        rgQ7 = (RadioGroup) findViewById(R.id.rgQ7);
        rgQ8 = (RadioGroup) findViewById(R.id.rgQ8);
        rgQ9 = (RadioGroup) findViewById(R.id.rgQ9);
        rgQ10 = (RadioGroup) findViewById(R.id.rgQ10);

        rbQ1a = (RadioButton) findViewById(R.id.rbQ1a);
        rbQ1b = (RadioButton) findViewById(R.id.rbQ1b);
        rbQ1c = (RadioButton) findViewById(R.id.rbQ1c);

        rbQ2a = (RadioButton) findViewById(R.id.rbQ2a);
        rbQ2b = (RadioButton) findViewById(R.id.rbQ2b);
        rbQ2c = (RadioButton) findViewById(R.id.rbQ2c);

        rbQ3a = (RadioButton) findViewById(R.id.rbQ3a);
        rbQ3b = (RadioButton) findViewById(R.id.rbQ3b);
        rbQ3c = (RadioButton) findViewById(R.id.rbQ3c);

        rbQ4a = (RadioButton) findViewById(R.id.rbQ4a);
        rbQ4b = (RadioButton) findViewById(R.id.rbQ4b);
        rbQ4c = (RadioButton) findViewById(R.id.rbQ4c);

        rbQ5a = (RadioButton) findViewById(R.id.rbQ5a);
        rbQ5b = (RadioButton) findViewById(R.id.rbQ5b);
        rbQ5c = (RadioButton) findViewById(R.id.rbQ5c);

        rbQ6a = (RadioButton) findViewById(R.id.rbQ6a);
        rbQ6b = (RadioButton) findViewById(R.id.rbQ6b);
        rbQ6c = (RadioButton) findViewById(R.id.rbQ6c);

        rbQ7a = (RadioButton) findViewById(R.id.rbQ7a);
        rbQ7b = (RadioButton) findViewById(R.id.rbQ7b);
        rbQ7c = (RadioButton) findViewById(R.id.rbQ7c);

        rbQ8a = (RadioButton) findViewById(R.id.rbQ8a);
        rbQ8b = (RadioButton) findViewById(R.id.rbQ8b);
        rbQ8c = (RadioButton) findViewById(R.id.rbQ8c);

        rbQ9a = (RadioButton) findViewById(R.id.rbQ9a);
        rbQ9b = (RadioButton) findViewById(R.id.rbQ9b);
        rbQ9c = (RadioButton) findViewById(R.id.rbQ9c);

        rbQ10a = (RadioButton) findViewById(R.id.rbQ10a);
        rbQ10b = (RadioButton) findViewById(R.id.rbQ10b);
        rbQ10c = (RadioButton) findViewById(R.id.rbQ10c);

        etQ11 = (EditText) findViewById(R.id.etQ11);

        btnRegister = (Button) findViewById(R.id.btnRegister);

        // 첫번째 질문
        rgQ1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup rg, int checked) {
                switch (checked){
                    case R.id.rbQ1a:
                        Q1 = "A";
                        break;
                    case R.id.rbQ1b:
                        Q1 = "B";
                        break;
                    case R.id.rbQ1c:
                        Q1 = "C";
                        break;
                }
            }
        });

        // 두번째 질문
        rgQ2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup rg, int checked) {
                switch (checked){
                    case R.id.rbQ2a:
                        Q2 = "A";
                        break;
                    case R.id.rbQ2b:
                        Q2 = "B";
                        break;
                    case R.id.rbQ2c:
                        Q2 = "C";
                        break;
                }
            }
        });

        // 세번째 질문
        rgQ3.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup rg, int checked) {
                switch (checked){
                    case R.id.rbQ3a:
                        Q3 = "A";
                        break;
                    case R.id.rbQ3b:
                        Q3 = "B";
                        break;
                    case R.id.rbQ3c:
                        Q3 = "C";
                        break;
                }
            }
        });

        // 네번째 질문
        rgQ4.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup rg, int checked) {
                switch (checked){
                    case R.id.rbQ4a:
                        Q4 = "A";
                        break;
                    case R.id.rbQ4b:
                        Q4 = "B";
                        break;
                    case R.id.rbQ4c:
                        Q4 = "C";
                        break;
                }
            }
        });

        // 다섯번째 질문
        rgQ5.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup rg, int checked) {
                switch (checked){
                    case R.id.rbQ5a:
                        Q5 = "A";
                        break;
                    case R.id.rbQ5b:
                        Q5 = "B";
                        break;
                    case R.id.rbQ5c:
                        Q5 = "C";
                        break;
                }
            }
        });

        // 여섯번째 질문
        rgQ6.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup rg, int checked) {
                switch (checked){
                    case R.id.rbQ6a:
                        Q6 = "A";
                        break;
                    case R.id.rbQ6b:
                        Q6 = "B";
                        break;
                    case R.id.rbQ6c:
                        Q6 = "C";
                        break;
                }
            }
        });

        // 일곱번째 질문
        rgQ7.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup rg, int checked) {
                switch (checked){
                    case R.id.rbQ7a:
                        Q7 = "A";
                        break;
                    case R.id.rbQ7b:
                        Q7 = "B";
                        break;
                    case R.id.rbQ7c:
                        Q7 = "C";
                        break;
                }
            }
        });

        // 여덟번째 질문
        rgQ8.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup rg, int checked) {
                switch (checked){
                    case R.id.rbQ8a:
                        Q8 = "A";
                        break;
                    case R.id.rbQ8b:
                        Q8 = "B";
                        break;
                    case R.id.rbQ8c:
                        Q8 = "C";
                        break;
                }
            }
        });

        // 아홉번째 질문
        rgQ9.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup rg, int checked) {
                switch (checked){
                    case R.id.rbQ9a:
                        Q9 = "A";
                        break;
                    case R.id.rbQ9b:
                        Q9 = "B";
                        break;
                    case R.id.rbQ9c:
                        Q9 = "C";
                        break;
                }
            }
        });

        // 열번째 질문
        rgQ10.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(RadioGroup rg, int checked) {
                switch (checked){
                    case R.id.rbQ10a:
                        Q10 = "A";
                        break;
                    case R.id.rbQ10b:
                        Q10 = "B";
                        break;
                    case R.id.rbQ10c:
                        Q10 = "C";
                        break;
                }
            }
        });

        // 등록 버튼
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Q11 = etQ11.getText().toString();

                try{
                    // 선택 누락
                    if (TextUtils.isEmpty(Q1)||TextUtils.isEmpty(Q2)||TextUtils.isEmpty(Q3)||TextUtils.isEmpty(Q4)||TextUtils.isEmpty(Q5)||TextUtils.isEmpty(Q6)||TextUtils.isEmpty(Q7)||TextUtils.isEmpty(Q8)||TextUtils.isEmpty(Q9)||TextUtils.isEmpty(Q10)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(PBMSActivity.this);
                        dialog = builder.setMessage("모든 항목을 선택해주세요").setPositiveButton("확인", null).create();
                        dialog.show();
                    }

                    else { // PBMS 통과 후 DB 저장
                        try{
                            createPBMS();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(PBMSActivity.this, "뭔가 이상해요1", Toast.LENGTH_SHORT).show();
                        }
                        Toast.makeText(PBMSActivity.this, "회원가입이 완료되었습니다", Toast.LENGTH_SHORT).show();
                        finish();
                        Intent intent = new Intent(PBMSActivity.this, Register2Activity.class);
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(PBMSActivity.this, "뭔가 이상해요2", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void createPBMS(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("PBMS"); // tkitaka에 접근. 밑에는 User 있음
        String userID = auth.getUid(); // 현재 userID 가져오기
        String tmi = etQ11.getText().toString();

        // PBMS 저장
        String PBMSID = reference.push().getKey(); // 이게 기본키이자 고유키

        PBMS pbms = new PBMS();
        pbms.setPBMSID(PBMSID);
        pbms.setAccommondation(Q1);
        pbms.setMeal(Q2);
        pbms.setsTransportation(Q3);
        pbms.setlTransportation(Q4);
        pbms.setExpense(Q5);
        pbms.setPreplan(Q6);
        pbms.setSpending(Q7);
        pbms.setFlight(Q8);
        pbms.setGuide(Q9);
        pbms.setSmoking(Q10);
        pbms.setTmi(tmi);
        pbms.setUserID(userID);

        reference.child(userID).setValue(pbms); // PBMS 가지에 생김
    }
}
