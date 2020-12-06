package com.example.tkitaka_fb.PanelMenuFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tkitaka_fb.R;

import java.io.File;

public class SettingFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    TextView tvDelete;
    Switch sApp;
    Switch sChat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_setting, container, false );

        tvDelete = (TextView) view.findViewById( R.id.tvDelete );
        tvDelete.setOnClickListener( this );
        sApp = (Switch) view.findViewById( R.id.sApp );
        sApp.setOnCheckedChangeListener( this );
        sChat = (Switch) view.findViewById( R.id.sChat );
        sChat.setOnCheckedChangeListener( this );

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v == tvDelete){
            File cache = getContext().getCacheDir();
            File appDir = new File( cache.getParent() );
            if(appDir.exists()) {
                String[] children = appDir.list();
                for (String s : children) {
                    // 다운로드 파일은 지우지 않도록 설정
                    deleteDir(new File( appDir, s ));
                    Log.d("test", "File /date/date" + getContext().getPackageName() + "/" + s + " DELETED");
                    Toast.makeText( getActivity(), "캐시가 삭제되었습니다.", Toast.LENGTH_SHORT ).show();
                }
            }
        }
    }

    // 캐시 삭제
    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir( new File( dir, children[i] ) );
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    // 스위치 선택
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == sApp) {
            Toast.makeText( getActivity(), "체크 상태 = " + isChecked, Toast.LENGTH_SHORT).show();

        }
        else if (buttonView == sChat) {

        }
    }
}