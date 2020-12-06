package com.example.tkitaka_fb.MainMenuFragment.Trip;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.tkitaka_fb.MemberProfileFragment;
import com.example.tkitaka_fb.Model.PBMS;
import com.example.tkitaka_fb.Model.TripInfo;
import com.example.tkitaka_fb.Model.User;
import com.example.tkitaka_fb.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class TripSearchListFragment extends Fragment {
    public static TripSearchListFragment newInstance() {
        return new TripSearchListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private static final String TAG = "여행정보검색";

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference UserRef, TripInfoRef;

    TextView tvCountry, tvDate, tvEmptyData;

    RecyclerView rv;

    String CurrentUserID;

    List<PBMS> PBMSList;
    List<User> UserList;
    List<TripInfo> TripInfoList;
    TripSearchAdapter tripSearchAdapter;

    String accommondation, meal, sTransportation, lTransportation, expense, preplan, spending, flight, guide, smoking, tmi, userID2;

    // 가져온 값
    String uTripInfoID, uCodeID, uDeDate, uReDate, uTheme, uAge, uCDate;
    Boolean uIsChecked;

    String userID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_trip_search_list, container, false);

        tvCountry = (TextView) view.findViewById(R.id.tvCountry);
        tvDate = (TextView) view.findViewById(R.id.tvDate);
        tvEmptyData = (TextView) view.findViewById(R.id.tvEmptyData);

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

        tvCountry.setText(uCodeID);
        tvDate.setText(uDeDate);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        UserList = new ArrayList<>();
        PBMSList = new ArrayList<>();

        CurrentUserID = user.getUid();
        UserRef = database.getReference().child("User");

        // 됨
        Query query2 = database.getReference().child("TripInfo").child(uCodeID);
        query2.addValueEventListener(valueEventListener2);

        emptyData();

        tripSearchAdapter = new TripSearchAdapter(getContext(), UserList, false);

        rv = (RecyclerView) view.findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rv.setLayoutManager(linearLayoutManager);
        rv.setAdapter(tripSearchAdapter);

        // 멤버 프로필 보기
        tripSearchAdapter.setOnItemClickListener(new TripSearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                findMemberProfile(position);
            }

            @Override
            public void onSearchButtonClick(View view, int position) {

            }
        });

        return view;
    }

    // 맨 처음 호출. 빈 화면 보여줌
    public void emptyData(){
        TripInfoRef = database.getReference().child("TripInfo").child(uCodeID);
        Log.d(TAG, "일단 나라    "+uCodeID);
        TripInfoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount();
                Log.d(TAG, "2 자식아    "+count);
                if (count==1){
                    tvEmptyData.setVisibility(View.VISIBLE);
                }
                else {
                    tvEmptyData.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // 멤버프로필 다이얼로그
    private void findMemberProfile(int position) {
        User user = UserList.get(position);
        final String userID = user.getUserID();
        final String userName = user.getUserName();
        final String userIntro = user.getUserIntroduction();

        DatabaseReference PBMSRef = database.getReference().child("PBMS").child(userID);
        PBMSRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    PBMS pbms = dataSnapshot.getValue(PBMS.class);
                    accommondation = pbms.getAccommondation();
                    meal = pbms.getMeal();
                    sTransportation = pbms.getsTransportation();
                    lTransportation = pbms.getlTransportation();
                    expense = pbms.getExpense();
                    preplan = pbms.getPreplan();
                    spending = pbms.getSpending();
                    flight = pbms.getFlight();
                    guide = pbms.getGuide();
                    smoking = pbms.getSmoking();
                    tmi = pbms.getTmi();
                }

                MemberProfileFragment dialog = MemberProfileFragment.newInstance();
                Bundle bundle = new Bundle();
                bundle.putString("accommondation", accommondation);
                bundle.putString("meal", meal);
                bundle.putString("sTransportation", sTransportation);
                bundle.putString("lTransportation", lTransportation);
                bundle.putString("expense", expense);
                bundle.putString("preplan", preplan);
                bundle.putString("spending", spending);
                bundle.putString("flight", flight);
                bundle.putString("guide", guide);
                bundle.putString("smoking", smoking);
                bundle.putString("tmi", tmi);
                bundle.putString("userID", userID);
                bundle.putString("userIntro", userIntro);
                bundle.putString("userName", userName);

                dialog.setArguments(bundle);
                dialog.show(getFragmentManager(), "fragmentDialog");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // 데이터 읽기
    ValueEventListener valueEventListener2 = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()){
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    TripInfo tripInfo = snapshot.getValue(TripInfo.class); // 트립인포 테이블에서 데이터 검색
                    userID = tripInfo.getUserID();
                    Log.d(TAG, "1 userID    "+userID); // 나 포함 모두 나옴

                    DatabaseReference UserRef = database.getReference().child("User").child(userID);
                    UserRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                User user = dataSnapshot.getValue(User.class);

                                if (!user.getUserID().equals(CurrentUserID)){
                                    String a = user.getUserID();
                                    UserList.add(user);
                                    Log.d(TAG, "2 userID    "+a);
                                }
                            }
                            tripSearchAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            throw databaseError.toException();
                        }
                    });
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            throw databaseError.toException();
        }
    };
}
