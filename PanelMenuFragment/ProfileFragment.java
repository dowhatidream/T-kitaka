package com.example.tkitaka_fb.PanelMenuFragment; // 이승연이 추가함 1011

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.tkitaka_fb.LoginRegister.LoginActivity;
import com.example.tkitaka_fb.MainMenuFragment.Trip.TripFragment;
import com.example.tkitaka_fb.Model.User;
import com.example.tkitaka_fb.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static android.support.constraint.Constraints.TAG;

//import com.example.tkitaka.Database.DBHelper;
//import com.example.tkitaka.LoginRegister.LoginActivity;
//import com.example.tkitaka.MainMenuFragment.MainActivity;
//import com.example.tkitaka.MainMenuFragment.Trip.TripFragment;
//import com.example.tkitaka.Model.Person;

public class ProfileFragment extends Fragment implements View.OnClickListener{
    private TripFragment tripFragment = new TripFragment();
    private UpdatePBMSFragment updatePBMSFragment = new UpdatePBMSFragment();

    TextView tvName;
    TextView tvBirth;
    TextView tvWithdraw;
    TextView tvPbms;
    TextView tvEmail;
    EditText etPass;
    EditText etnPass;
    EditText etnPass2;
    EditText etPhone;
    EditText etNumber;
    EditText etInt;

    FirebaseUser firebaseUser;
    FirebaseAuth auth;

    public RequestManager mGlideRequestManager;

    private ChildEventListener mChild;
    private DatabaseReference mReference, UserRef;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth.AuthStateListener authListener;

    static ArrayList<String> arrayData = new ArrayList<String>();

    Button btnCancel;
    Button btnChange;
    Button btnAuth;

    CircleImageView civProfilePic;
    CircleImageView civProfileSelect;
    String currentUser;

    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;
    private StorageReference storageReference;

    private String verificationId;
    // 비밀번호 정규식
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{8,16}$");
    // 전화번호 정규식
    private static final Pattern PHONE_PATTERN = Pattern.compile("^01(?:0|1|[6-9])(\\d{3}|\\d{4})(\\d{4})$");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_profile, container, false );

        mDatabase = FirebaseDatabase.getInstance();

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        currentUser = firebaseUser.getUid();

        mGlideRequestManager = Glide.with(this);

        tvName = (TextView) view.findViewById( R.id.tvName );
        tvBirth = (TextView) view.findViewById( R.id.tvBirth );
        tvEmail = (TextView) view.findViewById( R.id.tvEmail );
        etPhone = (EditText) view.findViewById( R.id.etPhone );
        etNumber = (EditText) view.findViewById( R.id.etNumber );
        etInt = (EditText) view.findViewById( R.id.etInt );
        tvWithdraw = (TextView) view.findViewById( R.id.tvWithdraw );
        tvWithdraw.setOnClickListener( this );
        tvPbms = (TextView) view.findViewById( R.id.tvPbms );
        tvPbms.setOnClickListener( this );
        // 유저 비밀번호
        etPass = (EditText) view.findViewById( R.id.etPass );
        etnPass = (EditText) view.findViewById( R.id.etnPass );
        etnPass2 = (EditText) view.findViewById( R.id.etnPass2 );
        // 취소 버튼
        btnCancel = (Button) view.findViewById( R.id.btnCancel );
        btnCancel.setOnClickListener( this );
        // 저장 버튼
        btnChange = (Button) view.findViewById( R.id.btnChange );
        btnChange.setOnClickListener( this );
        // 인증 버튼
        btnAuth = (Button) view.findViewById( R.id.btnAuth );
        btnAuth.setOnClickListener( this );

        // 이승연
        civProfilePic = (CircleImageView) view.findViewById(R.id.civProfilePic);
        civProfileSelect = (CircleImageView) view.findViewById(R.id.civProfileSelect);
        storageReference = FirebaseStorage.getInstance().getReference().child("Profile");


        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser == null){
                    Intent intent = new Intent( getActivity(), LoginActivity.class );
                    startActivity( intent );
                }
            }
        };

        civProfileSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });

        mReference = mDatabase.getReference( "User" );
        mReference.child(currentUser).addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue( User.class );
                tvName.setText( user.getUserName() );
                tvEmail.setText( user.getUserEmail() );
                tvBirth.setText( "(" + user.getUserBirth() + ")" );
                etPhone.setText( user.getUserPhone() );
                etInt.setText( user.getUserIntroduction() );

                if (user.getUserProfile().equals("default")){
                    civProfilePic.setImageResource(R.mipmap.ic_launcher);
                }
                else {
                    mGlideRequestManager.load(user.getUserProfile()).into(civProfilePic);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );

        return view;
    }

    private void openImage() {
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

    private void uploadImage(){
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("업로드 중");
        progressDialog.show();

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
                        String uri = downloadUri.toString();

                        UserRef = mDatabase.getReference().child("User").child(currentUser);
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("userProfile", uri);
                        UserRef.updateChildren(hashMap);

                        progressDialog.dismiss();
                    }
                    else {
                        Toast.makeText(getContext(), "업로드 실패", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        }
        else {
            Toast.makeText(getContext(), "사진이 선택되지 않았습니다!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==IMAGE_REQUEST && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imageUri = data.getData();

            if (uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(getContext(), "업로드 중입니다...", Toast.LENGTH_SHORT).show();
            }
            else {
                uploadImage();
                Toast.makeText(getContext(), "업로드가 완료되었습니다.", Toast.LENGTH_SHORT).show();

            }
        }
    }

    public void onClick(View view){
        // '탈퇴하기' 버튼 클릭 시
        if (view == tvWithdraw){
            AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
            builder.setMessage("정말로 탈퇴하실거에요?")
                    .setPositiveButton( "탈퇴하기", new DialogInterface.OnClickListener() {
                        // '탈퇴하기'를 선택한다면?
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                user.delete().addOnCompleteListener( new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            String uId = firebaseUser.getUid();
                                            Log.d( TAG, "Uid" + uId );
                                            Map<String, Object> map = new HashMap<>(  );
                                            map.put( "userID", null );
                                            mReference.updateChildren( map );
                                        }
                                        Toast.makeText(getActivity(), "탈퇴되었습니다.", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent( getActivity(), LoginActivity.class );
                                        startActivity( intent );
                                    }
                                } );
                            } catch (Exception e){
                                e.printStackTrace();
                                Toast.makeText(getActivity(), "오류난거임", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent( getActivity(), LoginActivity.class );
                                startActivity( intent );
                            }
                        }
                    } )
                    .setNegativeButton( "돌아가기", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    } );
            AlertDialog dialog=builder.create();
            dialog.show();
        }
        //'나의 여행취향(PBMS) 변경' 클릭 시
        else if (view == tvPbms){
            UpdatePBMSFragment dialog = UpdatePBMSFragment.newInstance();

            dialog.show( getFragmentManager(), "fragmentDialog" );
        }
        // '취소' 버튼 클릭 시
        else if (view == btnCancel){
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace( R.id.frameLayout, tripFragment );
            fragmentTransaction.commit();
        }

        // '인증' 버튼 클릭 시
        else if (view == btnAuth){
            String userPhone = "+82" + etPhone.getText().toString().trim();
            try{
                if (userPhone.isEmpty()){
                    etPhone.setError("휴대폰 번호를 입력해주세요");
                    etPhone.requestFocus();
                    return;
                }
                sendPhoneVerification(userPhone); // 문자로 인증번호 전송
                etPhone.setEnabled(false);
                Toast.makeText(getActivity(), "인증 번호를 전송했습니다", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "뭔가 이상해요", Toast.LENGTH_SHORT).show();
            }
        }
        // '저장'버튼 클릭 시
        else if (view == btnChange){
            final String pre_pPw = etPass.getText().toString(); // 현재 비밀번호
            final String pPw = etnPass.getText().toString(); // 새 비밀번호
            final String pPwAgain = etnPass2.getText().toString();

            if( pre_pPw.isEmpty() ){
                Toast.makeText( getActivity(), "현재 비밀번호는 필수 입력 항목입니다", Toast.LENGTH_SHORT ).show();
            }
            else {
                final String userEmail = firebaseUser.getEmail();
                auth.signInWithEmailAndPassword( userEmail, pre_pPw ).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (pPw.trim().length() < 8) {
                                etnPass.setError( "비밀번호는 최소 8자리 이상이어야 합니다" );
                                etnPass.requestFocus();
                                return;
                            } else if (!pPw.matches( pPwAgain )) {
                                etnPass2.setError( "비밀번호가 일치하지 않습니다" );
                                etnPass2.requestFocus();
                                return;
                            } else if (TextUtils.isEmpty( pre_pPw )) {
                                Toast.makeText( getActivity(), "현재 비밀번호는 필수 입력 항목입니다", Toast.LENGTH_SHORT ).show();
                            } else {
                                try {

                                    if (!TextUtils.isEmpty( pPw )){
                                        firebaseUser.updatePassword( etnPass.getText().toString().trim() ).addOnCompleteListener( new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        });
                                    }
                                    createInt(); // 자소, 폰번호 변경
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                Toast.makeText( getActivity(), "정보가 변경되었습니다.", Toast.LENGTH_SHORT ).show();
                            }
                        } else {
                            Toast.makeText( getActivity(), "현재 비밀번호를 다시 입력해주세요", Toast.LENGTH_SHORT ).show();
                            etPass.requestFocus();
                        }
                    }
                } );
            }
        }
    }

    private AdapterView.OnItemClickListener onClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String[] temp = arrayData.get( position ).split( "\\s+" );
        }
    };

    private void createInt(){
        final String intro = etInt.getText().toString();
        final String phone = etPhone.getText().toString();

        final DatabaseReference userRef = mDatabase.getReference().child( "User" );
        userRef.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put( "userIntroduction", intro);
                    hashMap.put( "userPhone", phone);
                    userRef.child( currentUser ).updateChildren( hashMap );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        } );
    }

    // 폰번호 인증
    public void sendPhoneVerification(String userPhone) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(userPhone, 60, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD, mCallBack);
    }
    // 폰번호 인증2
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null){
                verifyCode(code);
                Toast.makeText( getActivity(), "인증 완료", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(getActivity(), "Failed to send verification number.", Toast.LENGTH_SHORT).show();
        }
    };

    private void verifyCode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
    }
}