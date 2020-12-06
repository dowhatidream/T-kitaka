package com.example.tkitaka_fb.MainMenuFragment.Planbook;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.tkitaka_fb.Model.Planbook;
import com.example.tkitaka_fb.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class AddPlanbookFragment extends Fragment {

    //파이어베이스 유저
    FirebaseAuth auth;
    FirebaseUser user;

    AlertDialog dialog;

    String title, cDate, deDate, reDate, place, diffday;
    String begin1, begin2, begin3, begin;
    String end1, end2, end3, end;

    //나라 스피너
    ArrayAdapter<CharSequence> adapterCountry;

    //날짜
    private int yearD, monthD, dayD;
    private int yearR, monthR, dayR;
    int beginy, beginm, begind;

    //레이아웃 변수
    EditText etTitle, etDeDate, etReDate;
    Spinner spCountry;
    Button btnAdd;

    PlanbookViewPagerAdapter viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_planbook, container, false);

        // 현재 유저 불러오기
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        //작성 내용 변수에 저장
        etTitle = view.findViewById(R.id.etTitle);
        etDeDate = (EditText) view.findViewById(R.id.etDeDate);
        etReDate = (EditText) view.findViewById(R.id.etReDate);
        spCountry = view.findViewById(R.id.spCountry);
        btnAdd = view.findViewById(R.id.btnAdd);

        // 나라 스피너
        adapterCountry = ArrayAdapter.createFromResource(getContext(),
                R.array.countryArray, android.R.layout.simple_spinner_item); // arrays파일 항목을 가지고 adapter 생성
        adapterCountry.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // 스피너 펼칠때 사용할 모양
        spCountry.setAdapter(adapterCountry); // 스피너에 어댑터 설정

        // 출발일 선택
        etDeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                final Calendar minDateD = Calendar.getInstance();

                yearD = calendar.get(Calendar.YEAR);
                monthD = calendar.get(Calendar.MONTH);
                dayD = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        etDeDate.setText(year + "/" + (monthOfYear + 1) + "/" + dayOfMonth);
                        yearR = year;
                        monthR = monthOfYear;
                        dayR = dayOfMonth;
                        begin = String.valueOf(year + (monthOfYear + 1) + dayOfMonth);
                    }
                }, yearD, monthD, dayD);

                minDateD.set(yearD, monthD, dayD);
                datePickerDialog.getDatePicker().setMinDate(minDateD.getTime().getTime());

                datePickerDialog.show();
            }
        });

        // 도착일 선택
        etReDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar minDateR = Calendar.getInstance();

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        etReDate.setText(year + "/" + (monthOfYear + 1) + "/" + dayOfMonth);
                        end = String.valueOf(year + (monthOfYear + 1) + dayOfMonth);
                    }
                }, yearR, monthR, dayR);

                minDateR.set(yearR, monthR, dayR);
                datePickerDialog.getDatePicker().setMinDate(minDateR.getTime().getTime());

                datePickerDialog.show();
            }
        });

        // 플랜북 등록 완료 버튼
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // 기재 누락 여부
                    if (TextUtils.isEmpty(etTitle.getText().toString()) || TextUtils.isEmpty(etTitle.getText().toString())) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        dialog = builder.setMessage("플랜 제목을 입력해주세요.")
                                .setPositiveButton("확인", null)
                                .create();
                        dialog.show();
                        return;
                    } else {
                        createPlanbook(); //DB 등록 메소드

                        PlanbookFragment.getInstance().jumpToPage();    //뷰페이저 두 번째 페이지로 자동 이동

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "뭔가 이상해요", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    public void createPlanbook(){
        //레이아웃에서 작성한 내용 String 변환
        deDate = etDeDate.getText().toString();
        reDate = etReDate.getText().toString();
        place = spCountry.getSelectedItem().toString();
        title = etTitle.getText().toString();

        //생성일
        TimeZone time;
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

        time = TimeZone.getTimeZone("Asia/Seoul");
        dateFormat.setTimeZone(time);
        cDate = dateFormat.format(date);

        // 현재 userID 가져오기
        DatabaseReference reference;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference = database.getReference("Planbook");
        String userID = auth.getUid(); // 현재 userID 가져오기

        String planbookID = reference.push().getKey(); //기본키이자 고유 노드

        //날짜차이 계산
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
            Date beginDate = formatter.parse(begin);
            Date endDate = formatter.parse(end);

            // 시간차이를 시간,분,초를 곱한 값으로 나누면 하루 단위가 나옴
            long diff = endDate.getTime() - beginDate.getTime();
            long diffDays = (diff / (24 * 60 * 60 * 1000)) + 1;

            diffday = String.valueOf(diffDays);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        try{
            //Planbook 모델에 내용 저장
            Planbook planbook = new Planbook();
            planbook.setPlanbookID(planbookID);
            planbook.setPlace(place);
            planbook.setDeDate(deDate);
            planbook.setReDate(reDate);
            planbook.setTitle(title);
            planbook.setDate(cDate);
            planbook.setUserID(userID);
            planbook.setDiffDay(diffday);

            //레퍼런스 child 경로 설정 및 저장
            reference.child(userID).child(planbookID).setValue(planbook);

            Toast.makeText(getContext(), "플랜북 등록 완료", Toast.LENGTH_SHORT).show();

            //레이아웃 텍스트 초기화
            etTitle.setText(null);
            etReDate.setText(null);
            etDeDate.setText(null);
            spCountry.setSelection(0);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "뭔가 이상해요", Toast.LENGTH_SHORT).show();
        } finally {

        }
    }
}