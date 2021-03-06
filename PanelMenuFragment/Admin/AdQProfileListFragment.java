package com.example.tkitaka_fb.PanelMenuFragment.Admin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

public class AdQProfileListFragment extends Fragment {

    RecyclerView rvQue;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseDatabase database;
    private DatabaseReference reference, questionRef;

    AdQProfileListFragment.AdQProfileListAdapter adQProfileListAdapter;
    List<Question> adQProfileList;

    String userID;
    String CurrentUserID;
    List<Question> questionList;

    String uQuestionID, uContent, uTitle, ucDate, uAnswer,uCategory, uUserID;

    Question question;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_ad_qprofile_list, container, false );
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        userID = user.getUid();

        questionList = new ArrayList<>();
        adQProfileListAdapter = new AdQProfileListAdapter(getContext(), questionList);

        rvQue = (RecyclerView) view.findViewById( R.id.rvQue );
        rvQue.setHasFixedSize( true );
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager( getContext() );
        rvQue.setLayoutManager( linearLayoutManager );
        rvQue.setAdapter(adQProfileListAdapter);

        adQProfileList = new ArrayList<>(  );
        adQProfileListAdapter = new AdQProfileListAdapter(getContext(), adQProfileList);

        adQProfileListAdapter.setOnItemClickListener(new AdQProfileListAdapter.OnItemClickListener() {
            public void onItemClick(View view, int position){
                showActionsDialog(position);
            }
        } );

        getFIrebaseData();
        return view;
    }

    private void showActionsDialog(final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder( getContext() );
        builder.setCancelable( false ).setMessage( "답변하시겠습니까?" )
                .setPositiveButton( "답변하기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateAnswer(position);
                    }
                } )
                .setNegativeButton( "취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                } );

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    void  getFIrebaseData(){
        reference = database.getReference("Question").child("개인정보");
        reference.addChildEventListener( new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Question question = dataSnapshot.getValue(Question.class);
                adQProfileList.add( 0, question );
                rvQue.setAdapter(adQProfileListAdapter);
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

    private void updateAnswer(int position){
        question = adQProfileList.get( position );

        uQuestionID = question.getQuestionID();
        uTitle = question.getTitle();
        uContent = question.getContent();
        uCategory = question.getCategory();
        uAnswer = question.getAnswer();
        ucDate = question.getcDate();
        uUserID = question.getUserID();

        QuestionAnswerFragment dialog = QuestionAnswerFragment.newInstance();

        Bundle bundle = new Bundle(  );
        bundle.putString( "uQuestionID", uQuestionID );
        bundle.putString( "uTitle", uTitle );
        bundle.putString( "uContent", uContent );
        bundle.putString( "uCategory", uCategory );
        bundle.putString( "ucDate", ucDate );
        bundle.putString( "uAnswer", uAnswer );
        bundle.putString( "uUserID", uUserID );

        dialog.setArguments( bundle );
        dialog.show( getFragmentManager(), "fragmentDialog" );
        Log.d( "여기", "여기까지는 왔어 그래도" );
    }

    public static class AdQProfileListAdapter extends RecyclerView.Adapter<AdQProfileListAdapter.ViewHolder>{

        public interface OnItemClickListener{
            void onItemClick(View view, int position);
        }

        private Context context;
        private OnItemClickListener mListener = null;

        Question question;

        public void setOnItemClickListener(OnItemClickListener listener){
            this.mListener = listener;
        }

        public AdQProfileListAdapter(OnItemClickListener listener) {
            this.mListener = listener;
        }

        List<Question> listArray;

        public AdQProfileListAdapter(Context context, List<Question> list){
            this.listArray = list;
            this.context = context;
        }

        @NonNull
        @Override
        public AdQProfileListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
            View view = LayoutInflater.from( viewGroup.getContext() )
                    .inflate( R.layout.item_question2, viewGroup, false);
            ViewHolder holder = new ViewHolder( view );
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull final AdQProfileListAdapter.ViewHolder holder, int position) {
            final Question question = listArray.get( position );
            holder.tvTitle.setText( question.getTitle() );
            holder.tvCategory.setText( "[" + question.getCategory() + "]" );
            holder.tvContent.setText( question.getContent() );
            holder.tvAnswer.setText( question.getAnswer() );
            holder.tvcDate.setText(question.getcDate());

            Question question2 = listArray.get( position );
            String answer = question2.getAnswer();
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

            TextView tvTitle, tvContent, tvCategory, tvAnswer, tvView, tvcDate;
            RelativeLayout view_question;
            ImageButton ibMore, ibQue;
            LinearLayout llQue;

            public ViewHolder(@NonNull View itemView) {
                super( itemView );

                final FirebaseAuth auth = FirebaseAuth.getInstance();
                final FirebaseUser user = auth.getCurrentUser();

                tvTitle = itemView.findViewById( R.id.tvTitle );
                tvContent = itemView.findViewById( R.id.tvContent );
                tvCategory = itemView.findViewById( R.id.tvCategory );
                tvAnswer = itemView.findViewById( R.id.tvAnswer );
                view_question = itemView.findViewById( R.id.view_question );
                tvcDate = itemView.findViewById(R.id.tvcDate);
                tvView = itemView.findViewById( R.id.tvView );
                ibMore = itemView.findViewById( R.id.ibMore );
//                ibQue = itemView.findViewById( R.id.ibQue );

                ibMore.setOnClickListener( new View.OnClickListener() {
                    public void onClick(View view){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            if (mListener != null) {
                                mListener.onItemClick( view, position );
                                notifyItemChanged( position );
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

