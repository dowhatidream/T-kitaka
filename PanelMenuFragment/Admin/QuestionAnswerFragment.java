package com.example.tkitaka_fb.PanelMenuFragment.Admin;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tkitaka_fb.Model.Question;
import com.example.tkitaka_fb.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class QuestionAnswerFragment extends DialogFragment implements View.OnClickListener {

    public static QuestionAnswerFragment newInstance() {
        QuestionAnswerFragment e = new QuestionAnswerFragment();
        return e;
    }

    public void onCreate(Bundle savedInstanceState){
        super.onCreate( savedInstanceState );
        setStyle( DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle );
    }

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference reference, QuestionRef;

    EditText etAnswer;
    Button btnCancel, btnConfirm;

    ArrayAdapter<CharSequence> adapterQuestion;

    String userID;

    String uQuestionID, uTitle, uContent, uCategory, uAnswer, ucDate, uUserID;
    String title, category, content, cDate;

    String answer;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        final View view = inflater.inflate( R.layout.fragment_question_answer, container, false );

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        userID = user.getUid();
        database = FirebaseDatabase.getInstance();

        QuestionRef = database.getReference("Question").child( userID ).child( "questionID" );

        etAnswer = (EditText) view.findViewById( R.id.etAnswer );
        btnCancel = (Button) view.findViewById( R.id.btnCancel );
        btnConfirm = (Button) view.findViewById( R.id.btnConfirm );

        if (getArguments() != null){
            Bundle bundle = getArguments();
            uQuestionID = bundle.getString( "uQuestionID" );
            uTitle = bundle.getString( "uTitle" );
            uContent = bundle.getString( "uContent" );
            uCategory = bundle.getString( "uCategory" );
            ucDate = bundle.getString( "ucDate" );
            uAnswer = bundle.getString( "uAnswer" );
            uUserID = bundle.getString( "uUserID" );

        }

        etAnswer.setText( uAnswer );

        btnCancel.setOnClickListener( this );
        btnConfirm.setOnClickListener( this );

        return view;
    }

    public void onClick(View v){
        answer = etAnswer.getText().toString();

        if (v == btnCancel){
            dismissAllowingStateLoss();
        }
        if (v == btnConfirm){
            try {
                if (TextUtils.isEmpty(answer)) {
                    Toast.makeText(getContext(), "답변을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    updateAnswer();
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void updateAnswer(){
        answer = etAnswer.getText().toString();

        title = uTitle;
        content = uContent;
        category = uCategory;
        cDate = ucDate;

        DatabaseReference reference;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference = database.getReference("Question");
        String userID = auth.getUid();

        String questionID = reference.push().getKey();

        try {
            Question question = new Question();
            question.setQuestionID( uQuestionID );
            question.setCategory( category );
            question.setcDate( cDate );
            question.setTitle( title );
            question.setContent( content );
            question.setUserID( uUserID );
            question.setAnswer( answer );

            reference.child( uUserID ).child( uQuestionID ).setValue( question );
            reference.child(category).child( uQuestionID ).setValue( question );
            Toast.makeText( getActivity(), "수정되었습니다", Toast.LENGTH_SHORT ).show();

            dismiss();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
