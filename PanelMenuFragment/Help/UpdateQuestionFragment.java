package com.example.tkitaka_fb.PanelMenuFragment.Help;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
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
import com.google.android.gms.common.data.DataBufferSafeParcelable;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class UpdateQuestionFragment extends DialogFragment implements View.OnClickListener {

    public void onCreate(Bundle savedInstanceState){
        super.onCreate( savedInstanceState );
        setStyle( DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle );
    }

    public static UpdateQuestionFragment newInstance(){
        return new UpdateQuestionFragment();
    }

    FirebaseAuth auth;
    FirebaseUser user;
    String title, content, cDate, category, userID;

    ArrayAdapter<CharSequence> adapterCategory;

    Spinner spnrQue;
    EditText etTitle, etContent;
    Button btnCancel, btnConfirm;

    String uCategory, uQuestionID, uTitle, ucDate, uContent, uAnswer;

    DatabaseReference reference, reference2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_question_update, container, false );

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        etTitle = view.findViewById( R.id.etTitle );
        etContent = view.findViewById( R.id.etContent );
        spnrQue = view.findViewById( R.id.spnrQue );
        btnCancel = view.findViewById( R.id.btnCancel );
        btnConfirm = view.findViewById( R.id.btnConfirm );

        if(getArguments() != null){
            Bundle bundle = getArguments();

            uQuestionID = bundle.getString( "QuestionID" );
            uTitle = bundle.getString( "Title" );
            uContent = bundle.getString( "Content" );
            ucDate = bundle.getString( "cDate" );
            uCategory = bundle.getString( "Category" );
            uAnswer = bundle.getString( "Answer" );
        }

        adapterCategory = ArrayAdapter.createFromResource( getContext(), R.array.question, android.R.layout.simple_spinner_item);
        spnrQue.setAdapter( adapterCategory );

        etTitle.setText( uTitle );
        etContent.setText( uContent );

        btnCancel.setOnClickListener( this );
        btnConfirm.setOnClickListener( this );

        return view;
    }

    public void updateQuestion(){
        title = etTitle.getText().toString();
        content = etContent.getText().toString();
        category = spnrQue.getSelectedItem().toString();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        userID = auth.getUid();
        reference = database.getReference("Question").child(userID);
        reference2 = database.getReference("Question").child( category );

        try {
            Question question = new Question(  );
            question.setQuestionID( uQuestionID );
            question.setTitle( title );
            question.setContent( content );
            question.setcDate( ucDate );
            question.setCategory( category );
            question.setUserID( userID );
            question.setAnswer( uAnswer );

            reference.child( uQuestionID ).setValue( question );
            reference2.child( uQuestionID ).setValue( question );

            getActivity().getSupportFragmentManager().beginTransaction().
                    replace(R.id.frameLayout, new QuestionTwoFragment()).commit();

            Toast.makeText( getContext(), "문의가 수정되었습니다", Toast.LENGTH_SHORT ).show();

            dismiss();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        title = etTitle.getText().toString();
        content = etContent.getText().toString();
        category = spnrQue.getSelectedItem().toString();

        if (v == btnCancel){
            dismissAllowingStateLoss();
        }
        if (v == btnConfirm){
            try {
                if (TextUtils.isEmpty(title)) {
                    Toast.makeText(getContext(), "제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty( content )) {
                    Toast.makeText(getContext(), "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    updateQuestion();
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
