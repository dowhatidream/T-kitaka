package com.example.tkitaka_fb.PanelMenuFragment.Help;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.tkitaka_fb.Model.Question;
import com.example.tkitaka_fb.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class QuestionOneFragment extends Fragment implements View.OnClickListener {

    EditText etTitle;
    EditText etContent;

    Button btnCancel;
    Button btnConfirm;

    Spinner spnrQue;

    String title;
    String content;
    String cDate;
    String category;

    private AlertDialog dialog;

    private HelpFragment helpFragment = new HelpFragment();

    ArrayAdapter<CharSequence> adapterQuestion;

    FirebaseAuth auth;
    FirebaseUser user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_question_one, container, false );

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        etTitle = (EditText) view.findViewById( R.id.etTitle );
        etContent = (EditText) view.findViewById( R.id.etContent );
        spnrQue = (Spinner) view.findViewById( R.id.spnrQue );
        btnCancel = (Button) view.findViewById( R.id.btnCancel );
        btnCancel.setOnClickListener( this );
        btnConfirm = (Button) view.findViewById( R.id.btnConfirm );
        btnConfirm.setOnClickListener( this );

        adapterQuestion = ArrayAdapter.createFromResource( getContext(), R.array.question, android.R.layout.simple_spinner_dropdown_item);
        spnrQue.setAdapter( adapterQuestion );

        return view;
    }

    @Override
    public void onClick(View v) {
        title = etTitle.getText().toString();
        content = etContent.getText().toString();
        category = spnrQue.getSelectedItem().toString();

        if(v == btnCancel){
            AlertDialog.Builder builder = new AlertDialog.Builder( getActivity() );
            builder.setMessage( "문의를 취소하시겠습니까?" )
                    .setPositiveButton( "확인",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FragmentManager fragmentManager = getFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.replace( R.id.frameLayout, helpFragment );
                                    fragmentTransaction.commit();
                                }
                            }
                    ).setNegativeButton( "취소", null );
        }
        else if (v == btnConfirm){

            if(title.isEmpty()) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder( getActivity() );
                builder.setMessage( "제목을 입력해주세요." )
                        .setPositiveButton( "확인", null )
                        .create();
                dialog.show();
            } else if (content.isEmpty()) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder( getActivity() );
                builder.setMessage( "내용을 입력해주세요." )
                        .setPositiveButton( "확인", null )
                        .create();
                dialog.show();
            } else {
                createQue();
                QuestionFragment.getInstance().jumpToPage();
            }
        }
    }

    private void createQue(){
        TimeZone time;
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        time = TimeZone.getTimeZone("Asia/Seoul");
        dateFormat.setTimeZone(time);
        cDate = dateFormat.format(date);
        title = etTitle.getText().toString();
        content = etContent.getText().toString();
        category = spnrQue.getSelectedItem().toString();

        DatabaseReference reference;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference = database.getReference("Question");
        String userID = auth.getUid();

        String questionID = reference.push().getKey();

        try{
            Question question = new Question();
            question.setQuestionID( questionID );
            question.setCategory( category );
            question.setcDate( cDate );
            question.setTitle( title );
            question.setContent( content );
            question.setUserID( userID );
            question.setAnswer( "답변 예정" );

            reference.child( userID ).child( questionID ).setValue( question );
            reference.child(category).child( questionID ).setValue(question);
            Toast.makeText( getActivity(), "문의가 등록되었습니다", Toast.LENGTH_SHORT ).show();

            etTitle.setText( null );
            etContent.setText( null );
            spnrQue.setSelection(0);

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
