package com.example.tkitaka_fb.PanelMenuFragment.Help;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.RequestManager;
import com.example.tkitaka_fb.MainMenuFragment.Board.BoardFragment;
import com.example.tkitaka_fb.MainMenuFragment.Board.BoardListFragment;
import com.example.tkitaka_fb.Model.Question;
import com.example.tkitaka_fb.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class DetailQuestionFragment extends Fragment {

    public DetailQuestionFragment(){
        super();
    }

    public static DetailQuestionFragment newInstance(){
        DetailQuestionFragment fragment = new DetailQuestionFragment();
        Bundle args = new Bundle();
        fragment.setArguments( args );
        return fragment;
    }

    private DatabaseReference reference, questionRef, UserRef, allQuestionRef;

    //파이어베이스 유저
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseDatabase database;

    String title, content, category, userID, questionID;

    TextView tvTitle, tvContent, tvCategory, tvAnswer, tvView;

    AlertDialog dialog;

    ImageButton ibMore, ibAnswer;

    String ucDate, uAnswer, uCategory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_detail_question, container, false );

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        userID = user.getUid();
        UserRef = database.getReference().child( "User" );

        initInstances(view);

        getFIrebaseData();

        Bundle extra = this.getArguments();
        final String questionID = extra.getString( "QuestionID" );

        return view;
    }

    private void initInstances(View view) {
        tvTitle = (TextView) view.findViewById( R.id.tvTitle );
        tvContent = (TextView) view.findViewById( R.id.tvContent );
        tvCategory = (TextView) view.findViewById( R.id.tvCategory );
        tvView = (TextView) view.findViewById( R.id.tvView );

        Bundle extra = this.getArguments();

        questionID = extra.getString( "QuestionID" );

        tvTitle.setText( extra.getString( "Title" ) );
        tvContent.setText( extra.getString( "Content" ) );
        tvCategory.setText( "[" + extra.getString( "Category" ) + "]" );
        final String qUserID = extra.getString( "UserID" );

        tvAnswer = (TextView) view.findViewById( R.id.tvAnswer );
        tvAnswer.setText( extra.getString( "Answer" ) );

        ibMore = (ImageButton) view.findViewById( R.id.ibMore );
        ibMore.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((userID).equals( qUserID )) {
                    PopupMenu popup = new PopupMenu( getContext(), ibMore );
                    popup.setOnMenuItemClickListener( new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.update:
                                    updateQuestion();
                                    return true;
                                case R.id.delete:
                                    deleteQuestion();
                                    getActivity().getSupportFragmentManager().beginTransaction().
                                            replace(R.id.frameLayout, new QuestionTwoFragment()).commit();
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    } );
                    popup.inflate( R.menu.ib_more_question );
                    popup.show();
                } else {

                }
            }
        } );

        String answer = tvAnswer.getText().toString();

        if (answer.equals( "답변 예정" )) {
            tvView.setText( "답변 예정" );
        } else {
            tvView.setText( "답변완료" );
            tvView.setBackgroundColor( getContext().getResources().getColor( R.color.main ) );

        }

    }

    void getFIrebaseData(){
        Bundle extra = this.getArguments();
        final String questionID = extra.getString( "QuestionID" );

        DatabaseReference reference = database.getReference().child("Question").child(userID).child(questionID);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Question question = dataSnapshot.getValue(Question.class);
                    uCategory = question.getCategory();
                    ucDate = question.getcDate();
                    uAnswer = question.getAnswer();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateQuestion(){
        try{
            title = tvTitle.getText().toString();
            content = tvContent.getText().toString();
            category = tvCategory.getText().toString();

            UpdateQuestionFragment dialog = UpdateQuestionFragment.newInstance();

            Bundle bundle = new Bundle(  );
            bundle.putString( "QuestionID", questionID );
            bundle.putString( "Category", category );
            bundle.putString( "Title", title );
            bundle.putString( "cDate", ucDate );
            bundle.putString( "Content", content );
            bundle.putString( "Answer", uAnswer );

            dialog.setArguments( bundle );
            dialog.show(getFragmentManager(), "fragmentDialog");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void deleteQuestion(){
        try{
            allQuestionRef = database.getReference().child( "Question" ).child( uCategory );
            allQuestionRef.child(questionID).removeValue();

            questionRef = database.getReference().child( "Question" ).child( userID );
            questionRef.child( questionID ).removeValue();


            Toast.makeText( getContext(), "삭제 완료", Toast.LENGTH_SHORT ).show();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new QuestionTwoFragment()).commit();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
