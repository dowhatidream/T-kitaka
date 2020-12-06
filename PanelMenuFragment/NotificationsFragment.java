package com.example.tkitaka_fb.PanelMenuFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.tkitaka_fb.MainMenuFragment.Board.AddPostFragment;
import com.example.tkitaka_fb.MainMenuFragment.Board.BoardListFragment;
import com.example.tkitaka_fb.MainMenuFragment.Board.DetailPostFragment;
import com.example.tkitaka_fb.MainMenuFragment.Board.UpdatePostFragment;
import com.example.tkitaka_fb.Model.AdminPost;
import com.example.tkitaka_fb.Model.Post;
import com.example.tkitaka_fb.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private  FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseDatabase database;

    private DatabaseReference reference, MyPostRef, AllPostRef;

    NotificationsFragment.AdPostAdapter adPostAdapter;
    List<AdminPost> list;

    String CurrentUserID, allPostUserID;

    RecyclerView rvAdPost;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        // 현재 유저 불러오기
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        CurrentUserID = user.getUid();

        //어댑터
        rvAdPost = (RecyclerView) view.findViewById(R.id.rvPost);
        rvAdPost.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvAdPost.setLayoutManager(linearLayoutManager);
        list = new ArrayList<>();
        adPostAdapter = new NotificationsFragment.AdPostAdapter(getContext(), list);

        //데이터 메소드
        getFirebaseData();
        getFirebaseData2();

        return view;
    }

    //데이터 메소드
    void getFirebaseData(){
        reference = database.getReference("AdminPost").child("공지사항");
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                AdminPost adminPost = dataSnapshot.getValue(AdminPost.class);
                list.add(0, adminPost);
                rvAdPost.setAdapter(adPostAdapter);
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    void getFirebaseData2(){
        reference = database.getReference("AdminPost").child("이벤트");
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                AdminPost adminPost = dataSnapshot.getValue(AdminPost.class);
                list.add(0, adminPost);
                rvAdPost.setAdapter(adPostAdapter);
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    //게시글 어댑터
    public static class AdPostAdapter extends RecyclerView.Adapter<NotificationsFragment.AdPostAdapter.ViewHolder>{
        public RequestManager mGlideRequestManager;

        private Context context;

        List<AdminPost> listArray;

        public  AdPostAdapter( Context context, List<AdminPost> list){
            this.listArray = list;
            this.context = context;
        }

        @NonNull
        @Override
        public NotificationsFragment.AdPostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_notifications, viewGroup, false);
            NotificationsFragment.AdPostAdapter.ViewHolder holder = new NotificationsFragment.AdPostAdapter.ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull final NotificationsFragment.AdPostAdapter.ViewHolder holder, int position) {
            final AdminPost adminPost = listArray.get(position);
            holder.tvDate.setText(adminPost.getDate());
            holder.tvTitle.setText(adminPost.getTitle());
            holder.tvCategory.setText("[" + adminPost.getCategory() + "]");
            holder.tvName.setText(adminPost.getUserName());
            holder.tvContent.setText(adminPost.getContent());

            //게시글 이미지 미리보기
            if (adminPost.getPostImage().equals("default")){
                holder.ivPic.setColorFilter(null);
            }
            else {
                mGlideRequestManager = Glide.with(context);
                mGlideRequestManager.load(adminPost.getPostImage()).into(holder.ivPic);
            }

            //세부내용 보기 버튼
            holder.ivExpandableBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    holder.ivExpandableBtn.setVisibility(View.GONE);
                    holder.ivBackBtn.setVisibility(View.VISIBLE);
                    holder.tvContent.setVisibility(View.VISIBLE);
                    holder.ivPic.setVisibility(View.VISIBLE);
                }
            });


            //세부내용 접기 버튼
            holder.ivBackBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    holder.ivExpandableBtn.setVisibility(View.VISIBLE);
                    holder.ivBackBtn.setVisibility(View.GONE);
                    holder.tvContent.setVisibility(View.GONE);
                    holder.ivPic.setVisibility(View.GONE);
                }
            });
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            TextView tvCategory;
            TextView tvTitle;
            TextView tvDate;
            TextView tvName;
            TextView tvContent;
            ImageView ivPic;
            RelativeLayout viewForeground;
            RelativeLayout llContent;
            ImageButton ivExpandableBtn, ivBackBtn;

            public ViewHolder(View itemView) {
                super(itemView);

                tvCategory=itemView.findViewById(R.id.tvCategory);
                tvTitle=itemView.findViewById(R.id.tvTitle);
                tvDate=itemView.findViewById(R.id.tvDate);
                tvName=itemView.findViewById(R.id.tvName);
                viewForeground=itemView.findViewById(R.id.view_foreground);

                ivExpandableBtn=itemView.findViewById(R.id.ivExpandableBtn);
                ivBackBtn=itemView.findViewById(R.id.ivBackBtn);
                tvContent=itemView.findViewById(R.id.tvContent);
                ivPic = itemView.findViewById(R.id.ivPic);

                ivBackBtn.setVisibility(View.GONE);
                tvContent.setVisibility(View.GONE);
                ivPic.setVisibility(View.GONE);
            }
        }
        @Override
        public int getItemCount() {
            return listArray.size();
        }
    }
}


