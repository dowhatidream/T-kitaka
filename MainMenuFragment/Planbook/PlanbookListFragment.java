package com.example.tkitaka_fb.MainMenuFragment.Planbook;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
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

import com.example.tkitaka_fb.Model.Planbook;
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

public class PlanbookListFragment extends Fragment {

    RecyclerView rvPlanbook;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseDatabase database;

    private DatabaseReference reference, planbookRef,  planningRef;

    PlanbookListFragment.PlanbookAdapter planbookAdapter;
    List<Planbook> list;

    String CurrentUserID;

    public PlanbookListFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_planbook_list, container, false);

        // 현재 유저 불러오기
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        CurrentUserID = user.getUid();

        //어댑터
        rvPlanbook = (RecyclerView) view.findViewById(R.id.rvPlanbook);
        rvPlanbook.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvPlanbook.setLayoutManager(linearLayoutManager);

        list = new ArrayList<>();
        planbookAdapter = new PlanbookAdapter(getContext(), list);

        getFirebaseData(); // 읽어들임

        planbookAdapter.setOnItemClickListener(new PlanbookAdapter.OnItemClickListener() {
            @Override
            public void onUpdateClick(View view, int position) {
                updateData(position);
            }

            @Override
            public void onDeleteClick(View view, int position) {
                try{
                    planbookRef = database.getReference().child("Planbook").child(CurrentUserID);
                    String planbookID =  list.get(position).getPlanbookID();
                    planbookRef.child(planbookID).removeValue();

                    planningRef = database.getReference().child("Planning").child(CurrentUserID);
                    planningRef.child(planbookID).removeValue();

                    Toast.makeText(getContext(), "삭제 완료", Toast.LENGTH_SHORT).show();
                    list.remove(position);
                    rvPlanbook.removeViewAt(position);
                    planbookAdapter.notifyItemRemoved(position);
                    planbookAdapter.notifyItemRangeChanged(position, list.size());
                    planbookAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "뭔가 이상해요ㅠ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onDetailClick(View view, int position) {
                DetailPlanbookFragment detailPlanbookFragment = new DetailPlanbookFragment();

                Planbook planbook = list.get(position);
                String planbookKey =  planbook.getPlanbookID();
                String title =  planbook.getTitle();
                String place =  planbook.getPlace();
                String deDate =  planbook.getDeDate();
                String reDate =  planbook.getReDate();

                Bundle bundle = new Bundle();
                bundle.putString("PlanbookID", planbookKey);
                bundle.putString("Title", title);
                bundle.putString("Place", place);
                bundle.putString("Dedate", deDate);
                bundle.putString("Redate", reDate);

                detailPlanbookFragment.setArguments(bundle);

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fgPlanbook, detailPlanbookFragment).commit();
            }
        });
        return view;
    }

    //플랜북 수정
    private void updateData(final int position){
        try{

            // 해당 정보 얻어서
            Planbook planbook = list.get(position);
            String planbookKey =  planbook.getPlanbookID();
            String title =  planbook.getTitle();
            String place =  planbook.getPlace();
            String deDate =  planbook.getDeDate();
            String reDate =  planbook.getReDate();

            // 다이얼로그 프레그먼트로 전달해줌
            UpdatePlanbookFragment updatePlanbookFragment = UpdatePlanbookFragment.newInstance();

            Bundle bundle = new Bundle();
            bundle.putString("planbookId", planbookKey);
            bundle.putString("Title", title);
            bundle.putString("Place", place);
            bundle.putString("Dedate", deDate);
            bundle.putString("Redate", reDate);

            updatePlanbookFragment.setArguments(bundle);

            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fgPlanbook2, updatePlanbookFragment).commit();
        }
        catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getContext(), "뭔가 이상해요ㅠ", Toast.LENGTH_SHORT).show();
        }

    }

    //데이터 가져오기
    void getFirebaseData(){
        reference = database.getReference("Planbook").child(CurrentUserID);
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Planbook planbook = dataSnapshot.getValue(Planbook.class);
                list.add(0, planbook);
                rvPlanbook.setAdapter(planbookAdapter);

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
        });
    }

    //플랜북 리스트 어댑터
    public static class PlanbookAdapter extends RecyclerView.Adapter<PlanbookAdapter.ViewHolder>{

        public interface OnItemClickListener{
            void onDeleteClick(View view, int position);
            void onUpdateClick(View view, int position);
            void onDetailClick(View view, int position);
        }

        private Context context;
        private OnItemClickListener mListener = null;

        public void setOnItemClickListener(OnItemClickListener listener){
            this.mListener = listener;
        }

        public PlanbookAdapter(OnItemClickListener listener) {
            this.mListener = listener;
        }

        List<Planbook> listArray;

        public  PlanbookAdapter( Context context, List<Planbook> list){
            this.listArray = list;
            this.context = context;
        }

        @NonNull
        @Override
        public PlanbookAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_planbook, viewGroup, false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull final PlanbookAdapter.ViewHolder holder, int position) {
            Planbook planbook = listArray.get(position);
            holder.tvTitle.setText(planbook.getTitle());
            holder.tvPlace.setText(planbook.getPlace());
            holder.deDate.setText(planbook.getDeDate());
            holder.reDate.setText(planbook.getReDate());

            holder.ibMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    final int p = holder.getAdapterPosition() ;
                    PopupMenu popup = new PopupMenu(context, view);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.update:
                                    if (p != RecyclerView.NO_POSITION) {
                                        if (mListener != null) {
                                            mListener.onUpdateClick(view, p);
                                            notifyItemChanged(p);
                                        }
                                    }
                                    return true;
                                case R.id.delete:
                                    if (p != RecyclerView.NO_POSITION) {
                                        if (mListener != null) {
                                            mListener.onDeleteClick(view, p);
                                            notifyItemChanged(p);
                                        }
                                    }
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                    popup.inflate(R.menu.ib_more_menu);
                    popup.show();
                }
            });
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            TextView tvTitle, deDate, reDate, tvPlace;
            RelativeLayout viewForeground;
            ImageButton ibMore;
            CardView cv;

            public ViewHolder(View itemView) {
                super(itemView);
                tvTitle=itemView.findViewById(R.id.tvTitle);
                deDate=itemView.findViewById(R.id.deDate);
                reDate=itemView.findViewById(R.id.reDate);
                tvPlace=itemView.findViewById(R.id.tvPlace);
                viewForeground=itemView.findViewById(R.id.view_foreground);
                ibMore= itemView.findViewById(R.id.ibMore);
                cv = itemView.findViewById(R.id.cv);
                cv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition() ;
                        if (position != RecyclerView.NO_POSITION) {
                            if (mListener != null) {
                                mListener.onDetailClick(view, position);
                            }
                        }
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return listArray.size();
        }
    }
}
