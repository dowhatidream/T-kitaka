package com.example.tkitaka_fb.MainMenuFragment.Trip; // 이승연

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.tkitaka_fb.MainMenuFragment.Chat.ChattingActivity;
import com.example.tkitaka_fb.Model.Chat;
import com.example.tkitaka_fb.Model.PBMS;
import com.example.tkitaka_fb.Model.User;
import com.example.tkitaka_fb.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TripSearchAdapter extends RecyclerView.Adapter<TripSearchAdapter.ViewHolder> {

    public TripSearchAdapter() {
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onSearchButtonClick(View view, int position);
    }

    // 아이템 리스트
    private List<User> UserList;
    private List<PBMS> PBMSList;
    private OnItemClickListener mListener = null;
    private Context context;
    int score;
    private boolean isChat;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference UserRef, PBMSRef, ChatRef;
    private String currentUser;
    private FirebaseDatabase database;

    String accommondation = "";
    String meal = "";
    String sTransportation = "";
    String lTransportation = "";
    String expense = "";
    String preplan = "";
    String spending = "";
    String flight = "";
    String guide = "";
    String smoking = "";

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

    String theLastMSG;
    String myPBMS;
    String matchingUser;
    String yourPBMS;

    String noProblem = " ";

    String kAccommondation = "숙소";
    String kMeal = "식사";
    String kSTransportation = "근거리";
    String kLTransportation = "장거리";
    String kExpense = "경비";
    String kPreplan = "계획";
    String kSpending = "지출";
    String kFlight = "항공";
    String kGuide = "가이드";
    String kSmoking = "흡연";

    private static final String TAG = "어댑터";

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public TripSearchAdapter(Context context, List<User> UserList, boolean isChat) {
        this.context = context;
        this.UserList = UserList;
        this.isChat = isChat;
    }

    public TripSearchAdapter(OnItemClickListener listener) {
        this.mListener = listener;
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName;
        TextView tvMSGLast;
        TextView tvMatchingRate;
        CircleImageView civProfilePic;
        CircleImageView civStatusOn;
        CircleImageView civStatusOff;

        public ViewHolder(View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvMSGLast = itemView.findViewById(R.id.tvMSGLast);
            tvMatchingRate = itemView.findViewById(R.id.tvMatchingRate);
            civProfilePic = itemView.findViewById(R.id.civProfilePic);
            civStatusOn = itemView.findViewById(R.id.civStatusOn);
            civStatusOff = itemView.findViewById(R.id.civStatusOff);

            civProfilePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        if (mListener != null) {
                            mListener.onItemClick(view, position);
                        }
                    }
                }
            });
        }
    }

    // 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴
    @Override
    public TripSearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_search, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    // position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시
    @Override
    public void onBindViewHolder(TripSearchAdapter.ViewHolder holder, int position) {
        final User user = UserList.get(position);
        matchingUser = user.getUserID();
        Log.d(TAG, "user from adapter   " + matchingUser);
        getMyPBMS(); // 나
        getYourPBMS(holder); // 너

        holder.tvUserName.setText(user.getUserName());

        if (user.getUserProfile().equals("default")) {
            holder.civProfilePic.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(context).load(user.getUserProfile()).into(holder.civProfilePic);
        }

        if (isChat) {
            lastMessage(user.getUserID(), holder.tvMSGLast);
            holder.tvMSGLast.setTextSize(15);
        } else {
//            holder.tvMSGLast.setVisibility(View.GONE);
        }

        // 채팅 접속 상태
        if (isChat) {
            if (user.getUserStatus().equals("online")) {
                holder.civStatusOn.setVisibility(View.VISIBLE);
                holder.civStatusOff.setVisibility(View.GONE);
                holder.tvMatchingRate.setVisibility(View.GONE);
            } else if (user.getUserStatus().equals("offline")) {
                holder.civStatusOn.setVisibility(View.GONE);
                holder.civStatusOff.setVisibility(View.VISIBLE);
                holder.tvMatchingRate.setVisibility(View.GONE);
            }
        } else {
            holder.civStatusOn.setVisibility(View.GONE);
            holder.civStatusOff.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChattingActivity.class);
                intent.putExtra("userID", user.getUserID());
//                intent.putExtra("userName", user.getUserName());
                String a = user.getUserID();
                context.startActivity(intent);
                Log.d(TAG, "어댑터에서 보냄   " + a);
            }
        });
    }

    // 채팅리스트에서 마지막 대화내용 보이게 함 하아아아아ㅠㅠㅠㅠ
    public void lastMessage(final String userId, final TextView lastMSG) {
        theLastMSG = "default";

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        currentUser = user.getUid();

        database = FirebaseDatabase.getInstance();
        ChatRef = database.getReference().child("Chat");
        ChatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);

                    if (chat.getReceiverID().equals(currentUser) && chat.getSenderID().equals(userId) || chat.getReceiverID().equals(userId) && chat.getSenderID().equals(currentUser)) {
                        theLastMSG = chat.getMessege();
                    }
                }
                switch (theLastMSG) {
                    case "default":
                        lastMSG.setText("대화 내역이 존재하지 않습니다");
                        break;
                    default:
                        lastMSG.setText(theLastMSG);
                        break;
                }
                theLastMSG = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getMyPBMS() {
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        user = auth.getCurrentUser();
        currentUser = user.getUid();

        // PBMS 내 값
        PBMSRef = database.getReference().child("PBMS").child(currentUser);
        PBMSRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
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

                    myPBMS = myAccommondation + myMeal + mySTransportation + myLTransportation + myExpense + myPreplan + mySpending + myFlight + myGuide + mySmoking;
                    Log.d(TAG, "my PBMS★   " + myPBMS);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void getYourPBMS(final TripSearchAdapter.ViewHolder holder) {
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        // PBMS 너꺼
        PBMSRef = database.getReference().child("PBMS").child(matchingUser);
        PBMSRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    accommondation = dataSnapshot.child("accommondation").getValue(String.class);
                    meal = dataSnapshot.child("meal").getValue(String.class);
                    sTransportation = dataSnapshot.child("sTransportation").getValue(String.class);
                    lTransportation = dataSnapshot.child("lTransportation").getValue(String.class);
                    expense = dataSnapshot.child("expense").getValue(String.class);
                    preplan = dataSnapshot.child("preplan").getValue(String.class);
                    spending = dataSnapshot.child("spending").getValue(String.class);
                    flight = dataSnapshot.child("flight").getValue(String.class);
                    guide = dataSnapshot.child("guide").getValue(String.class);
                    smoking = dataSnapshot.child("smoking").getValue(String.class);

                    yourPBMS = accommondation + meal + sTransportation + lTransportation + expense + preplan + spending + flight + guide + smoking;
                    Log.d(TAG, "your PBMS★   " + yourPBMS);
                    getMatching();

                    if (score >= 9) {
                        holder.tvMatchingRate.setBackgroundResource(R.drawable.background_high);
                        holder.tvMatchingRate.setText(String.valueOf(score * 10) + "%");
                    }
                    else if (score >= 6) {
                        holder.tvMatchingRate.setBackgroundResource(R.drawable.background_middle);
                        holder.tvMatchingRate.setText(String.valueOf(score * 10) + "%");
                    }
                    else if (score >= 3) {
                        holder.tvMatchingRate.setBackgroundResource(R.drawable.background_low);
                        holder.tvMatchingRate.setText(String.valueOf(score * 10) + "%");
                    }
                    else {
                        holder.tvMatchingRate.setText(String.valueOf(score * 10) + "%");
                    }

                    holder.tvMSGLast.setText(noProblem);
                    holder.tvMSGLast.setTextSize(12);

                    score = 0;
                    noProblem = "";
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void getMatching() {
        score = 0;
        if (accommondation.matches(myAccommondation) || accommondation.matches("B") || myAccommondation.matches("B")) {
            score = score + 1;
            if (accommondation.matches("B") || myAccommondation.matches("B")) {
                noProblem = noProblem + " " + kAccommondation;
            }
        }
        if (meal.matches(myMeal) || meal.matches("B") || myMeal.matches("B")) {
            score = score + 1;
            if (meal.matches("B") || myMeal.matches("B")) {
                noProblem = noProblem + " " + kMeal;
            }
        }
        if (sTransportation.matches(mySTransportation) || sTransportation.matches("B") || mySTransportation.matches("B")) {
            score = score + 1;
            if (sTransportation.matches("B") || mySTransportation.matches("B")) {
                noProblem = noProblem + " " + kSTransportation;
            }
        }
        if (lTransportation.matches(myLTransportation) || lTransportation.matches("B") || myLTransportation.matches("B")) {
            score = score + 1;
            if (lTransportation.matches("B") || myLTransportation.matches("B")) {
                noProblem = noProblem + " " + kLTransportation;
            }
        }
        if (expense.matches(myExpense) || expense.matches("B") || myExpense.matches("B")) {
            score = score + 1;
            if (expense.matches("B") || myExpense.matches("B")) {
                noProblem = noProblem + " " + kExpense;
            }
        }
        if (preplan.matches(myPreplan) || preplan.matches("B") || myPreplan.matches("B")) {
            score = score + 1;
            if (preplan.matches("B") || myPreplan.matches("B")) {
                noProblem = noProblem + " " + kPreplan;
            }
        }
        if (spending.matches(mySpending) || spending.matches("B") || mySpending.matches("B")) {
            score = score + 1;
            if (spending.matches("B") || mySpending.matches("B")) {
                noProblem = noProblem + " " + kSpending;
            }
        }
        if (flight.matches(myFlight) || flight.matches("B") || myFlight.matches("B")) {
            score = score + 1;
            if (flight.matches("B") || myFlight.matches("B")) {
                noProblem = noProblem + " " + kFlight;
            }
        }
        if (guide.matches(myGuide) || guide.matches("B") || myGuide.matches("B")) {
            score = score + 1;
            if (guide.matches("B") || myGuide.matches("B")) {
                noProblem = noProblem + " " + kGuide;
            }
        }
        if (smoking.matches(mySmoking) || smoking.matches("B") || mySmoking.matches("B")) {
            score = score + 1;
            if (smoking.matches("B") || mySmoking.matches("B")) {
                noProblem = noProblem + " " + kSmoking;
            }
        }
        Log.d(TAG, "our PBMS★   " + score);
        Log.d(TAG, "무관항목★   " + noProblem);
    }

    // 전체 데이터 갯수 리턴
    @Override
    public int getItemCount() {
        try {
            int size = UserList.size();
            return size;
        } catch (NullPointerException ex) {
            return 0;
        }
    }
}