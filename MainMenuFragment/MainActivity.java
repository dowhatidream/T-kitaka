package com.example.tkitaka_fb.MainMenuFragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.tkitaka_fb.MainMenuFragment.Board.BoardFragment;
import com.example.tkitaka_fb.MainMenuFragment.Chat.ChatFragment;
import com.example.tkitaka_fb.MainMenuFragment.Planbook.PlanbookMainFragment;
import com.example.tkitaka_fb.MainMenuFragment.Trip.TripFragment;
import com.example.tkitaka_fb.Model.User;
import com.example.tkitaka_fb.PanelMenuFragment.Admin.AdminFragment;
import com.example.tkitaka_fb.PanelMenuFragment.Help.HelpFragment;
import com.example.tkitaka_fb.PanelMenuFragment.NotificationsFragment;
import com.example.tkitaka_fb.PanelMenuFragment.PostFragment;
import com.example.tkitaka_fb.PanelMenuFragment.ProfileFragment;
import com.example.tkitaka_fb.PanelMenuFragment.SettingFragment;
import com.example.tkitaka_fb.R;
import com.example.tkitaka_fb.StartActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    // FrameLayout(틀)에 각 메뉴의 Fragment(실질적 내용)를 바꿔줌
    private FragmentManager fragmentManager = getSupportFragmentManager();

    // 바텀 네비게이션 내 메인메뉴 4개 Fragment
    private TripFragment menu1Fragment = new TripFragment();
    private ChatFragment menu2Fragment = new ChatFragment();
    private BoardFragment menu3Fragment = new BoardFragment();
    private PlanbookMainFragment menu4Fragment = new PlanbookMainFragment();

    // 네비게이션 패널 내 5개 Fragment(로그아웃 제외)
    private ProfileFragment list1Fragment = new ProfileFragment();
    private PostFragment list2Fragment = new PostFragment();
    private NotificationsFragment list3Fragment = new NotificationsFragment();
    private HelpFragment list4Fragment = new HelpFragment();
    private SettingFragment list5Fragment = new SettingFragment();
    private AdminFragment list6Fragment = new AdminFragment();

    Toolbar toolbar;
    CircleImageView civProfile;
    TextView tvUserName;
    private AlertDialog dialog;

    FirebaseUser user;
    FirebaseAuth auth; //
    DatabaseReference reference, UserRef;

    String userID;
    String userGrade;
    String currentUserId;
    NavigationView panelNav;
    MenuItem nav_item;


    public RequestManager mGlideRequestManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userID = user.getUid();

        mGlideRequestManager = Glide.with(this);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav); // 바텀 네비게이션
        panelNav = (NavigationView) findViewById(R.id.panelNav); // 네비게이션 패널

        // 툴바
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // 프로젝트명 가리기

        // 네비게이션 헤더 찾기
        View header = LayoutInflater.from(this).inflate(R.layout.nav_header_main, null);
        panelNav.addHeaderView(header); // 네비게이션 패널에 헤더 인식

        //Toolbar 패널 아이콘 추가
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_dehaze_black_24dp);

        // 네비게이션 패널 내 메뉴로 이동하고 리스너 호출
        DrawerLayout drawerLayout = findViewById(R.id.drawLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        panelNav.setNavigationItemSelectedListener(this);

        tvUserName = (TextView) header.findViewById(R.id.tvUserName); // 헤더 안에 있는 뷰
        civProfile = (CircleImageView) header.findViewById(R.id.civProfile);

        // 유저 프로필 찾기
        UserRef = database.getReference().child("User");
        UserRef.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue( User.class );
                    tvUserName.setText(user.getUserName());
                    currentUserId = user.getUserID();

                    //관리자 등급 불러와서
                    userGrade = user.getUserGrade();

                    //관리자인지 아닌지 확인한 후에 패널에 관리자 메뉴 보이기
                    Menu menuNav = panelNav.getMenu();
                    nav_item = menuNav.findItem(R.id.iAdmin);
                    if (userGrade.equals("member")) {
                        nav_item.setVisible(false);
                        nav_item.setEnabled(false);
                    } else {
                        nav_item.setVisible(true);
                        nav_item.setEnabled(true);
                    }

                    if (user.getUserProfile().equals("default")){
                        civProfile.setImageResource(R.mipmap.ic_launcher);
                    }
                    else {
                        Glide.with(getApplicationContext()).load(user.getUserProfile()).into(civProfile);
                    }
                }
                else {
                    Toast.makeText(MainActivity.this, "프로필 없음..", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });


        // 첫 화면 지정
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, menu1Fragment).commitAllowingStateLoss();

        // 바텀 네비게이션 메인메뉴 프레그먼트 4개 리스너
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                switch (item.getItemId()) {
                    case R.id.nav_home: {
                        transaction.replace(R.id.frameLayout, menu1Fragment).commitAllowingStateLoss();
                        break;
                    }
                    case R.id.nav_chatRoom: {
                        transaction.replace(R.id.frameLayout, menu2Fragment).commitAllowingStateLoss();
                        break;
                    }
                    case R.id.nav_board: {
                        transaction.replace(R.id.frameLayout, menu3Fragment).commitAllowingStateLoss();
                        break;
                    }
                    case R.id.nav_planBook: {
                        transaction.replace(R.id.frameLayout, menu4Fragment).commitAllowingStateLoss();
                        break;
                    }
                }
                return true;
            }
        });
    }

    // 네비게이션 패널 메뉴 프레그먼트 5개
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        int id = item.getItemId();

        // 동행찾기(홈 메뉴)
        if (id == R.id.iSearch) {
            transaction.replace(R.id.frameLayout, menu1Fragment).commitAllowingStateLoss();
        }
        // 알림
        if (id == R.id.iNotifications) {
            transaction.replace(R.id.frameLayout, list3Fragment).commitAllowingStateLoss();
        }
        // 개인 정보 수정
        if (id == R.id.iEditProfile) {
            transaction.replace(R.id.frameLayout, list1Fragment).commitAllowingStateLoss();
        }
        // 작성한 글
        if (id == R.id.iPost) {
            transaction.replace(R.id.frameLayout, list2Fragment).commitAllowingStateLoss();
        }
        // 고객센터/도움말
        if (id == R.id.iHelp) {
            transaction.replace(R.id.frameLayout, list4Fragment).commitAllowingStateLoss();
        }
        // 설정
        if (id == R.id.iSettings) {
            transaction.replace(R.id.frameLayout, list5Fragment).commitAllowingStateLoss();
        }
        // 관리자
        if (id == R.id.iAdmin) {
            transaction.replace(R.id.frameLayout, list6Fragment).commitAllowingStateLoss();
        }
        // 로그아웃
        if (id == R.id.iLogout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            dialog = builder.setMessage("로그아웃 하시겠습니까?")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int whichButton) {
                            auth.signOut();

                            Intent intent = new Intent(MainActivity.this, StartActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP); // 일단 클리어탑만 하긴 했음
                            startActivity(intent);

                        }
                    })
                    .setNegativeButton("취소", null)
                    .create();
            dialog.show();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawLayout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (user == null){
            SendLoginActivity();
        }
        else {
            CheckUserExist();
        }
    }

    private void CheckUserExist() {
        currentUserId = userID;
    }

    private void SendLoginActivity() {
        Intent intent = new Intent(MainActivity.this, StartActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // 유저 접속상태
    private void status(String status){
        reference = FirebaseDatabase.getInstance().getReference("User").child(userID);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userStatus", status);

        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }
}