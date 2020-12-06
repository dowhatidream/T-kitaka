package com.example.tkitaka_fb.PanelMenuFragment.Admin;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import com.example.tkitaka_fb.Model.AdminPost;
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
import static com.facebook.login.widget.ProfilePictureView.TAG;

public class AdAdminPostFragment extends Fragment {

    FirebaseAuth auth;
    FirebaseUser user;

    DatabaseReference reference, UserRef;

    FirebaseDatabase database, mDatabase;

    AlertDialog dialog;

    //레이아웃 변수
    EditText etTitle;
    EditText etContent;
    Spinner spnrAdPost;
    ImageView ivPic;
    ImageButton btnPic;
    Button btnPost;

    //이미지
    private Uri imageUri;
    private StorageTask uploadTask;

    StorageReference  storageReference;
    private static final int IMAGE_REQUEST = 1;

    String title, category, content, cdate, postID, userID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_ad_admin_post, container, false );

        // 현재 유저 불러오기
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userID = user.getUid(); // 현재 userID 가져오기

        //작성 내용 변수에 저장
        etTitle = view.findViewById(R.id.etTitle);
        etContent = view.findViewById(R.id.etContent);
        spnrAdPost = view.findViewById(R.id.spnrAdPost);

        //이미지
        storageReference = FirebaseStorage.getInstance().getReference().child("AdminPost");
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
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
        category = spnrAdPost.getSelectedItem().toString();
        content = etContent.getText().toString();

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("AdminPost");

        postID = reference.push().getKey(); //기본키이자 고유 노드

        //게시글 생성
        uploadPost();

        Toast.makeText(getActivity(), "글이 정상적으로 업로드 되었습니다.", Toast.LENGTH_SHORT).show();

        //레이아웃 텍스트 초기화
        etTitle.setText(null);
        etContent.setText(null);
        spnrAdPost.setSelection(0);
        ivPic.setImageBitmap(null);
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
                                    AdminPost adminPost = new AdminPost();
                                    adminPost.setAdPostID(postID); // 위에서 설정한 고유 노드를 아예 기본키 컬럼으로 가져오기
                                    adminPost.setCategory(category);
                                    adminPost.setContent(content);
                                    adminPost.setTitle(title);
                                    adminPost.setDate(cdate);
                                    adminPost.setUserID(userID);
                                    adminPost.setUserName("운영자");
                                    adminPost.setPostImage(postImage);

                                    reference.child(category).child(postID).setValue(adminPost);
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
            AdminPost adminPost = new AdminPost();
            adminPost.setAdPostID(postID); // 위에서 설정한 고유 노드를 아예 기본키 컬럼으로 가져오기
            adminPost.setCategory(category);
            adminPost.setContent(content);
            adminPost.setTitle(title);
            adminPost.setDate(cdate);
            adminPost.setUserID(userID);
            adminPost.setUserName("운영자");
            adminPost.setPostImage(d);

            reference.child(category).child(postID).setValue(adminPost);
        }
        else {
            Toast.makeText(getContext(), "사진이 선택되지 않았습니다!", Toast.LENGTH_SHORT).show();
        }
    }

}
