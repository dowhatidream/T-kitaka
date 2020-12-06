package com.example.tkitaka_fb.MainMenuFragment.Chat;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.example.tkitaka_fb.MainMenuFragment.MainActivity;
import com.example.tkitaka_fb.MemberProfileFragment;
import com.example.tkitaka_fb.Model.Chat;
import com.example.tkitaka_fb.Model.PBMS;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public class ChattingActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference reference;
    private String currentUser;
    private FirebaseDatabase database;

    TextView tvUserName;
    ImageButton btnSend;
    ImageView btnFile;
    EditText etMyMsg;

    Intent intent;
    private static final String TAG = "채팅액티";
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask<UploadTask.TaskSnapshot> uploadTask;
    private StorageReference storageReference;

    private String currentTime;

    ChattingAdapter chattingAdapter;
    List<Chat> ChatList;
    List<User> UserList;

    RecyclerView rv;

    String userID, messege, uri;
    String accommondation, meal, sTransportation, lTransportation, expense, preplan, spending, flight, guide, smoking, tmi;

    ValueEventListener seenListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        UserList = new ArrayList<>();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        currentUser = user.getUid();
        storageReference = FirebaseStorage.getInstance().getReference().child("Chat");

        tvUserName = (TextView) findViewById(R.id.tvUserName);
        btnSend = (ImageButton) findViewById(R.id.btnSend);
        etMyMsg = (EditText) findViewById(R.id.etMyMsg);
        btnFile = (ImageView) findViewById(R.id.btnFile);
        rv = (RecyclerView) findViewById(R.id.rv);

        rv.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        rv.setLayoutManager(linearLayoutManager);

        TimeZone time;
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm a");

        time = TimeZone.getTimeZone("Asia/Seoul");
        dateFormat.setTimeZone(time);
        currentTime = dateFormat.format(date);

        // 툴바
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChattingActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        // 어댑터에서 리사이클러뷰 아이템 값 가져옴(상대방 아이디)
        intent = getIntent();
        userID = intent.getStringExtra("userID");
        Log.d(TAG, "채팅받음   "+userID);

        reference = database.getReference().child("User").child(userID);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                tvUserName.setText(user.getUserName());

                readMessage(currentUser, userID, user.getUserProfile());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // 사진 첨부 버튼
        btnFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });

        // 메세지 전송
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messege = etMyMsg.getText().toString();

                if(TextUtils.isEmpty(messege)&&TextUtils.isEmpty(uri)){
                    Toast.makeText(ChattingActivity.this, "전송할 내용이 없어요T.T", Toast.LENGTH_SHORT).show();
                }
                else {
                    uploadImage(currentUser, userID, messege, currentTime); // 나, 상대방, 내용, 시각, 파일
                }
                etMyMsg.setText("");
            }
        });
        seenMessage(userID);
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
                dialog.show((ChattingActivity.this).getSupportFragmentManager(), "fragmentDialog");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // 메세지 읽음 여부
    private void seenMessage(final String userid){
        reference = database.getReference().child("Chat");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);

                    if (chat.getReceiverID().equals(currentUser) && chat.getSenderID().equals(userid)){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isSeen", true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // 메세지 읽기
    private void readMessage(final String myID, final String userID, final String imageURL){
        ChatList = new ArrayList<>();

        reference = database.getReference().child("Chat");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ChatList.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (chat.getReceiverID().equals(myID)&&chat.getSenderID().equals(userID) || chat.getReceiverID().equals(userID)&&chat.getSenderID().equals(myID)){
                        ChatList.add(chat);
                    }
                    chattingAdapter = new ChattingAdapter(ChattingActivity.this, ChatList, imageURL);
                    rv.setAdapter(chattingAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // 유저 접속상태
    private void status(String status){
        reference = FirebaseDatabase.getInstance().getReference("User").child(currentUser);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userStatus", status);

        reference.updateChildren(hashMap);
    }

    // 갤러리 열기
    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage(final String sender, final String receiver, final String messege, final String sTime){
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
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        uri = downloadUri.toString();

                        DatabaseReference reference = database.getReference();
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("senderID", sender);
                        hashMap.put("receiverID", receiver);
                        hashMap.put("messege", "");
                        hashMap.put("sTime", sTime);
                        hashMap.put("isSeen", false);
                        hashMap.put("file", uri);

                        reference.child("Chat").push().setValue(hashMap);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "업로드 실패", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        else if (imageUri == null){
            DatabaseReference reference = database.getReference();

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("senderID", sender);
            hashMap.put("receiverID", receiver);
            hashMap.put("messege", messege);
            hashMap.put("sTime", sTime);
            hashMap.put("isSeen", false);
            hashMap.put("file", "default");

            reference.child("Chat").push().setValue(hashMap);
        }

        else {
            Toast.makeText(getApplicationContext(), "사진이 선택되지 않았습니다!", Toast.LENGTH_SHORT).show();
        }

        // 챗 프래그먼트에 유저 추가(내가 메세지 보낸 유저)
        final DatabaseReference ChatRef = database.getReference().child("ChatList").child(currentUser).child(userID);
        ChatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    ChatRef.child("userID").setValue(userID);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // 챗 프래그먼트에 유저 추가(메세지 받은 유저)
        final DatabaseReference ChatRef2 = database.getReference().child("ChatList").child(userID).child(currentUser);
        ChatRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()){
                    ChatRef2.child("userID").setValue(currentUser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==IMAGE_REQUEST && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imageUri = data.getData();

            if (uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(getApplicationContext(), "업로드 중입니다...", Toast.LENGTH_SHORT).show();
            }
            else {
                uploadImage(currentUser, userID, messege, currentTime);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
        status("offline");
    }
}
