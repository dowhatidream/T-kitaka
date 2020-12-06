package com.example.tkitaka_fb.MainMenuFragment.Planbook;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.tkitaka_fb.Model.Planning;
import com.example.tkitaka_fb.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class DetailPlanbookFragment extends Fragment {

    public static final String EXTRA_PLANBOOK_KEY = "planbook_key";

    public static DetailPlanbookFragment newInstance(){
        DetailPlanbookFragment fragment = new DetailPlanbookFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public DetailPlanbookFragment() {
        super();
    }

    private DatabaseReference reference, planningRef;

    RecyclerView rvDetailPlanbook;

    TextView tvTitle, tvPlace, tvdeDate, tvreDate, tv1;
    ImageButton ibBack;

    List<Planning> list;

    DetailPlanbookFragment.PlanningAdapter planningAdapter;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseDatabase database;

    //플랜 작성
    AlertDialog dialog;
    EditText etTime, etPlace, etContent, etDate;
    Button btnAdd;
    String cContent, cTime, cPlace, planbookID, userID, pdate ;

    //시간 변수
    int hour, min;
    TimePickerDialog.OnTimeSetListener time1;
    Calendar myCalendar;

    //날짜
    private int yearD, monthD, dayD;
    private int yearR, monthR, dayR;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_planbook, container, false);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        userID = user.getUid();

        //PlanbookListFragment에서 받아온 번들 뷰에 삽입 메소드
        initInstances(view);

        //플랜 작성 및 확인 버튼
        btnAdd = view.findViewById(R.id.btnAdd);
        etTime = view.findViewById(R.id.etTime);
        etPlace = view.findViewById(R.id.etPlace);
        etContent = view.findViewById(R.id.etContent);
        etDate = view.findViewById(R.id.etDate);

        //캘린더.시계 불러오기
        myCalendar = Calendar.getInstance();
        hour = myCalendar.get(Calendar.HOUR_OF_DAY);
        min = myCalendar.get(Calendar.MINUTE);

        //에디트 텍스트 클릭 리스너
        etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(getContext(), time1,
                        myCalendar.get(Calendar.HOUR_OF_DAY),
                        myCalendar.get(Calendar.MINUTE), false).show();
            }
        });

        //시간 설정
        updateDisplay();

        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar minDate = Calendar.getInstance();
                final Calendar maxDate = Calendar.getInstance();

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        etDate.setText(year + "." + (monthOfYear + 1) + "." + dayOfMonth);
                    }
                }, yearD, monthD, dayD);

                minDate.set(yearD, monthD -1, dayD);
                maxDate.set(yearR, monthR -1, dayR);
                datePickerDialog.getDatePicker().setMinDate(minDate.getTime().getTime());
                datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

                datePickerDialog.show();
            }
        });

        //플랜 추가
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // 기재 누락 여부
                    if (TextUtils.isEmpty(etPlace.getText().toString()) && TextUtils.isEmpty(etContent.getText().toString())) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        dialog = builder.setMessage("내용을 입력해주세요")
                                .setPositiveButton("확인", null)
                                .create();
                        dialog.show();
                    } else {
                        createPlanning();    //댓글 DB 작성
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        getFirebaseData();

        //번들로 플랜북ID 받아옴
        Bundle extra = this.getArguments();
        final String planbookID = extra.getString("PlanbookID");

        //닫기 버튼
        ibBack = view.findViewById(R.id.ibBack);
        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new PlanbookFragment()).commit();
            }
        });

        //어댑터 설정
        rvDetailPlanbook = (RecyclerView) view.findViewById(R.id.rvDetailPlanbook);
        rvDetailPlanbook.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvDetailPlanbook.setLayoutManager(linearLayoutManager);

        list = new ArrayList<>();
        planningAdapter = new PlanningAdapter(getContext(), list);

        //어댑터 안 클릭 리스너 설정
        planningAdapter.setOnItemClickListener(new PlanningAdapter.OnItemClickListener() {

            //플랜북 삭제
            @Override
            public void onDeleteClick(View view, int position) {
                try{
                    Planning pPlanning = list.get(position);

                    planningRef = database.getReference().child("Planning").child(userID);
                    planningRef.child(planbookID).child(pPlanning.getPlanningID()).removeValue();

                    Toast.makeText(getContext(), "삭제 완료", Toast.LENGTH_SHORT).show();
                    list.remove(position);
                    rvDetailPlanbook.removeViewAt(position);
                    planningAdapter.notifyItemRemoved(position);
                    planningAdapter.notifyItemRangeChanged(position, list.size());
                    planningAdapter.notifyDataSetChanged();

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "뭔가 이상해요ㅠ", Toast.LENGTH_SHORT).show();
                } finally {
                }
            }

        });
        return view;
    }

    //PlanbookListFragment에서 받아온 번들 값 레이아웃 변수에 저장
    private void initInstances(View view){

        Bundle extra = this.getArguments();
        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvPlace = (TextView) view.findViewById(R.id.tvPlace);
        tvdeDate = (TextView) view.findViewById(R.id.deDate);
        tvreDate = (TextView) view.findViewById(R.id.reDate);
        tv1 = (TextView) view.findViewById(R.id.tv1);


        tvTitle.setText(extra.getString("Title"));
        tvPlace.setText(extra.getString("Place"));
        tvdeDate.setText(extra.getString("Dedate"));
        tvreDate.setText(extra.getString("Redate"));
        planbookID = extra.getString("PlanbookID");

        String deDate = extra.getString("Dedate");
        String reDate = extra.getString("Redate");

        //날짜 문자배열로 나눠서 각 String에 저장
        String[] de = deDate.split("/");
        String deYear = de[0];
        String deMonth = de[1];
        String deDay = de[2];

        String[] re = reDate.split("/");
        String reYear = re[0];
        String reMonth = re[1];
        String reDay = re[2];

        //int로 변환
        yearD = Integer.parseInt(deYear);
        monthD = Integer.parseInt(deMonth);
        dayD = Integer.parseInt(deDay);
        yearR = Integer.parseInt(reYear);
        monthR = Integer.parseInt(reMonth);
        dayR = Integer.parseInt(reDay);


    }

    //데이터 메소드
    void getFirebaseData(){
        Bundle extra = this.getArguments();
        final String planbookID = extra.getString("PlanbookID");

        planningRef = database.getReference("Planning").child(userID).child(planbookID);
        planningRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Planning planning = dataSnapshot.getValue(Planning.class);
                list.add(planning);
                rvDetailPlanbook.setAdapter(planningAdapter);
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

    //플랜 어댑터 메소드
    public static class PlanningAdapter extends RecyclerView.Adapter<PlanningAdapter.ViewHolder>{
        //클릭 리스너
        public interface OnItemClickListener{
            void onDeleteClick(View view, int position);
        }

        private Context context;
        private OnItemClickListener mListener = null;

        public void setOnItemClickListener(OnItemClickListener listener){
            this.mListener = listener;
        }

        public PlanningAdapter(OnItemClickListener listener) {
            this.mListener = listener;
        }

        List<Planning> listArray;

        public  PlanningAdapter( Context context, List<Planning> list){
            this.listArray = list;
            this.context = context;
        }

        //시간 변수
        int hour, min;
        TimePickerDialog.OnTimeSetListener time1;
        Calendar myCalendar;

        String tvTitle , tvTime, tvPlace, tvDate;

        @NonNull
        @Override
        public PlanningAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_detail_planning, viewGroup, false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull final PlanningAdapter.ViewHolder holder, int position) {
            Planning planning = listArray.get(position);
            holder.etContent.setText(planning.getTitle());
            holder.etPlace.setText(planning.getPlace());
            holder.etTime.setText(planning.getTime());
            holder.etDate.setText(planning.getDate());

            final ImageButton button = holder.ibMore;
            final FirebaseAuth auth = FirebaseAuth.getInstance();
            final FirebaseUser user = auth.getCurrentUser();

            //더보기 버튼
            holder.ibMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    final int p = holder.getAdapterPosition();
                    PopupMenu popup = new PopupMenu(context, button);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.update:
                                    //에디트텍스트 수정 가능하게 하고 스타일 변경
                                    holder.etContent.setEnabled(true);
                                    holder.etTime.setEnabled(true);
                                    holder.etPlace.setEnabled(true);
                                    holder.etDate.setEnabled(true);
                                    holder.btnCancel.setVisibility(View.VISIBLE);
                                    holder.btnUPlanning.setVisibility(View.VISIBLE);
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
                    popup.inflate(R.menu.ib_more_menu3);
                    popup.show();
                }
            });

            //수정 취소 버튼
            holder.btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    holder.etContent.setBackground(null);
                    holder.etContent.setEnabled(false);

                    holder.etTime.setBackground(null);
                    holder.etTime.setEnabled(false);

                    holder.etPlace.setBackground(null);
                    holder.etPlace.setEnabled(false);

                    holder.etDate.setBackground(null);
                    holder.etDate.setEnabled(false);

                    holder.btnCancel.setVisibility(View.GONE);
                    holder.btnUPlanning.setVisibility(View.GONE);
                }
            });

            //수정된 텍스트 저장하고 원래 뷰로 돌아감
            holder.btnUPlanning.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    final int p = holder.getAdapterPosition();
                    if (p != RecyclerView.NO_POSITION) {
                        if (mListener != null) {
                            tvTitle = holder.etContent.getText().toString();
                            tvTime = holder.etTime.getText().toString();
                            tvPlace = holder.etPlace.getText().toString();
                            tvDate = holder.etDate.getText().toString();

                            holder.etContent.setText(tvTitle);
                            holder.etTime.setText(tvTime);
                            holder.etPlace.setText(tvPlace);
                            holder.etDate.setText(tvDate);

                            holder.etContent.setBackground(null);
                            holder.etContent.setEnabled(false);

                            holder.etTime.setBackground(null);
                            holder.etTime.setEnabled(false);

                            holder.etPlace.setBackground(null);
                            holder.etPlace.setEnabled(false);

                            holder.etDate.setBackground(null);
                            holder.etDate.setEnabled(false);

                            holder.btnCancel.setVisibility(View.GONE);
                            holder.btnUPlanning.setVisibility(View.GONE);

                            //댓글 수정 메소드
                            updatePlanning(p);
                        }
                    }
                }
            });
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            public EditText etContent, etTime, etPlace, etDate;
            RelativeLayout viewForeground;
            ImageButton ibMore;
            public Button btnUPlanning, btnCancel;

            public ViewHolder(View itemView) {
                super(itemView);

                viewForeground=itemView.findViewById(R.id.view_foreground);
                ibMore= itemView.findViewById(R.id.ibMore);
                etContent = itemView.findViewById(R.id.etContent);
                etTime = itemView.findViewById(R.id.etTime);
                etPlace = itemView.findViewById(R.id.etPlace);
                etDate = itemView.findViewById(R.id.etDate);

                btnUPlanning = itemView.findViewById(R.id.btnUPlanning);
                btnCancel = itemView.findViewById(R.id.btnCancel);

                //버튼, 에디트 텍스트 막아둠
                etContent.setEnabled(false);
                etPlace.setEnabled(false);
                etTime.setEnabled(false);
                etDate.setEnabled(false);
                btnCancel.setVisibility(View.GONE);
                btnUPlanning.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return listArray.size();
        }

        //플랜 수정 메소드
        public void updatePlanning(final int p){
            Planning pPlanning = listArray.get(p);

            // 현재시간 저장
            TimeZone time;
            Date date = new Date();
            DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
            time = TimeZone.getTimeZone("Asia/Seoul");
            dateFormat.setTimeZone(time);
            String cdate = dateFormat.format(date);

            Planning uPlanning = new Planning();
            uPlanning.setPlanningID(pPlanning.getPlanningID()); // 위에서 설정한 고유 노드를 아예 기본키 컬럼으로 가져오기
            uPlanning.setPlanbookID(pPlanning.getPlanbookID());
            uPlanning.setTitle(tvTitle);
            uPlanning.setTime(tvTime);
            uPlanning.setPlace(tvPlace);
            uPlanning.setUserID(pPlanning.getUserID());
            uPlanning.setCDate(cdate);
            uPlanning.setDate(tvDate);

            //레퍼런스 child 경로 중 해당 댓글 아이디 경로의 값 수정
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference UPlanningRef = database.getReference("Planning").child(pPlanning.getUserID()).child(pPlanning.getPlanbookID());
            UPlanningRef.child(pPlanning.getPlanningID()).setValue(uPlanning);

            Toast.makeText(context, "수정 완료", Toast.LENGTH_SHORT).show();
        }
    }

    //플랜 작성 메소드
    private void createPlanning() {

        TimeZone time;
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        time = TimeZone.getTimeZone("Asia/Seoul");
        dateFormat.setTimeZone(time);
        String cdate = dateFormat.format(date);

        //작성 String
        cContent = etContent.getText().toString();
        cTime = etTime.getText().toString();
        cPlace = etPlace.getText().toString();
        pdate = etDate.getText().toString();

        String userID = auth.getUid(); // 현재 userID 가져오기

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reference = database.getReference("Planning");
        String planningID = reference.push().getKey(); //기본키이자 고유 노드

        try {
            Planning planning = new Planning();
            planning.setPlanningID(planningID); // 위에서 설정한 고유 노드를 아예 기본키 컬럼으로 가져오기
            planning.setPlanbookID(planbookID);
            planning.setTitle(cContent);
            planning.setTime(cTime);
            planning.setPlace(cPlace);
            planning.setCDate(cdate);
            planning.setUserID(userID);
            planning.setDate(pdate);

            reference.child(userID).child(planbookID).child(planningID).setValue(planning);
            planningAdapter.notifyDataSetChanged();

            //레이아웃 텍스트 초기화
            etContent.setText(null);
            etPlace.setText(null);
            etTime.setText(null);
            etDate.setText(null);

            Toast.makeText(getActivity(), "플랜 등록!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "뭔가 이상해요", Toast.LENGTH_SHORT).show();
        }
    }

    //시간 설정 메소드
    private void updateDisplay() {
        time1 = new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int Hour, int Minute) {
                hour = Hour;
                min = Minute;
                etTime.setText(new StringBuilder().append(pad(hour))
                        .append(":").append(pad(min)));
            }
        };
    }

    //시간 설정 메소드2
    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }
}