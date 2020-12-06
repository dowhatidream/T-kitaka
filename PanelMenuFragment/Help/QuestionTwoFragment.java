package com.example.tkitaka_fb.PanelMenuFragment.Help;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.tkitaka_fb.Model.Chat;
import com.example.tkitaka_fb.Model.ChatList;
import com.example.tkitaka_fb.Model.Question;
import com.example.tkitaka_fb.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class QuestionTwoFragment extends Fragment {

    RecyclerView rvQue;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference reference;

    QuestionTwoFragment.QuestionAdapter questionAdapter;
    List<Question> questionList;

    String userID;
    String uQuestionID, uTitle, uContent, uCategory, uAnswer, ucDate;
    Question question;

    public QuestionTwoFragment() { super(); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_question_two, container, false );

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        userID = user.getUid();

        questionList = new ArrayList<>(  );
        questionAdapter = new QuestionAdapter(getContext(), questionList );

        rvQue = (RecyclerView) view.findViewById( R.id.rvQue );
        rvQue.setHasFixedSize( true );
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager( getContext() );
//        linearLayoutManager.setOrientation( LinearLayoutManager.VERTICAL );
        rvQue.setLayoutManager( linearLayoutManager );
        rvQue.setAdapter( questionAdapter );

        questionAdapter.setOnItemClickListener( new QuestionAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(View view, int position) {
//                deleteQuestion(position);
            }

            public void onItemClick(View view, int position){
//                showActionsDialog(position);
            }

            @Override
            public void onAnswerClick(View view, int position) {
//                updateAnswer( position );
            }

            @Override
            public void onUpdateClick(View view, int position) {
//                updateQuestion( position );
            }

            @Override
            public void onDetailClick(View view, int position) {
                try {
                    DetailQuestionFragment detailQuestionFragment = new DetailQuestionFragment();

                    Question question = questionList.get( position );
                    String questionKey = question.getQuestionID();
                    String title = question.getTitle();
                    String content = question.getContent();
                    String category = question.getCategory();
                    String date = question.getcDate();
                    String userID = question.getUserID();
                    String answer = question.getAnswer();

                    Bundle bundle = new Bundle();
                    bundle.putString( "QuestionID", questionKey );
                    bundle.putString( "Title", title );
                    bundle.putString( "Content", content );
                    bundle.putString( "Category", category );
                    bundle.putString( "Date", date );
                    bundle.putString( "UserID", userID );
                    bundle.putString( "Answer", answer );

                    detailQuestionFragment.setArguments( bundle );
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace( R.id.frameLayout, detailQuestionFragment ).commitAllowingStateLoss();
                    Log.d( "퀘스천", "아직 가지도 않았어" );
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        } );
        getFIrebaseData();

        return view;
    }

    void  getFIrebaseData(){
        reference = database.getReference("Question").child( userID );
        reference.addChildEventListener( new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Question question = dataSnapshot.getValue(Question.class);
                questionList.add( 0, question );
                rvQue.setAdapter( questionAdapter );
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
        } );
    }

    public static class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder>{

        public interface OnItemClickListener{
            void onDeleteClick(View view, int position);
            void onItemClick(View view, int position);
            void onAnswerClick(View view, int position);
            void onUpdateClick(View view, int position);
            void onDetailClick(View view, int position);
        }

        private Context context;
        private OnItemClickListener mListener = null;

        Question question;

        public void setOnItemClickListener(OnItemClickListener listener){
            this.mListener = listener;
        }

        public QuestionAdapter(OnItemClickListener listener) {
            this.mListener = listener;
        }

        List<Question> listArray;

        public QuestionAdapter(Context context, List<Question> list){
            this.listArray = list;
            this.context = context;
        }

        @NonNull
        @Override
        public QuestionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
            View view = LayoutInflater.from( viewGroup.getContext() )
                    .inflate( R.layout.item_question, viewGroup, false);
            ViewHolder holder = new ViewHolder( view );

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull final QuestionAdapter.ViewHolder holder, int position) {
            final Question question = listArray.get( position );
            holder.tvTitle.setText( question.getTitle() );
            holder.tvCategory.setText( "[" + question.getCategory() + "]" );
            holder.tvContent.setText( question.getContent() );
            holder.tvcDate.setText(question.getcDate());

            final ImageButton ibMore = holder.ibMore;
            final ImageButton ibQue = holder.ibQue;

            Question question2 = listArray.get(position); // 문의 답변
            String answer = question2.getAnswer(); // 답변예정
            Log.d( "퀘스천" , "이건뭐지     "+answer);

            if (answer.equals( "답변 예정" )) {
                holder.tvView.setText( "답변예정" );
            } else {
                try {
                    holder.tvView.setText( "답변완료" );
                    holder.tvView.setBackgroundColor( context.getResources().getColor( R.color.main ) );
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView tvTitle, tvContent, tvCategory, tvAnswer, tvView, tvcDate;
            public RelativeLayout view_question;
            public ImageButton ibMore, ibQue;
            public LinearLayout llQue;

            public ViewHolder(@NonNull View itemView) {
                super( itemView );

                final FirebaseAuth auth = FirebaseAuth.getInstance();
                final FirebaseUser user = auth.getCurrentUser();

                tvTitle = itemView.findViewById( R.id.tvTitle );
                tvContent = itemView.findViewById( R.id.tvContent );
                tvCategory = itemView.findViewById( R.id.tvCategory );
                tvView = itemView.findViewById( R.id.tvView );
                view_question = itemView.findViewById( R.id.view_question );
                tvcDate = itemView.findViewById(R.id.tvcDate);
                ibMore = itemView.findViewById( R.id.ibMore );

                llQue = itemView.findViewById( R.id.llQue );

                llQue.setOnClickListener( new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            if (mListener != null) {
                                mListener.onDetailClick( v, position );
                            }
                        }
                    }
                } );
            }
        }

        @Override
        public int getItemCount() {
            return listArray.size();
        }
    }
}
