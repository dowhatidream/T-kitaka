package com.example.tkitaka_fb;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tkitaka_fb.MainMenuFragment.Chat.ChattingActivity;
import com.example.tkitaka_fb.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;


public class MemberProfileFragment extends DialogFragment {

    FirebaseDatabase database;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference UserRef, PBMSRef;
    String currentUser;

    CircleImageView civProfile;
    ImageView ivOut;
    TextView tvUserIntro;
    TextView tvUserName;
    Button btnChat;
    TextView tvAccommondation, tvMeal, tvSTransportation, tvLTransportation, tvExpense, tvPreplan, tvSpending, tvFlight, tvGuide, tvSmoking, tvTMI;

    // pbms값
    String accommondation;
    String meal;
    String sTransportation;
    String lTransportation;
    String expense;
    String preplan;
    String spending;
    String flight;
    String guide;
    String smoking;
    String tmi;

    String userID;
    String userIntro;
    String userName;

    String myAccommondation = "";
    String myMeal = "";
    String mySTransportation = "";
    String myLTransportation = "";
    String myExpense = "";
    String myPreplan = "";
    String mySpending = "";
    String myFlight = "";
    String myGuide = "";
    String mySmoking = "";
    String myPBMS;

    private static final String TAG = "프로필";

    public static MemberProfileFragment newInstance(){
        return new MemberProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_member_profile, container, false);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        currentUser = user.getUid();

        setHasOptionsMenu(true); // 반드시 해줘야 함!!
//        setCancelable(false); // 바깥 영역 터치시 종료되는 거 막아줌

        civProfile = (CircleImageView) view.findViewById(R.id.civProfile);
        tvUserIntro = (TextView) view.findViewById(R.id.tvUserIntro);
        tvUserName = (TextView) view.findViewById(R.id.tvUserName);
        tvAccommondation = (TextView) view.findViewById(R.id.tvAccommondation);
        tvMeal = (TextView) view.findViewById(R.id.tvMeal);
        tvSTransportation = (TextView) view.findViewById(R.id.tvSTransportation);
        tvLTransportation = (TextView) view.findViewById(R.id.tvLTransportation);
        tvExpense = (TextView) view.findViewById(R.id.tvExpense);
        tvPreplan = (TextView) view.findViewById(R.id.tvPreplan);
        tvSpending = (TextView) view.findViewById(R.id.tvSpending);
        tvFlight = (TextView) view.findViewById(R.id.tvFlight);
        tvGuide = (TextView) view.findViewById(R.id.tvGuide);
        tvSmoking = (TextView) view.findViewById(R.id.tvSmoking);
        tvTMI = (TextView) view.findViewById(R.id.tvTMI);

        // 프래그먼트에서 전달받은 값
        if(getArguments() != null){
            Bundle bundle = getArguments();
            userID = bundle.getString("userID");
            userIntro = bundle.getString("userIntro");
            userName = bundle.getString("userName");
            accommondation = bundle.getString("accommondation");
            meal = bundle.getString("meal");
            sTransportation = bundle.getString("sTransportation");
            lTransportation = bundle.getString("lTransportation");
            expense = bundle.getString("expense");
            preplan = bundle.getString("preplan");
            spending = bundle.getString("spending");
            flight = bundle.getString("flight");
            guide = bundle.getString("guide");
            smoking = bundle.getString("smoking");
            tmi = bundle.getString("tmi");
        }

        tvUserName.setText(userName);
        tvUserIntro.setText(userIntro);
        tvAccommondation.setText(accommondation);
        tvMeal.setText(meal);
        tvSTransportation.setText(sTransportation);
        tvLTransportation.setText(lTransportation);
        tvExpense.setText(expense);
        tvPreplan.setText(preplan);
        tvSpending.setText(spending);
        tvFlight.setText(flight);
        tvGuide.setText(guide);
        tvSmoking.setText(smoking);
        tvTMI.setText(tmi);

        // 유저 프로필 찾기
        UserRef = database.getReference().child("User");
        UserRef.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (getContext()==null){
                    return;
                }
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue( User.class );

                    if (user.getUserProfile().equals("default")){
                        civProfile.setImageResource(R.mipmap.ic_launcher);
                    }
                    else {
                        Glide.with(getContext()).load(user.getUserProfile()).into(civProfile);
                    }
                }
                else {
                    Toast.makeText(getContext(), "프로필 없음..", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        findPBMSKorean();
        getMyPBMS();

        // 취소 버튼
        ivOut = (ImageView) view.findViewById(R.id.ivOut);
        ivOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAllowingStateLoss();
            }
        });

        // 채팅 버튼
        btnChat = (Button) view.findViewById(R.id.btnChat);
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ChattingActivity.class);
                intent.putExtra("userID", userID);
                String a = userID;
                getContext().startActivity(intent);
                Log.d(TAG, "어댑터에서 보냄   "+a);
            }
        });

        return view;
    }

    public void findPBMSKorean(){
        // 숙소
        switch (accommondation){
            case "A":
                tvAccommondation.setText("호텔");
                break;
            case "B":
                tvAccommondation.setText("무관");
                break;
            case "C":
                tvAccommondation.setText("호스텔");
                break;
        }

        // 식사
        switch (meal){
            case "A":
                tvMeal.setText("맛집");
                break;
            case "B":
                tvMeal.setText("무관");
                break;
            case "C":
                tvMeal.setText("현지식");
                break;
        }

        // 근거리
        switch (sTransportation){
            case "A":
                tvSTransportation.setText("택시");
                break;
            case "B":
                tvSTransportation.setText("무관");
                break;
            case "C":
                tvSTransportation.setText("대중교통");
                break;
        }

        // 장거리
        switch (lTransportation){
            case "A":
                tvLTransportation.setText("비행기");
                break;
            case "B":
                tvLTransportation.setText("무관");
                break;
            case "C":
                tvLTransportation.setText("버스/기차");
                break;
        }

        // 경비
        switch (expense){
            case "A":
                tvExpense.setText("사비");
                break;
            case "B":
                tvExpense.setText("무관");
                break;
            case "C":
                tvExpense.setText("공금");
                break;
        }

        // 사전계획
        switch (preplan){
            case "A":
                tvPreplan.setText("철저");
                break;
            case "B":
                tvPreplan.setText("무관");
                break;
            case "C":
                tvPreplan.setText("즉흥적");
                break;
        }

        // 지출 성향
        switch (spending){
            case "A":
                tvSpending.setText("여유");
                break;
            case "B":
                tvSpending.setText("무관");
                break;
            case "C":
                tvSpending.setText("알뜰");
                break;
        }

        // 항공편
        switch (flight){
            case "A":
                tvFlight.setText("대형");
                break;
            case "B":
                tvFlight.setText("무관");
                break;
            case "C":
                tvFlight.setText("저비용");
                break;
        }

        // 가이드
        switch (guide){
            case "A":
                tvGuide.setText("있음");
                break;
            case "B":
                tvGuide.setText("무관");
                break;
            case "C":
                tvGuide.setText("없음");
                break;
        }

        // 흡연
        switch (smoking){
            case "A":
                tvSmoking.setText("괜찮음");
                break;
            case "B":
                tvSmoking.setText("무관");
                break;
            case "C":
                tvSmoking.setText("불편함");
                break;
        }
    }

    public void getMyPBMS(){
        PBMSRef = database.getReference().child("PBMS").child(currentUser);
        PBMSRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    myAccommondation = dataSnapshot.child("accommondation").getValue(String.class);
                    myMeal = dataSnapshot.child("meal").getValue(String.class);
                    mySTransportation = dataSnapshot.child("sTransportation").getValue(String.class);
                    myLTransportation = dataSnapshot.child("lTransportation").getValue(String.class);
                    myExpense = dataSnapshot.child("expense").getValue(String.class);
                    myPreplan = dataSnapshot.child("preplan").getValue(String.class);
                    mySpending = dataSnapshot.child("spending").getValue(String.class);
                    myFlight = dataSnapshot.child("flight").getValue(String.class);
                    myGuide = dataSnapshot.child("guide").getValue(String.class);
                    mySmoking = dataSnapshot.child("smoking").getValue(String.class);

                    myPBMS = myAccommondation+myMeal+mySTransportation+myLTransportation+myExpense+myPreplan+mySpending+myFlight+myGuide+mySmoking;
                    Log.d(TAG, "my PBMS★   "+myPBMS);

                    compareToMine();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void compareToMine(){
        if (accommondation.matches(myAccommondation)||accommondation.matches("B")||myAccommondation.matches("B")){
            tvAccommondation.setTextColor(Color.parseColor("#ff5e3f"));
        }
        if (meal.matches(myMeal)||meal.matches("B")||myMeal.matches("B")){
            tvMeal.setTextColor(Color.parseColor("#ff5e3f"));
        }
        if (sTransportation.matches(mySTransportation)||sTransportation.matches("B")||mySTransportation.matches("B")){
            tvSTransportation.setTextColor(Color.parseColor("#ff5e3f"));
        }
        if (lTransportation.matches(myLTransportation)||lTransportation.matches("B")||myLTransportation.matches("B")){
            tvLTransportation.setTextColor(Color.parseColor("#ff5e3f"));
        }
        if (expense.matches(myExpense)||expense.matches("B")||myExpense.matches("B")){
            tvExpense.setTextColor(Color.parseColor("#ff5e3f"));
        }
        if (preplan.matches(myPreplan)||preplan.matches("B")||myPreplan.matches("B")){
            tvPreplan.setTextColor(Color.parseColor("#ff5e3f"));
        }
        if (spending.matches(mySpending)||spending.matches("B")||mySpending.matches("B")){
            tvSpending.setTextColor(Color.parseColor("#ff5e3f"));
        }
        if (flight.matches(myFlight)||flight.matches("B")||myFlight.matches("B")){
            tvFlight.setTextColor(Color.parseColor("#ff5e3f"));
        }
        if (guide.matches(myGuide)||guide.matches("B")||myGuide.matches("B")){
            tvGuide.setTextColor(Color.parseColor("#ff5e3f"));
        }
        if (smoking.matches(mySmoking)||smoking.matches("B")||mySmoking.matches("B")){
            tvSmoking.setTextColor(Color.parseColor("#ff5e3f"));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }
}