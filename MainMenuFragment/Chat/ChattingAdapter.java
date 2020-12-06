package com.example.tkitaka_fb.MainMenuFragment.Chat;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.tkitaka_fb.MainMenuFragment.Trip.TripSearchAdapter;
import com.example.tkitaka_fb.Model.Chat;
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

import static android.view.View.GONE;

public class ChattingAdapter extends RecyclerView.Adapter<ChattingAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference UserRef;
    private String currentUser;
    private FirebaseDatabase database;

    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }

    // 아이템 리스트
    private OnItemClickListener mListener = null;
    private List<Chat> ChatList;
    private Context context;
    private String imageURL;

    private static final String TAG = "어댑터";

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }

    // 생성자에서 데이터 리스트 객체를 전달받음
    public ChattingAdapter(Context context, List<Chat> chat, String imageURL) {
        this.context = context;
        this.ChatList = chat;
        this.imageURL = imageURL;
    }

    public ChattingAdapter(OnItemClickListener listener) {
        this.mListener = listener;
    }

    // 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_right, parent, false);
            return new ViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_left, parent, false);
            return new ViewHolder(view);
        }
    }

    // position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Chat chat = ChatList.get(position);
        String yourID = chat.getSenderID();

        if (imageURL.equals("default")){
            holder.civProfile.setImageResource(R.mipmap.ic_launcher);
        }
        else {
            Glide.with(context).load(imageURL).into(holder.civProfile);
        }

        // 사진 유무 어우 눈빠지겠따
        if (!chat.getFile().equals("default")){ // 사진이 있으면
            Glide.with(context).load(chat.getFile()).into(holder.ivFile); // 사진 받아오고
            holder.ivFile.setVisibility(View.VISIBLE); // 사진뷰 보이고

            holder.tvTime2.setVisibility(View.VISIBLE); // 사진뷰 시간 보이고
            holder.tvTime2.setText(chat.getsTime()); // 메세지 시간 가져오고

            holder.tvMsg.setVisibility(View.GONE); // 메세지 안 보이고
            holder.tvTime.setVisibility(View.GONE); // 메세지 시간 안 보이고

            if(chat.getIsSeen()){ // 메세지 읽었으면
                holder.tvSeen.setVisibility(View.GONE);
                holder.tvSeen2.setVisibility(View.GONE);
            }
            else { // 메세지 안 읽었으면
                holder.tvSeen.setVisibility(View.GONE); // 메세지 읽음 없애고
                holder.tvSeen2.setVisibility(View.VISIBLE); // 사진 읽음 보이게 함
                holder.tvSeen2.setText("안 읽음");
            }
        }
        else if (chat.getFile().equals("default")){ // 사진이 없으면
            holder.ivFile.setVisibility(View.GONE); // 사진뷰 안 보이고
            holder.tvMsg.setVisibility(View.VISIBLE); // 메세지 보이고
            holder.tvMsg.setText(chat.getMessege()); // 메세지 가져오고
            holder.tvTime.setText(chat.getsTime()); // 메세지 시간 가져오고

            if(chat.getIsSeen()){ // 메세지 읽었으면
                holder.tvSeen.setVisibility(View.GONE);
                holder.tvSeen2.setVisibility(View.GONE);
            }
            else { // 메세지 안 읽었으면
                holder.tvSeen2.setVisibility(View.GONE); // 사진 읽음 없애고
                holder.tvSeen.setVisibility(View.VISIBLE); // 메세지 읽음 보이게 함
                holder.tvSeen.setText("안 읽음");
            }
        }

        database = FirebaseDatabase.getInstance();
        DatabaseReference UserRef = database.getReference().child("User").child(yourID);
        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    User user = dataSnapshot.getValue(User.class);
                    String userName = user.getUserName();

                    holder.tvUserName.setText(userName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMsg;
        private CircleImageView civProfile;
        private TextView tvSeen;
        private TextView tvTime;
        private TextView tvUserName;
        private ImageView ivFile;
        private TextView tvTime2;
        private TextView tvSeen2;


        public ViewHolder(View itemView) {
            super(itemView);
            tvMsg = itemView.findViewById(R.id.tvMsg);
            civProfile = itemView.findViewById(R.id.civProfile);
            tvSeen = itemView.findViewById(R.id.tvSeen);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            ivFile = itemView.findViewById(R.id.ivFile);
            tvTime2 = itemView.findViewById(R.id.tvTime2);
            tvSeen2 = itemView.findViewById(R.id.tvSeen2);

            civProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition() ;
                    if (position != RecyclerView.NO_POSITION) {
                        if (mListener != null) {
                            mListener.onItemClick(view, position) ;
                        }
                    }
                }
            });
        }
    }

    // 전체 데이터 갯수 리턴
    @Override
    public int getItemCount() {
        try {
            int size = ChatList.size();
            return size;
        } catch(NullPointerException ex) {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        currentUser = user.getUid();

        if (ChatList.get(position).getSenderID().equals(currentUser)){
            return MSG_TYPE_RIGHT;
        }
        else {
            return MSG_TYPE_LEFT;
        }
    }
}