package com.example.tkitaka_fb.MainMenuFragment.Trip; // 이승연

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
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
import com.example.tkitaka_fb.Model.TripPopular;
import com.example.tkitaka_fb.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class AddTripFragment extends DialogFragment {

    public static AddTripFragment newInstance(){
        return new AddTripFragment();
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

    String cDate;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_add_trip, container, false);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userID = user.getUid();
        database = FirebaseDatabase.getInstance();

        UserRef = database.getReference("User");
        TripInfoRef = database.getReference("TripInfo");

        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvTitle.setText("여행 정보 추가");

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

        // 나라 스피너
        adapterCountry = ArrayAdapter.createFromResource(getContext(),
                R.array.countryArray, android.R.layout.simple_spinner_item); // arrays파일 항목을 가지고 adapter 생성
        adapterCountry.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // 스피너 펼칠때 사용할 모양
        spCountry.setAdapter(adapterCountry); // 스피너에 어댑터 설정

        // 테마 스피너
        adapterTheme = ArrayAdapter.createFromResource(getContext(),
                R.array.themeArray, android.R.layout.simple_spinner_item); // arrays파일 항목을 가지고 adapter 생성
        adapterTheme.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // 스피너 펼칠때 사용할 모양
        spTheme.setAdapter(adapterTheme); // 스피너에 어댑터 설정

        setHasOptionsMenu(true); // 반드시 해줘야 함!!
        setCancelable(false); // 바깥 영역 터치시 종료되는 거 막아줌

        // 나라 스피너 아이템 선택시 이벤트 처리
        spCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // 스피너가 화면에 출력되면 첫번째 항목을 선택한 것으로 간주하기 때문에 이 메소드 무조건 호출됨. 이것을 방지하기 위해 코드 추가
                if(!mSpinnerCountry){
                    mSpinnerCountry = true;
                    return;
                }
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
                if(!mSpinnerTheme){
                    mSpinnerTheme = true;
                    return;
                }
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
                final Calendar minDateR = Calendar.getInstance();

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        etReDate.setText(year + "년 " + (monthOfYear + 1) + "월 " + dayOfMonth + "일");

                    }
                }, yearR, monthR, dayR);

                minDateR.set(yearR, monthR, dayR);
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
                    age = age + ""+ age30;
                }
                if(cb40.isChecked()){
                    age = age + ""+ age40;
                }
                if(cb50.isChecked()){
                    age = age + ""+ age50;
                }

                try{
                    if(TextUtils.isEmpty(selectedCountry)){
                        Toast.makeText(getContext(), "나라를 선택해주세요", Toast.LENGTH_SHORT).show();
                        spCountry.requestFocus();
                        return;
                    }
                    else if(TextUtils.isEmpty(deDate)){
                        Toast.makeText(getContext(), "출발일을 선택해주세요", Toast.LENGTH_SHORT).show();
                        etDeDate.requestFocus();
                        return;
                    }
                    else if(TextUtils.isEmpty(reDate)){
                        Toast.makeText(getContext(), "도착일을 선택해주세요", Toast.LENGTH_SHORT).show();
                        etReDate.requestFocus();
                        return;
                    }
                    else if(TextUtils.isEmpty(selectedTheme)){
                        Toast.makeText(getContext(), "테마를 선택해주세요", Toast.LENGTH_SHORT).show();
                        spTheme.requestFocus();
                        return;
                    }
                    else if(TextUtils.isEmpty(age)) {
                        Toast.makeText(getContext(), "동행인 연령대를 선택해주세요", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    addTrip(); // 여행정보 DB에서 불러오기
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "뭔가 이상해요", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
    private void addTrip(){
        TimeZone time;
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

        time = TimeZone.getTimeZone("Asia/Seoul");
        dateFormat.setTimeZone(time);
        cDate = dateFormat.format(date);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("TripInfo"); // tripInfo 접근. 밑에는 tripInfoID 있음
        String tripInfoID = reference.push().getKey(); // 이게 기본키이자 고유키

        try{
            if(cbBooking.isChecked()){ // 공개에 체크되어 있으면
                isChecked = true;

                TripInfo tripInfo = new TripInfo();
                tripInfo.setTripInfoID(tripInfoID);
                tripInfo.setCodeID(selectedCountry);
                tripInfo.setDeDate(deDate);
                tripInfo.setReDate(reDate);
                tripInfo.setTheme(selectedTheme);
                tripInfo.setAge(age);
                tripInfo.setIsChecked(isChecked);
                tripInfo.setcDate(cDate);
                tripInfo.setuDate("");
                tripInfo.setUserID(userID);

                reference.child(userID).child(selectedCountry).setValue(tripInfo);
                reference.child(selectedCountry).child(userID).setValue(tripInfo);

                isPopular();
                Log.d("트립!!!!", "체크함 ");

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fgTripInfo, new TripListFragment()).commit();

                Toast.makeText(getContext(), "추가 완료(등록O)", Toast.LENGTH_SHORT).show();
            }
            else {
                TripInfo tripInfo = new TripInfo();
                tripInfo.setTripInfoID(tripInfoID);
                tripInfo.setCodeID(selectedCountry);
                tripInfo.setDeDate(deDate);
                tripInfo.setReDate(reDate);
                tripInfo.setTheme(selectedTheme);
                tripInfo.setAge(age);
                tripInfo.setIsChecked(isChecked);
                tripInfo.setcDate(cDate);
                tripInfo.setuDate("");
                tripInfo.setUserID(userID);

                reference.child(userID).child(selectedCountry).setValue(tripInfo);
                reference.child(selectedCountry).child(userID).setValue(tripInfo);

                isPopular();
                Log.d("트립!!!!", "체크안함 ");

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fgTripInfo, new TripListFragment()).commit();

                Toast.makeText(getContext(), "추가 완료(등록X)", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "뭔가 이상해요", Toast.LENGTH_SHORT).show();
        }
        dismiss();
    }

    public void isPopular(){
        final DatabaseReference reference2 = database.getReference("TripPopular");

        reference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(selectedCountry)){
                    String a = dataSnapshot.child(selectedCountry).getKey();
                    String b = dataSnapshot.child(selectedCountry).child("tripPopular").getValue().toString();

                    TripPopular tripPopular = new TripPopular();
                    tripPopular.setTripPopular(Integer.valueOf(b)+1);
                    tripPopular.setCountry(selectedCountry);

                    reference2.child(selectedCountry).setValue(tripPopular);

                    Log.d("트립!!!!", "나오나??bbbbbbbbbb★      "+b);
                }
                else {
                    TripPopular tripPopular = new TripPopular();
                    tripPopular.setTripPopular(1);
                    tripPopular.setCountry(selectedCountry);

                    reference2.child(selectedCountry).setValue(tripPopular);

                    Log.d("트립!!!!", "나오나?222      "+dataSnapshot);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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