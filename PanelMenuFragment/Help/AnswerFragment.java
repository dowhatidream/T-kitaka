package com.example.tkitaka_fb.PanelMenuFragment.Help;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tkitaka_fb.Model.Answer;
import com.example.tkitaka_fb.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class AnswerFragment extends DialogFragment {

    public static AnswerFragment newInstance(){
        return new AnswerFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setStyle( DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle );

    }

    FirebaseAuth auth;
    FirebaseUser user;

    AlertDialog dialog;

    EditText etContent;
    Button btnConfirm, btnCancel;

    String content, cDate;

    QuestionTwoFragment questionTwoFragment;

    private DatabaseReference Database, reference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate( R.layout.fragment_answer, container, false );

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        Database = FirebaseDatabase.getInstance().getReference();

        etContent = view.findViewById( R.id.etContent );

        btnCancel = view.findViewById( R.id.btnCancel );
        btnConfirm = view.findViewById( R.id.btnConfirm );

        btnCancel.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder( getActivity() );
                builder.setMessage( "답변 작성을 취소하시겠습니까?" )
                        .setPositiveButton( "확인",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        FragmentManager fragmentManager = getFragmentManager();
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                        fragmentTransaction.replace( R.id.frameLayout, questionTwoFragment );
                                        fragmentTransaction.commit();
                                    }
                                }
                        ).setNegativeButton( "취소", null );
            }
        } );

        btnConfirm.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (TextUtils.isEmpty( etContent.getText().toString() )) {
                        AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
                        dialog = builder.setMessage( "내용을 입력해주세요." )
                                .setPositiveButton( "확인", null )
                                .create();
                        dialog.show();
                        return;
                    } else {
                        createAnswer();
                        dismiss();
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        } );

        return view;
    }

    public void createAnswer(){
        content = etContent.getText().toString();

        //생성일
        TimeZone time;
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

        time = TimeZone.getTimeZone("Asia/Seoul");
        dateFormat.setTimeZone(time);
        cDate = dateFormat.format(date);

        String userID = auth.getUid();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference = database.getReference("Answer");
        String answerID = reference.push().getKey();

        Bundle extra = this.getArguments();
        final String questionID = extra.getString( "QuestionID" );

        try{
            Answer answer = new Answer(  );
            answer.setAnswerID( answerID );
            answer.setContent( content );
            answer.setcDate( cDate );
            answer.setQuestionID( questionID );
            answer.setUserID( userID );

            reference.child( userID ).child( questionID ).child( answerID ).setValue( answer );

            Toast.makeText(getContext(), "답변이 등록되었습니다", Toast.LENGTH_SHORT).show();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }
}
