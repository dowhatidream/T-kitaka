package com.example.tkitaka_fb.PanelMenuFragment; //정다연

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.tkitaka_fb.MainMenuFragment.Board.DetailPostFragment;
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

public class PostFragment extends Fragment {
    private  FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseDatabase database;

    private DatabaseReference reference, reference2;

    String CurrentUserID;

    PostFragment.PostAdapter postAdapter;
    List<Post> list;

    RecyclerView rvPost;

    public PostFragment() {
        super();
    }

    public static PostFragment newInstance() {
        PostFragment fragment = new PostFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_post, container, false);

        // 현재 유저 불러오기
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        CurrentUserID = user.getUid();

        //어댑터
        rvPost = (RecyclerView) view.findViewById(R.id.rvPost);
        rvPost.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvPost.setLayoutManager(linearLayoutManager);

        list = new ArrayList<>();
        postAdapter = new PostFragment.PostAdapter(getContext(), list);

        //데이터 메소드
        getFirebaseData();

        //어댑터 클릭 리스너
        postAdapter.setOnItemClickListener(new PostFragment.PostAdapter.OnItemClickListener() {
            @Override
            public void onDetailClick(View view, int position) {
                DetailPostFragment detailPostFragment = new DetailPostFragment();

                Post post = list.get(position);
                String postKey =  post.getPostID();
                String title =  post.getTitle();
                String category =  post.getCategory();
                String date =  post.getDate();
                String content =  post.getContent();
                String userName =  post.getUserName();
                String userID =  post.getUserID();
                String postImage =  post.getPostImage();
                String userProfilePic =  post.getUserProfilePic();

                Bundle bundle = new Bundle();
                bundle.putString("PostID", postKey);
                bundle.putString("Title", title);
                bundle.putString("Category", category);
                bundle.putString("Date", date);
                bundle.putString("Content", content);
                bundle.putString("UserName", userName);
                bundle.putString("UserID", userID);
                bundle.putString("PostImage", postImage);
                bundle.putString("UserProfilePic", userProfilePic);

                detailPostFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, detailPostFragment).commit();
            }
        });
        return view;
    }

    //데이터 메소드
    void getFirebaseData(){
        reference = database.getReference("MyPost").child(CurrentUserID);
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Post post = dataSnapshot.getValue(Post.class);
                list.add(0, post);
                rvPost.setAdapter(postAdapter);

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
    public static class PostAdapter extends RecyclerView.Adapter<PostFragment.PostAdapter.ViewHolder>{

        public RequestManager mGlideRequestManager;

        public interface OnItemClickListener{
            void onDetailClick(View view, int position);
        }

        private Context context;
        private PostFragment.PostAdapter.OnItemClickListener mListener = null;

        public void setOnItemClickListener(PostFragment.PostAdapter.OnItemClickListener listener){
            this.mListener = listener;
        }

        public PostAdapter(PostFragment.PostAdapter.OnItemClickListener listener) {
            this.mListener = listener;
        }

        List<Post> listArray;

        public  PostAdapter( Context context, List<Post> list){
            this.listArray = list;
            this.context = context;
        }

        @NonNull
        @Override
        public PostFragment.PostAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_post, viewGroup, false);
            PostFragment.PostAdapter.ViewHolder holder = new PostFragment.PostAdapter.ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull final PostFragment.PostAdapter.ViewHolder holder, int position) {
            final Post post = listArray.get(position);
            holder.tvDate.setText(post.getDate());
            holder.tvTitle.setText(post.getTitle());
            holder.tvCategory.setText("[" + post.getCategory() + "]");
            holder.tvName.setText(post.getUserName());
            holder.tvCount.setText(String.valueOf(post.getPostCount()));

            //게시글 이미지 미리보기
            if (post.getPostImage().equals("default")){
                holder.ivPic.setColorFilter(null);
            }
            else {
                mGlideRequestManager = Glide.with(context);
                mGlideRequestManager.load(post.getPostImage()).into(holder.ivPic);
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            TextView tvCategory;
            TextView tvTitle;
            TextView tvDate;
            TextView tvName;
            TextView tvCount;
            ImageView ivPic;
            RelativeLayout viewForeground;
            RelativeLayout llContent;

            public ViewHolder(View itemView) {
                super(itemView);

                tvCategory=itemView.findViewById(R.id.tvCategory);
                tvTitle=itemView.findViewById(R.id.tvTitle);
                tvDate=itemView.findViewById(R.id.tvDate);
                tvName=itemView.findViewById(R.id.tvName);
                tvCount=itemView.findViewById(R.id.tvCount);
                ivPic=itemView.findViewById(R.id.ivPic);
                viewForeground=itemView.findViewById(R.id.view_foreground);
                llContent=itemView.findViewById(R.id.llContent);

                llContent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition() ;
                        if (position != RecyclerView.NO_POSITION) {
                            if (mListener != null) {
                                mListener.onDetailClick(view, position);
                            }
                        }
                    }
                });
            }
        }
        @Override
        public int getItemCount() {
            return listArray.size();
        }
    }

}


