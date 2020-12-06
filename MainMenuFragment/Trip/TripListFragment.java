package com.example.tkitaka_fb.MainMenuFragment.Trip; // 이승연

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tkitaka_fb.Model.AdminPost;
import com.example.tkitaka_fb.Model.TripInfo;
import com.example.tkitaka_fb.Model.TripPopular;
import com.example.tkitaka_fb.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TripListFragment extends Fragment {

    public static TripListFragment newInstance() {
        return new TripListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private static final String TAG = "여행정보리스트";

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference reference, reference2, UserRef, TripInfoRef, TripInfoRef2, TripPopularRef;

    Button btnAdd;
    TextView tvEmptyData;

    RecyclerView rv, rv2, rv3;

    String CurrentUserID;

    TripInfoAdapter tripInfoAdapter;
    TripPopularAdapter tripPopularAdapter;
    TripRecommandAdapter tripRecommandAdapter;
    List<TripInfo> tripInfoList;
    List<TripPopular> tripPopularList;
    List<AdminPost> tripRecommandList;

    TripInfo tripInfo;

    String uTripInfoID, uCodeID, uDeDate, uReDate, uTheme, uAge, uCDate;
    Boolean uIsChecked;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trip_list, container, false);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        CurrentUserID = user.getUid();
        UserRef = database.getReference().child("User");
        TripInfoRef = database.getReference().child("TripInfo").child(CurrentUserID);
        TripInfoRef2 = database.getReference().child("TripInfo");
        TripPopularRef = database.getReference().child("TripPopular");

        tvEmptyData = (TextView) view.findViewById(R.id.tvEmptyData);

        rv = (RecyclerView) view.findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rv.setLayoutManager(linearLayoutManager);
        tripInfoList = new ArrayList<>();
        tripInfoAdapter = new TripInfoAdapter(getContext(), tripInfoList);

        rv2 = (RecyclerView) view.findViewById(R.id.rv2);
        rv2.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getContext());
        rv2.setLayoutManager(linearLayoutManager2);
        tripPopularList = new ArrayList<>();
        tripPopularAdapter = new TripPopularAdapter(getContext(), tripPopularList);

        rv3 = (RecyclerView) view.findViewById(R.id.rv3);
        rv3.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager3 = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rv3.setLayoutManager(linearLayoutManager3);
        tripRecommandList = new ArrayList<>();
        tripRecommandAdapter = new TripRecommandAdapter(getContext(), tripRecommandList);

        btnAdd = (Button) view.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddTripFragment dialog = AddTripFragment.newInstance();
                dialog.show(getFragmentManager(), "fragmentDialog");
            }
        });

        tripInfoAdapter.setOnItemClickListener(new TripInfoAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                showActionsDialog(position);
            }

            @Override
            public void onSearchButtonClick(View view, int position) {
                sendActionsDialog(position);
            }
        });
        getFirebaseData(); // 읽어들임

        return view;
    }

    // 수정
    private void showActionsDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder
                .setCancelable(false).setMessage("수정 또는 삭제하시겠습니까?")
                .setPositiveButton("수정", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        updateData(position);;
                    }
                })
                .setNegativeButton("삭제",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                deleteData(position);

                                tripInfoList.remove(position);
                                rv.removeViewAt(position);
                                tripInfoAdapter.notifyItemRemoved(position);
                                tripInfoAdapter.notifyItemRangeChanged(position, tripInfoList.size());
                                tripInfoAdapter.notifyDataSetChanged();
                            }
                        })
                .setNeutralButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // 동행 찾기
    private void sendActionsDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder
                .setCancelable(false).setMessage("동행을 찾으러 가볼까요?")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        sendData(position);
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void getFirebaseData(){
//        final ProgressDialog progressDialog = new ProgressDialog(getContext());
//        progressDialog.setMessage("여행정보 불러오는 중...");
//        progressDialog.show();

        reference = database.getReference("TripInfo").child(CurrentUserID);
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()){
                    TripInfo tripInfo = dataSnapshot.getValue(TripInfo.class);
                    Log.d("트립리스트", "트립인포    "+tripInfo);
                    tripInfoList.add(0, tripInfo);
                    rv.setAdapter(tripInfoAdapter);
//                    progressDialog.dismiss();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
        read();
        read2();
    }

    public void read(){
        reference2 = database.getReference("TripPopular");
        reference2.orderByChild("tripPopular").limitToLast(7).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    tripPopularList.add(0, snapshot.getValue(TripPopular.class));
                    rv2.setAdapter(tripPopularAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void read2(){
        reference2 = database.getReference("AdminPost").child("여행지 추천");
        reference2.limitToLast(10).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    tripRecommandList.add(0, snapshot.getValue(AdminPost.class));
                    rv3.setAdapter(tripRecommandAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // 데이터 전송
    public void sendData(int position){
        tripInfo = tripInfoList.get(position);

        uTripInfoID = tripInfo.getTripInfoID();
        uCodeID = tripInfo.getCodeID();
        uDeDate = tripInfo.getDeDate();
        uReDate = tripInfo.getReDate();
        uTheme = tripInfo.getTheme();
        uCDate = tripInfo.getcDate();
        uAge = tripInfo.getAge();
        uIsChecked = tripInfo.getIsChecked();

        Bundle bundle = new Bundle();
        bundle.putString("uTripInfoID", uTripInfoID);
        bundle.putString("uCodeID", uCodeID);
        bundle.putString("uDeDate", uDeDate);
        bundle.putString("uReDate", uReDate);
        bundle.putString("uTheme", uTheme);
        bundle.putString("uAge", uAge);
        bundle.putString("uCDate", uCDate);
        bundle.putBoolean("uIsChecked", uIsChecked);

        TripSearchListFragment fragment = new TripSearchListFragment();
        fragment.setArguments(bundle); // 데이터 넘기는 부분


        getActivity().getSupportFragmentManager().popBackStack(fragment.getClass().getSimpleName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fgTripInfo, fragment).commit();
        getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(fragment.getClass().getSimpleName());
    }

    // 데이터 수정
    private void updateData(final int position){
        try{
            tripInfo = tripInfoList.get(position);

            uTripInfoID = tripInfo.getTripInfoID();
            uCodeID = tripInfo.getCodeID();
            uDeDate = tripInfo.getDeDate();
            uReDate = tripInfo.getReDate();
            uTheme = tripInfo.getTheme();
            uCDate = tripInfo.getcDate();
            uAge = tripInfo.getAge();
            uIsChecked = tripInfo.getIsChecked();

            UpdateTripFragment dialog = UpdateTripFragment.newInstance();

            Bundle bundle = new Bundle();
            bundle.putString("uTripInfoID", uTripInfoID);
            bundle.putString("uCodeID", uCodeID);
            bundle.putString("uDeDate", uDeDate);
            bundle.putString("uReDate", uReDate);
            bundle.putString("uTheme", uTheme);
            bundle.putString("uAge", uAge);
            bundle.putString("uCDate", uCDate);
            bundle.putBoolean("uIsChecked", uIsChecked);

            dialog.setArguments(bundle);
            dialog.show(getFragmentManager(), "fragmentDialog");
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getContext(), "뭔가 이상해요ㅠ", Toast.LENGTH_SHORT).show();
        }
    }

    // 데이터 삭제
    private void deleteData(int position) {
        try{
            final String codeID = tripInfoList.get(position).getCodeID();

            TripInfoRef.child(codeID).removeValue();
            TripInfoRef2.child(codeID).child(CurrentUserID).removeValue();
            Log.d("트립리스트", "나옴1     ");

            TripPopularRef.child(codeID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        String b = dataSnapshot.child("tripPopular").getValue().toString();

                        if (b.matches("1")){
                            TripPopularRef.child(codeID).removeValue();
                        }
                        else {
                            TripPopularRef.child(codeID).child("country").setValue(codeID);
                            TripPopularRef.child(codeID).child("tripPopular").setValue(Integer.valueOf(b)-1);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            Toast.makeText(getContext(), "삭제 완료", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "뭔가 이상해요ㅠ", Toast.LENGTH_SHORT).show();
        }
    }

    public static class TripInfoAdapter extends RecyclerView.Adapter<TripInfoAdapter.ViewHolder>{
        List<TripInfo> tripInfoList;
        private OnItemClickListener mListener = null;
        private Context context;

        public TripInfoAdapter(Context context, List<TripInfo> list){
            this.context = context;
            this.tripInfoList = list;
        }

        public interface OnItemClickListener{
            void onItemClick(View view, int position);
            void onSearchButtonClick(View view, int position);
        }

        public void setOnItemClickListener(OnItemClickListener listener){
            this.mListener = listener;
        }

        public TripInfoAdapter(OnItemClickListener listener) {
            this.mListener = listener;
        }

        @NonNull
        @Override
        public TripInfoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_triplist, viewGroup, false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull TripInfoAdapter.ViewHolder holder, int position) {
            TripInfo tripInfo = tripInfoList.get(position);

            holder.tvCountry.setText(tripInfo.getCodeID());
            holder.tvDate.setText(tripInfo.getDeDate());
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            TextView tvCountry;
            TextView tvDate;
            ImageButton ibEdit;
            ImageButton ibSearch;

            public ViewHolder(View itemView){
                super(itemView);

                tvCountry = itemView.findViewById(R.id.tvCountry);
                tvDate = itemView.findViewById(R.id.tvDate);
                ibEdit = itemView.findViewById(R.id.ibEdit);
                ibSearch = itemView.findViewById(R.id.ibSearch);

                ibEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition() ;
                        if (position != RecyclerView.NO_POSITION) {
                            if (mListener != null) {
                                mListener.onItemClick(view, position);
                                notifyItemChanged(position);
                            }
                        }
                    }
                });

                ibSearch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition() ;
                        if (position != RecyclerView.NO_POSITION) {
                            if (mListener != null) {
                                mListener.onSearchButtonClick(view, position) ;
                            }
                        }
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return tripInfoList.size();
        }
    }

    public static class TripPopularAdapter extends RecyclerView.Adapter<TripPopularAdapter.ViewHolder>{
        List<TripPopular> tripPopularList;
        private Context context;

        public TripPopularAdapter(Context context, List<TripPopular> list){
            this.context = context;
            this.tripPopularList = list;
        }

        @NonNull
        @Override
        public TripPopularAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_tripplace, viewGroup, false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull TripPopularAdapter.ViewHolder holder, int position) {
            TripPopular tripPopular = tripPopularList.get(position);
            holder.tvCountry.setText(tripPopular.getCountry());
            holder.tvRanking.setText(String.valueOf(position+1)+"위");
            holder.tvRankNum.setText(String.valueOf(tripPopular.getTripPopular())+"명");

            if (String.valueOf(position+1).matches("1")){
                holder.ivLocation.setColorFilter(Color.parseColor("#FFD700"), PorterDuff.Mode.SRC_ATOP);
            }
            else if (String.valueOf(position+1).matches("2")){
                holder.ivLocation.setColorFilter(Color.parseColor("#C0C0C0"), PorterDuff.Mode.SRC_ATOP);
            }
            else if (String.valueOf(position+1).matches("3")){
                holder.ivLocation.setColorFilter(Color.parseColor("#CDA632"), PorterDuff.Mode.SRC_ATOP);
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            TextView tvRanking;
            TextView tvCountry;
            ImageView ivLocation;
            TextView tvRankNum;

            public ViewHolder(View itemView){
                super(itemView);
                tvRanking = itemView.findViewById(R.id.tvRanking);
                tvCountry = itemView.findViewById(R.id.tvCountry);
                ivLocation = itemView.findViewById(R.id.ivLocation);
                tvRankNum = itemView.findViewById(R.id.tvRankNum);
            }
        }

        @Override
        public int getItemCount() {
            return tripPopularList.size();
        }
    }

    public static class TripRecommandAdapter extends RecyclerView.Adapter<TripRecommandAdapter.ViewHolder>{
        List<AdminPost> tripRecommandList;
        private Context context;

        public TripRecommandAdapter(Context context, List<AdminPost> list){
            this.context = context;
            this.tripRecommandList = list;
        }

        @NonNull
        @Override
        public TripRecommandAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_recommand, viewGroup, false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull TripRecommandAdapter.ViewHolder holder, int position) {
            AdminPost adminPost = tripRecommandList.get(position);
            holder.tvRecommandCountry.setText(adminPost.getTitle());

            if (!adminPost.getPostImage().equals("")){
                Glide.with(context).load(adminPost.getPostImage()).into(holder.civRecommand);
                holder.civRecommand.setColorFilter(Color.parseColor("#BDBDBD"),PorterDuff.Mode.MULTIPLY);
            }
            else {
                holder.civRecommand.setImageResource(R.mipmap.ic_launcher);
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            CircleImageView civRecommand;
            TextView tvRecommandCountry;

            public ViewHolder(View itemView){
                super(itemView);
                civRecommand = itemView.findViewById(R.id.civRecommand);
                tvRecommandCountry = itemView.findViewById(R.id.tvRecommandCountry);
            }
        }

        @Override
        public int getItemCount() {
            return tripRecommandList.size();
        }
    }
}