package com.example.tkitaka_fb.MainMenuFragment.Trip; // 이승연

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tkitaka_fb.Model.TripInfo;
import com.example.tkitaka_fb.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

public class UpdateTripFragment extends DialogFragment {

    public static UpdateTripFragment newInstance(){
        return new UpdateTripFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference UserRef, TripInfoRef;

    Button btnAdd;
    ImageButton ibCancel;
    TextView tvTitle;

    Spinner spCountry; // 나라
    Spinner spTheme; // 테마
    EditText etDeDate; // 출발날짜
    EditText etReDate; // 도착날짜
    CheckBox cb20; // 체크박스
    CheckBox cb30;
    CheckBox cb40;
    CheckBox cb50;
    CheckBox cbBooking;

    Boolean isChecked = false;

    String selectedTheme;
    String selectedCountry;
    String deDate;
    String reDate;
    String age = ""; // 총 연령대

    String uDate;

    String age20 = "20";
    String age30 = "30";
    String age40 = "40";
    String age50 = "50";

    ArrayAdapter<CharSequence> adapterCountry; // 스피너 어댑터
    ArrayAdapter<CharSequence> adapterTheme;

    boolean mSpinnerCountry; // 스피너를 처음 선택한 것인지 여부를 저장할 변수
    boolean mSpinnerTheme; // 스피너를 처음 선택한 것인지 여부를 저장할 변수

    private int yearD, monthD, dayD; // 달력
    private int yearR, monthR, dayR;

    String userID;

    // 가져온 값
    String uTripInfoID, uCodeID, uDeDate, uReDate, uTheme, uAge, uCDate;
    Boolean uIsChecked;

    String[] iDateD, iDateR;

    int iYearD, iMonthD, iDayD;
    int iYearR, iMonthR, iDayR;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_add_trip, container, false);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userID = user.getUid();
        database = FirebaseDatabase.getInstance();

        UserRef = database.getReference("User");
        TripInfoRef = database.getReference("TripInfo").child(userID);

        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvTitle.setText("여행 정보 수정");

        btnAdd = (Button) view.findViewById(R.id.btnAdd);

        etDeDate = (EditText) view.findViewById(R.id.etDeDate);
        etReDate = (EditText) view.findViewById(R.id.etReDate);
        cb20 = (CheckBox) view.findViewById(R.id.cb20);
        cb30 = (CheckBox) view.findViewById(R.id.cb30);
        cb40 = (CheckBox) view.findViewById(R.id.cb40);
        cb50 = (CheckBox) view.findViewById(R.id.cb50);
        cbBooking = (CheckBox) view.findViewById(R.id.cbBooking);

        spCountry = (Spinner) view.findViewById(R.id.spCountry);
        spTheme = (Spinner) view.findViewById(R.id.spTheme);

        // 프래그먼트에서 전달받은 값
        if(getArguments() != null){
            Bundle bundle = getArguments();
            uTripInfoID = bundle.getString("uTripInfoID");
            uCodeID = bundle.getString("uCodeID");
            uDeDate = bundle.getString("uDeDate");
            uReDate = bundle.getString("uReDate");
            uTheme = bundle.getString("uTheme");
            uAge = bundle.getString("uAge");
            uCDate = bundle.getString("uCDate");
            uIsChecked = bundle.getBoolean("uIsChecked");
        }

        etDeDate.setText(uDeDate);
        etReDate.setText(uReDate);

//        String[] uAges = uAge.split("(?<=\\G.{" + 2 + "})");
//
//        for(int i=0;i<uAges.length;i++) {
//            System.out.println(array[i]);
//        }

//        String a = sAge[0];
//        String b = sAge[1];
//        String c = sAge[2];
//        String d = sAge[3];


        // 출발일 자르기(현재 값: 0000년 0월 0일)
        iDateD = uDeDate.split(" ");

        String ssYear = iDateD[0];
        int index = ssYear.indexOf("년");
        iYearD = Integer.parseInt(ssYear.substring(0, index));

        String ssMonth = iDateD[1];
        int index2 = ssMonth.indexOf("월");
        iMonthD = Integer.parseInt(ssMonth.substring(0, index2));

        String ssDay = iDateD[2];
        int index3 = ssDay.indexOf("일");
        iDayD = Integer.parseInt(ssDay.substring(0, index3));

        // 도착일 자르기(현재 값: 0000년 0월 0일)
        iDateR = uReDate.split(" ");

        String ssYear2 = iDateR[0];
        int index4 = ssYear2.indexOf("년");
        iYearR = Integer.parseInt(ssYear2.substring(0, index4));

        String ssMonth2 = iDateR[1];
        int index5 = ssMonth2.indexOf("월");
        iMonthR = Integer.parseInt(ssMonth2.substring(0, index5));

        String ssDay2 = iDateR[2];
        int index6 = ssDay2.indexOf("일");
        iDayR = Integer.parseInt(ssDay2.substring(0, index6));

        // 나라 스피너
        adapterCountry = ArrayAdapter.createFromResource(getContext(),
                R.array.countryArray, android.R.layout.simple_spinner_item); // arrays파일 항목을 가지고 adapter 생성
        adapterCountry.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // 스피너 펼칠때 사용할 모양
        spCountry.setAdapter(adapterCountry); // 스피너에 어댑터 설정
        spCountry.setSelection(((ArrayAdapter<String>)spCountry.getAdapter()).getPosition(uCodeID));

        // 테마 스피너
        adapterTheme = ArrayAdapter.createFromResource(getContext(),
                R.array.themeArray, android.R.layout.simple_spinner_item); // arrays파일 항목을 가지고 adapter 생성
        adapterTheme.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // 스피너 펼칠때 사용할 모양
        spTheme.setAdapter(adapterTheme); // 스피너에 어댑터 설정
        spTheme.setSelection(((ArrayAdapter<String>)spTheme.getAdapter()).getPosition(uTheme));

        setHasOptionsMenu(true); // 반드시 해줘야 함!!
        setCancelable(false); // 바깥 영역 터치시 종료되는 거 막아줌

        // 나라 스피너 아이템 선택시 이벤트 처리
        spCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                // 스피너가 화면에 출력되면 첫번째 항목을 선택한 것으로 간주하기 때문에 이 메소드 무조건 호출됨. 이것을 방지하기 위해 코드 추가
//                if(!mSpinnerCountry){
//                    mSpinnerCountry = true;
//                    return;
//                }
                selectedCountry = parent.getItemAtPosition(position).toString(); // 선택한 아이템
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // 테마 스피너 아이템 선택시 이벤트 처리
        spTheme.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // 스피너가 화면에 출력되면 첫번째 항목을 선택한 것으로 간주하기 때문에 이 메소드 무조건 호출됨. 이것을 방지하기 위해 코드 추가
//                if(!mSpinnerTheme){
//                    mSpinnerTheme = true;
//                    return;
//                }
                selectedTheme = parent.getItemAtPosition(position).toString(); // 선택한 아이템
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // 출발일 선택
        etDeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                final Calendar minDateD = Calendar.getInstance();

                calendar.set(Calendar.YEAR, iYearD);
                calendar.set(Calendar.MONTH, iMonthD-1);
                calendar.set(Calendar.DAY_OF_MONTH, iDayD);

                yearD = calendar.get(Calendar.YEAR);
                monthD = calendar.get(Calendar.MONTH);
                dayD = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        etDeDate.setText(year + "년 " + (monthOfYear + 1) + "월 " + dayOfMonth + "일");
                        yearR = year;
                        monthR = monthOfYear;
                        dayR = dayOfMonth;
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
                final Calendar calendar = Calendar.getInstance();
                final Calendar minDateR = Calendar.getInstance();

                calendar.set(Calendar.YEAR, iYearR);
                calendar.set(Calendar.MONTH, iMonthR-1);
                calendar.set(Calendar.DAY_OF_MONTH, iDayR);
                yearR = calendar.get(Calendar.YEAR);
                monthR = calendar.get(Calendar.MONTH);
                dayR = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        etReDate.setText(year + "년 " + (monthOfYear + 1) + "월 " + dayOfMonth + "일");
                    }
                }, yearR, monthR, dayR);

                minDateR.set(yearD, monthD, dayD);
                datePickerDialog.getDatePicker().setMinDate(minDateR.getTime().getTime());

                datePickerDialog.show();
            }
        });

        // 여행 취향 선택 완료 버튼
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deDate = etDeDate.getText().toString();
                reDate = etReDate.getText().toString();

                // 동행인 연령대 선택
                if(cb20.isChecked()){
                    age = age + ""+ age20;
                }
                if(cb30.isChecked()){
                    age = age + ","+ age30;
                }
                if(cb40.isChecked()){
                    age = age + ","+ age40;
                }
                if(cb50.isChecked()){
                    age = age + ","+ age50;
                }


                try{
                    if(TextUtils.isEmpty(selectedCountry)){
                        Toast.makeText(getContext(), "나라를 선택해주세요", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else if(TextUtils.isEmpty(deDate)){
                        Toast.makeText(getContext(), "출발일을 선택해주세요", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else if(TextUtils.isEmpty(reDate)){
                        Toast.makeText(getContext(), "도착일을 선택해주세요", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else if(TextUtils.isEmpty(selectedTheme)){
                        Toast.makeText(getContext(), "테마를 선택해주세요", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else if(TextUtils.isEmpty(age)) {
                        Toast.makeText(getContext(), "동행인 연령대를 선택해주세요", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    updateTrip();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "뭔가 이상해요", Toast.LENGTH_SHORT).show();
                } finally {

                }
                dismiss();
            }
        });

        // 취소 버튼
        ibCancel = (ImageButton) view.findViewById(R.id.ibCancel);
        ibCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAllowingStateLoss();
            }
        });

        return view;
    }

    // 여행정보 DB 추가
    private void updateTrip(){
        TimeZone time;
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

        time = TimeZone.getTimeZone("Asia/Seoul");
        dateFormat.setTimeZone(time);
        uDate = dateFormat.format(date);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("TripInfo"); // tripInfo 접근. 밑에는 tripInfoID 있음
        String userID = auth.getUid(); // 현재 userID 가져오기

        try{
            if(cbBooking.isChecked()){ // 공개에 체크되어 있으면
                isChecked = true;

                TripInfo tripInfo = new TripInfo();
                tripInfo.setTripInfoID(uTripInfoID);
                tripInfo.setCodeID(selectedCountry);
                tripInfo.setDeDate(deDate);
                tripInfo.setReDate(reDate);
                tripInfo.setTheme(selectedTheme);
                tripInfo.setAge(age);
                tripInfo.setIsChecked(isChecked);
                tripInfo.setcDate(uCDate);
                tripInfo.setuDate(uDate);
                tripInfo.setUserID(userID);

                TripInfoRef.child(uTripInfoID).setValue(tripInfo);

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fgTripInfo, new TripListFragment()).commit();

                Toast.makeText(getContext(), "수정 완료(등록O)", Toast.LENGTH_SHORT).show();
            }
            else {
                isChecked = false;

                TripInfo tripInfo = new TripInfo();
                tripInfo.setTripInfoID(uTripInfoID);
                tripInfo.setCodeID(selectedCountry);
                tripInfo.setDeDate(deDate);
                tripInfo.setReDate(reDate);
                tripInfo.setTheme(selectedTheme);
                tripInfo.setAge(age);
                tripInfo.setIsChecked(isChecked);
                tripInfo.setcDate(uCDate);
                tripInfo.setuDate(uDate);
                tripInfo.setUserID(userID);

                TripInfoRef.child(uTripInfoID).setValue(tripInfo);

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fgTripInfo, new TripListFragment()).commit();

                Toast.makeText(getContext(), "수정 완료(등록X)", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "뭔가 이상해요", Toast.LENGTH_SHORT).show();
        }
        dismiss();
    }

    // 스피너 옵션 메뉴
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.action_settings, menu);
    }

    // 스피너 옵션 메뉴 선택했을 때
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }
}