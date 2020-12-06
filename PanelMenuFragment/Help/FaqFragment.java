package com.example.tkitaka_fb.PanelMenuFragment.Help;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tkitaka_fb.MainMenuFragment.MainActivity;
import com.example.tkitaka_fb.MainMenuFragment.Trip.TripListFragment;
import com.example.tkitaka_fb.Model.Faq;
import com.example.tkitaka_fb.Model.TripInfo;
import com.example.tkitaka_fb.Model.User;
import com.example.tkitaka_fb.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.ref.Reference;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FaqFragment extends Fragment  {

    public static FaqFragment newInstance(){

        return new FaqFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    private static final String EXTRA_FAQ_KEY = "faq_key";

    Spinner spinner;
    FloatingActionButton btnWrite;

    private FirebaseDatabase database;
    private DatabaseReference reference, UserRef;
    private FirebaseUser user;

    private FirebaseAuth auth;

    String userID;

    private RecyclerView rv;
    List<Faq> faqList;

    Faq faq;

    FaqAdapter faqAdapter;

    String uFaqID, uCodeID, ucDate, uTitle, uContent, uCategory;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return inflater.inflate( R.layout.fragment_faq, container, false);
        View view = inflater.inflate( R.layout.fragment_faq, container, false );

        spinner = (Spinner) view.findViewById( R.id.spnrFaq );
        btnWrite = (FloatingActionButton) view.findViewById( R.id.btnWrite );

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();

        userID = user.getUid();

//        recyclerView = (RecyclerView) view.findViewById( R.id.rvList );
//        recyclerView.setLayoutManager( new LinearLayoutManager( this ) );

        isAdmin();

        btnWrite.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FaqWriteFragment dialog = new FaqWriteFragment().newInstance();
                dialog.show(getFragmentManager(), "FaqWriteDialog");
            }
        } );

        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child( "Faq" );

        rv = (RecyclerView) view.findViewById( R.id.rv );
        rv.setHasFixedSize( true );

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager( getContext() );
        rv.setLayoutManager( linearLayoutManager );

        faqList = new ArrayList<>(  );
        faqAdapter = new FaqAdapter( getContext(), faqList );

        faqAdapter.setOnItemClickListener( new FaqAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                showActionsDialog(position);
            }
        } );

        getFirebaseData();

        return view;
    }

    private void isAdmin() {
        UserRef = database.getReference().child("User");
        UserRef.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue( User.class );

                    String isAdmin = user.getUserGrade();

                    if (isAdmin.equals("admin")) {
                        btnWrite.show();
                    } else {
                        btnWrite.hide();
                    }
                }
                else {
                    Toast.makeText(getContext(), "프로필 없음..", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void showActionsDialog(final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder( getContext() );
        builder.setCancelable( false ).setMessage( "수정 또는 삭제하시겠습니까?" )
                .setPositiveButton( "수정", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateData(position);
                    }
                } )
                .setNegativeButton( "삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteData(position);

                        faqList.remove( position );
                        rv.removeViewAt( position );
                        faqAdapter.notifyItemRemoved( position );
                        faqAdapter.notifyItemRangeChanged( position, faqList.size() );
                        faqAdapter.notifyDataSetChanged();
                    }
                } )
                .setNeutralButton( "취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                } );

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void getFirebaseData(){
        try {
            reference = database.getReference( "Faq" );
            reference.addChildEventListener( new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    if (dataSnapshot.exists()) {
                        Faq faq = dataSnapshot.getValue( Faq.class );
                        faqList.add(0, faq );
                        rv.setAdapter( faqAdapter );
                    }
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
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void updateData(final int position){
        try{
            faq = faqList.get( position );

            uFaqID = faq.getFaqID();
            ucDate = faq.getcDate();
            uTitle = faq.getTitle();
            uContent = faq.getContent();
            uCategory = faq.getCategory();

            UpdateFaqFragment dialog = UpdateFaqFragment.newInstance();

            Bundle bundle = new Bundle(  );
            bundle.putString( "uFaqID", uFaqID );
            bundle.putString( "ucDate", ucDate );
            bundle.putString( "uTitle", uTitle );
            bundle.putString( "uContent", uContent );
            bundle.putString( "uCategory", uCategory );

            dialog.setArguments(bundle);
            dialog.show(getFragmentManager(), "fragmentDialog");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void deleteData(int position){
        try{
            String faqID = faqList.get( position ).getFaqID();

            reference.child( faqID ).removeValue();

            Toast.makeText( getContext(), "삭제됨", Toast.LENGTH_SHORT ).show();

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static class FaqAdapter extends RecyclerView.Adapter<FaqAdapter.ViewHolder>{
        List<Faq> faqList;
        private OnItemClickListener mListener = null;
        private Context context;

        public FaqAdapter(Context context, List<Faq> list){
            this.context = context;
            this.faqList = list;
        }

        public interface OnItemClickListener{
            void onItemClick(View view, int position);
        }

        public void setOnItemClickListener(OnItemClickListener listener){
            this.mListener = listener;
        }

        public FaqAdapter(OnItemClickListener listener) {this.mListener = listener;}

        public FaqAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_faq, viewGroup, false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        public void onBindViewHolder(@NonNull FaqAdapter.ViewHolder holder, int position) {
            Faq faq = faqList.get(position);

            holder.tvTitle.setText(faq.getTitle());
            holder.tvContent.setText(faq.getContent());
            holder.tvCategory.setText("[" + faq.getCategory() + "]");
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            TextView tvTitle;
            TextView tvContent;
            TextView tvCategory;
            ImageButton ibEdit;

            public ViewHolder(View itemView){
                super(itemView);

                tvTitle = itemView.findViewById( R.id.tvTitle );
                tvCategory = itemView.findViewById( R.id.tvCategory );
                tvContent = itemView.findViewById( R.id.tvContent );
                ibEdit = itemView.findViewById( R.id.ibEdit );

                ibEdit.setOnClickListener( new View.OnClickListener() {
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
            return faqList.size();
        }

    }

}
