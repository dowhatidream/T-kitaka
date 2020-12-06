package com.example.tkitaka_fb.MainMenuFragment.Board;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.tkitaka_fb.Model.Comment;
import com.example.tkitaka_fb.Model.User;
import com.example.tkitaka_fb.PanelMenuFragment.Help.QuestionFragment;
import com.example.tkitaka_fb.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailPostFragment extends Fragment {

    public DetailPostFragment() {
        super();
    }

    public static DetailPostFragment newInstance() {
        DetailPostFragment fragment = new DetailPostFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private DatabaseReference reference, postRef, CommentRef, UserRef, allPostRef;

    //파이어베이스 유저
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseDatabase database;

    String category, postID, title, content, cContent, userName, userID, postImage, userProfilePic;

    //레이아웃 변수
    TextView tvCategory, tvTitle, tvDate, tvName, tvContent, tvCount;

    AlertDialog dialog;
    Button btnNewComment;
    EditText etComment;

    //이미지
    CircleImageView civPostProfilePic;
    ImageView ivPic, ivCPic;
    ImageButton btnPic, ibMore;
    int PICK_IMAGE = 1011;
    RecyclerView rvComment;

    DetailPostFragment.CommentAdapter commentAdapter;
    List<Comment> list;

    public RequestManager mGlideRequestManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_post, container, false);

        // 현재 유저 불러오기
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        userID = user.getUid();
        UserRef = database.getReference().child("User");

        // 유저 프로필 찾기
        database = FirebaseDatabase.getInstance();
        UserRef = database.getReference().child("User");

        UserRef.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue( User.class );
                    userName = user.getUserName();
                    if (user.getUserProfile().equals("default")){
                        userProfilePic = "default";
                    }
                    else {
                        userProfilePic = user.getUserProfile();
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

        //번들로 받아온 값 레이아웃에 전달 메소드
        initInstances(view);

        //데이터 메소드
        getFirebaseData();

        //어댑터
        rvComment = (RecyclerView) view.findViewById(R.id.rvComment);
        rvComment.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvComment.setLayoutManager(linearLayoutManager);

        list = new ArrayList<>();
        commentAdapter = new CommentAdapter(getContext(), list);

        //번들로 받아온 게시글ID
        Bundle extra = this.getArguments();
        final String postID = extra.getString("PostID");

        //어뎁터 클릭 리스너
        commentAdapter.setOnItemClickListener(new CommentAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(View view, int position) {
                try {

                    Comment pComment = list.get(position);

                    CommentRef = database.getReference().child("Comment").child(postID);
                    CommentRef.child(pComment.getCommentID()).removeValue();

                    Toast.makeText(getContext(), "삭제 완료", Toast.LENGTH_SHORT).show();
                    list.remove(position);
                    rvComment.removeViewAt(position);
                    commentAdapter.notifyItemRemoved(position);
                    commentAdapter.notifyItemRangeChanged(position, list.size());
                    commentAdapter.notifyDataSetChanged();

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "뭔가 이상해요ㅠ", Toast.LENGTH_SHORT).show();
                } finally {
                }
            }

            @Override
            public void onReportClick(View view, int position) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new QuestionFragment()).commit();
            }
        });

        //이미지
        btnPic = view.findViewById(R.id.btnPic);
        btnPic.setVisibility(View.GONE);
        btnPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_IMAGE);
            }
        });

        //댓글 작성 및 확인 버튼
        btnNewComment = view.findViewById(R.id.btnNewComment);
        etComment = view.findViewById(R.id.etComment);

        btnNewComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // 기재 누락 여부
                    if (TextUtils.isEmpty(etComment.getText().toString())) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        dialog = builder.setMessage("댓글을 입력해주세요.")
                                .setPositiveButton("확인", null)
                                .create();
                        dialog.show();
                        return;
                    } else {
                        createComment();    //댓글 DB 작성
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return view;
    }

    private void initInstances(View view) {

        tvCategory = (TextView) view.findViewById(R.id.tvCategory);
        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvDate = (TextView) view.findViewById(R.id.tvDate);
        tvContent = (TextView) view.findViewById(R.id.tvContent);
        tvName = (TextView) view.findViewById(R.id.tvName);
        tvCount = (TextView) view.findViewById(R.id.tvCount);
        ivPic = (ImageView) view.findViewById(R.id.ivPic);
        ivPic.setVisibility(View.GONE);
        civPostProfilePic = (CircleImageView) view.findViewById(R.id.civPostProfilePic);


        Bundle extra = this.getArguments();

        postID = extra.getString("PostID");

        category = "[" + extra.getString("Category") + "]";
        tvDate.setText(extra.getString("Date"));
        tvTitle.setText(extra.getString("Title"));
        tvCategory.setText(category);
        tvContent.setText(extra.getString("Content"));
        tvName.setText(extra.getString("UserName"));
        tvCount.setText(extra.getString("PostCount"));
        final String pUserID = extra.getString("UserID");
        postImage = extra.getString("PostImage");

        //게시글 이미지
        if (postImage.equals("default")){
            ivPic.setColorFilter(null);
        }
        else {
            ivPic.setVisibility(View.VISIBLE);
            mGlideRequestManager = Glide.with(getContext());
            mGlideRequestManager.load(postImage).into(ivPic);
        }

        //게시글 작성자 프로필 이미지
        if (extra.getString("UserProfilePic").equals("default")){
            civPostProfilePic.setImageResource(R.drawable.ic_user);
        }
        else {
            mGlideRequestManager = Glide.with(getContext());
            mGlideRequestManager.load(extra.getString("UserProfilePic")).into(civPostProfilePic);
        }

        //게시글 더보기 버튼
        ibMore = (ImageButton) view.findViewById(R.id.ibMore);
        ibMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                //작성자 가져오기
                if ((userID).equals(pUserID)) {
                    PopupMenu popup = new PopupMenu(getContext(), ibMore);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.update:
                                    updatePostData();
                                    return true;
                                case R.id.delete:
                                    deletePostData();
                                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fgBoard, new BoardListFragment()).commit();
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                    popup.inflate(R.menu.ib_more_menu);
                    popup.show();
                } else {
                    PopupMenu popup2 = new PopupMenu(getContext(), ibMore);
                    popup2.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.report:
                                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new QuestionFragment()).commit();
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                    popup2.inflate(R.menu.ib_more_menu2);
                    popup2.show();
                }
            }
        });
    }

    //게시글 수정
    private void updatePostData() {
        title = tvTitle.getText().toString();
        category = tvCategory.getText().toString();
        content = tvContent.getText().toString();

        UpdatePostFragment dialog = new UpdatePostFragment();

        Bundle bundle = new Bundle();
        bundle.putString("PostID", postID);
        bundle.putString("Title", title);
        bundle.putString("Content", content);
        bundle.putString("PostImage", postImage);

        dialog.setArguments(bundle);
        dialog.show(getFragmentManager(), "fragmentDialog");
    }

    //게시글 삭제
    private void deletePostData() {
        allPostRef = database.getReference().child("MyPost").child(userID);
        postRef = database.getReference().child("Post");
        postRef.child(postID).removeValue();
        allPostRef.child(postID).removeValue();

        CommentRef = database.getReference().child("Comment").child(postID);
        CommentRef.removeValue();

        Toast.makeText(getContext(), "삭제 완료", Toast.LENGTH_SHORT).show();
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new BoardFragment()).commit();
    }

    //데이터 메소드
    void getFirebaseData() {
        Bundle extra = this.getArguments();
        final String postID = extra.getString("PostID");

        CommentRef = database.getReference("Comment").child(postID);
        CommentRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Comment planning = dataSnapshot.getValue(Comment.class);
                list.add(planning);
                rvComment.setAdapter(commentAdapter);
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

    //댓글 어댑터
    public static class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
        public interface OnItemClickListener {
            void onDeleteClick(View view, int position);
            void onReportClick(View view, int position);
        }

        private Context context;
        private OnItemClickListener mListener = null;

        public void setOnItemClickListener(OnItemClickListener listener) {
            this.mListener = listener;
        }

        public CommentAdapter(OnItemClickListener listener) {
            this.mListener = listener;
        }

        List<Comment> listArray;

        public CommentAdapter(Context context, List<Comment> list) {
            this.listArray = list;
            this.context = context;
        }

        String tvComment;

        public RequestManager mGlideRequestManager;

        @NonNull
        @Override
        public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_comment, viewGroup, false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull final CommentAdapter.ViewHolder holder, int position) {
            final Comment comment = listArray.get(position);
            holder.tvDate.setText(comment.getDate());
            holder.etUComment.setText(comment.getContent());
            holder.etUComment.setEnabled(false);
            holder.tvName.setText(comment.getUserName());

            final ImageButton button = holder.ibMore;

            final FirebaseAuth auth = FirebaseAuth.getInstance();
            final FirebaseUser user = auth.getCurrentUser();

            //더보기 버튼
            holder.ibMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    String commentUserID = comment.getUserID();
                    String CurrentUserID = user.getUid();
                    if (commentUserID.equals(CurrentUserID)) {
                        final int p = holder.getAdapterPosition();
                        PopupMenu popup = new PopupMenu(context, button);
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.update:
                                        holder.etUComment.setEnabled(true);
                                        holder.etUComment.setBackground(ContextCompat.getDrawable(context, R.drawable.edittext));
                                        holder.etUComment.setPadding(10,10,10,10);
                                        holder.btnCancel.setVisibility(View.VISIBLE);
                                        holder.btnUComment.setVisibility(View.VISIBLE);
                                        return true;
                                    case R.id.delete:
                                        if (p != RecyclerView.NO_POSITION) {
                                            if (mListener != null) {
                                                mListener.onDeleteClick(view, p);
                                                notifyItemChanged(p);
                                            }
                                        }
                                        return true;
                                    default:
                                        return false;
                                }
                            }
                        });
                        popup.inflate(R.menu.ib_more_menu3);
                        popup.show();
                    } else {
                        final int p2 = holder.getAdapterPosition();
                        PopupMenu popup2 = new PopupMenu(context, button);
                        popup2.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch (item.getItemId()) {
                                    case R.id.report:
                                        if (p2 != RecyclerView.NO_POSITION) {
                                            if (mListener != null) {
                                                mListener.onReportClick(view, p2);
                                                notifyItemChanged(p2);
                                            }
                                        }
                                        return true;
                                    default:
                                        return false;
                                }
                            }
                        });
                        popup2.inflate(R.menu.ib_more_menu2);
                        popup2.show();
                    }
                }
            });

            //수정 취소 버튼
            holder.btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    holder.etUComment.setEnabled(false);
                    holder.etUComment.setBackground(null);
                    holder.btnCancel.setVisibility(View.GONE);
                    holder.btnUComment.setVisibility(View.GONE);
                }
            });

            //수정된 텍스트 저장하고 원래 뷰로 돌아감
            holder.btnUComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    final int p = holder.getAdapterPosition();
                    if (p != RecyclerView.NO_POSITION) {
                        if (mListener != null) {
                            tvComment = holder.etUComment.getText().toString();
                            holder.etUComment.setText(tvComment);
                            holder.etUComment.setBackground(null);
                            holder.etUComment.setEnabled(false);
                            holder.btnCancel.setVisibility(View.GONE);
                            holder.btnUComment.setVisibility(View.GONE);
                            //댓글 수정 메소드
                            updateComment(p);
                        }
                    }
                }
            });

            //댓글 작성자 프로필 이미지
            if (comment.getUserProfilePic().equals("default")){
                holder.civCommentProfilePic.setImageResource(R.drawable.ic_user);
            }
            else {
                mGlideRequestManager = Glide.with(context);
                mGlideRequestManager.load(comment.getUserProfilePic()).into(holder.civCommentProfilePic);
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView tvDate;
            public EditText etUComment;
            public TextView tvName;
            public ImageView ivpic;
            public CircleImageView civCommentProfilePic;
            ImageButton ibMore;
            public Button btnUComment, btnCancel;
            public RelativeLayout viewForeground;

            public ViewHolder(View itemView) {
                super(itemView);

                tvDate = itemView.findViewById(R.id.tvDate);
                etUComment = itemView.findViewById(R.id.etUComment);
                tvName = itemView.findViewById(R.id.tvUser);
                ivpic = itemView.findViewById(R.id.ivPic);
                viewForeground = itemView.findViewById(R.id.view_foreground);
                ibMore = itemView.findViewById(R.id.ibMore);
                btnUComment = itemView.findViewById(R.id.btnAdd);
                btnCancel = itemView.findViewById(R.id.btnCancel);
                etUComment.setEnabled(false);
                btnCancel.setVisibility(View.GONE);
                btnUComment.setVisibility(View.GONE);
                civCommentProfilePic = itemView.findViewById(R.id.civCommentProfilePic);
            }
        }

        @Override
        public int getItemCount() {
            return listArray.size();
        }

        //댓글 수정 메소드
        public void updateComment(final int p){
            Comment pComment = listArray.get(p);

            // 현재시간 저장
            TimeZone time;
            Date date = new Date();
            DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
            time = TimeZone.getTimeZone("Asia/Seoul");
            dateFormat.setTimeZone(time);
            String cdate = dateFormat.format(date);

            Comment uComment = new Comment();
            uComment.setCommentID(pComment.getCommentID()); // 위에서 설정한 고유 노드를 아예 기본키 컬럼으로 가져오기
            uComment.setUserName(pComment.getUserName());
            uComment.setPostID(pComment.getPostID());
            uComment.setContent(tvComment);
            uComment.setDate(cdate);
            uComment.setUserID(pComment.getUserID());
            uComment.setUserProfilePic(pComment.getUserProfilePic());

            //레퍼런스 child 경로 중 해당 댓글 아이디 경로의 값 수정
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference UCommentRef = database.getReference("Comment").child(pComment.getPostID());
            UCommentRef.child(pComment.getCommentID()).setValue(uComment);

            Toast.makeText(context, "댓글이 정상적으로 수정 되었습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    //댓글 작성 메소드
    private void createComment() {
        // 현재시간 저장
        TimeZone time;
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        time = TimeZone.getTimeZone("Asia/Seoul");
        dateFormat.setTimeZone(time);
        String cdate = dateFormat.format(date);

        //작성 String
        cContent = etComment.getText().toString();

        String userID = auth.getUid(); // 현재 userID 가져오기

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference = database.getReference("Comment");
        String commentID = reference.push().getKey(); //기본키이자 고유 노드

        try {
            Comment comment = new Comment();
            comment.setCommentID(commentID); // 위에서 설정한 고유 노드를 아예 기본키 컬럼으로 가져오기
            comment.setUserName(userName);
            comment.setPostID(postID);
            comment.setContent(cContent);
            comment.setDate(cdate);
            comment.setUserID(userID);
            comment.setUserProfilePic(userProfilePic);

            reference.child(postID).child(commentID).setValue(comment);

            commentAdapter.notifyDataSetChanged();
            Toast.makeText(getActivity(), "댓글이 정상적으로 업로드 되었습니다.", Toast.LENGTH_SHORT).show();
            etComment.setText(null);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "뭔가 이상해요", Toast.LENGTH_SHORT).show();
        }
    }
}
