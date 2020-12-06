package com.example.tkitaka_fb.MainMenuFragment.Board; // 정다연

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.tkitaka_fb.Model.Post;
import com.example.tkitaka_fb.Model.User;
import com.example.tkitaka_fb.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static android.app.Activity.RESULT_OK;

public class AddPostFragment extends DialogFragment {

    private static final String TAG = "게시판";

    FirebaseAuth auth;
    FirebaseUser user;

    DatabaseReference reference, UserRef, reference2;

    FirebaseDatabase database, mDatabase;

    AlertDialog dialog;

    public static AddPostFragment newInstance() {
        return new AddPostFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }
    //레이아웃 변수
    EditText etTitle;
    EditText etContent;
    Spinner spnrPost;
    ImageView ivPic;
    ImageButton btnPic;
    ImageButton btnBack;
    Button btnPost;

    String title, category, content, cdate, postID;
    String userName, userID, userProfilePic;

    //이미지
    private Uri imageUri;
    private StorageTask uploadTask;

    StorageReference  storageReference;
    private static final int IMAGE_REQUEST = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_add_post, container, false);

        // 현재 유저 불러오기
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userID = user.getUid(); // 현재 userID 가져오기

        mDatabase = FirebaseDatabase.getInstance();

        //작성 내용 변수에 저장
        etTitle = view.findViewById(R.id.etTitle);
        etContent = view.findViewById(R.id.etContent);
        spnrPost = view.findViewById(R.id.spnrPost);

        //이미지
        storageReference = FirebaseStorage.getInstance().getReference().child("Board");
        ivPic = view.findViewById(R.id.ivPic);
        ivPic.setVisibility(View.GONE);
        btnPic = view.findViewById(R.id.btnPic);
        btnPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });

        //글 확인 및 취소
        btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btnPost = view.findViewById(R.id.btnAdd);
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // 기재 누락 여부
                    if (TextUtils.isEmpty(etTitle.getText().toString()) || TextUtils.isEmpty(etContent.getText().toString())) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        dialog = builder.setMessage("제목을 입력해주세요.")
                                .setPositiveButton("확인", null)
                                .create();
                        dialog.show();
                        return;
                    } else {
                        createPost();   //게시글 생성 메소드
                        dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

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
        return view;
    }

    //게시글 저장
    public void createPost() {
        // 현재시간 저장
        TimeZone time;
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");

        time = TimeZone.getTimeZone("Asia/Seoul");
        dateFormat.setTimeZone(time);
        cdate = dateFormat.format(date);

        //텍스트뷰 저장
        title = etTitle.getText().toString();
        category = spnrPost.getSelectedItem().toString();
        content = etContent.getText().toString();

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Post");
        reference2 = database.getReference("MyPost");

        postID = reference.push().getKey(); //기본키이자 고유 노드

        //게시글 생성
        uploadPost();

        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fgBoard, new BoardListFragment()).commit();
        Toast.makeText(getActivity(), "글이 정상적으로 업로드 되었습니다.", Toast.LENGTH_SHORT).show();

        dismiss();
    }

    //이미지 불러오기
    private void openImage () {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==IMAGE_REQUEST && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imageUri = data.getData();

            if (uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(getContext(), "사진 업로드 중...", Toast.LENGTH_SHORT).show();
            }
            else {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                    ivPic.setVisibility(View.VISIBLE);
                    ivPic.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //게시글 생성 메소드
    private void uploadPost(){
        final String d = "default";
        if (imageUri != null){
            final StorageReference fileRef = storageReference.child(System.currentTimeMillis()+"."+ getFileExtension(imageUri));
            uploadTask = fileRef.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String postImage = downloadUri.toString();
                        Log.d(TAG, "이미지주소    "+postImage);
                        try {
                            if(!(postImage).equals(d)){ // 사진이 있으면
                                Post post = new Post();
                                post.setPostID(postID); // 위에서 설정한 고유 노드를 아예 기본키 컬럼으로 가져오기
                                post.setUserName(userName);
                                post.setCategory(category);
                                post.setContent(content);
                                post.setTitle(title);
                                post.setDate(cdate);
                                post.setUserID(userID);
                                post.setUserProfilePic(userProfilePic);
                                post.setPostImage(postImage);
                                post.setPostCount(0);

                                reference2.child(userID).child(postID).setValue(post);
                                reference.child(postID).setValue(post);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "뭔가 이상해요", Toast.LENGTH_SHORT).show();
                        } finally {
                        }
                    }else {
                        Toast.makeText(getContext(), "업로드 실패", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else if (imageUri == null){
            Post post = new Post();
            post.setPostID(postID); // 위에서 설정한 고유 노드를 아예 기본키 컬럼으로 가져오기
            post.setUserName(userName);
            post.setCategory(category);
            post.setContent(content);
            post.setTitle(title);
            post.setDate(cdate);
            post.setUserID(userID);
            post.setUserProfilePic(userProfilePic);
            post.setPostImage(d);
            post.setPostCount(0);

            reference2.child(userID).child(postID).setValue(post);
            reference.child(postID).setValue(post);
        }
        else {
            Toast.makeText(getContext(), "사진이 선택되지 않았습니다!", Toast.LENGTH_SHORT).show();
        }
    }

    //다이얼로그 크기
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