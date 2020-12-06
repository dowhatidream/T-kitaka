package com.example.tkitaka_fb.PanelMenuFragment.Help;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.tkitaka_fb.Model.Faq;
import com.example.tkitaka_fb.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class FaqWriteFragment extends DialogFragment implements View.OnClickListener {

    public static FaqWriteFragment newInstance() {
        return new FaqWriteFragment();
    }

    EditText etTitle, etContent;
    Spinner spnrFaq;
    Button btnCancel, btnConfirm;

    HelpFragment helpFragment;

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase database;

    String userID, title, content, category, cDate;

    ArrayAdapter<CharSequence> adapterFaq;

    private AlertDialog dialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_faq_write, container, false );

        etTitle = (EditText) view.findViewById( R.id.etTitle );
        etContent = (EditText) view.findViewById( R.id.etContent );
        spnrFaq = (Spinner) view.findViewById( R.id.spnrFaq );
        btnCancel = (Button) view.findViewById( R.id.btnCancel );
        btnConfirm = (Button) view.findViewById( R.id.btnConfirm );
        btnCancel.setOnClickListener( this );
        btnConfirm.setOnClickListener( this );

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userID = user.getUid();

        adapterFaq = ArrayAdapter.createFromResource( getContext(), R.array.faq,
                android.R.layout.simple_spinner_dropdown_item );
        spnrFaq.setAdapter( adapterFaq );


        return view;
    }

    @Override
    public void onClick(View v) {
        title = etTitle.getText().toString();
        content = etContent.getText().toString();
        category = spnrFaq.getSelectedItem().toString();

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
                    createFaq();
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void createFaq(){
        TimeZone time;
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        time = TimeZone.getTimeZone("Asia/Seoul");
        dateFormat.setTimeZone(time);
        cDate = dateFormat.format(date);
        title = etTitle.getText().toString();
        content = etContent.getText().toString();
        category = spnrFaq.getSelectedItem().toString();

        DatabaseReference reference;
        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Faq");

        String faqID = reference.push().getKey();

        try {
            Faq faq = new Faq(  );
            faq.setFaqID( faqID );
            faq.setTitle( title );
            faq.setContent( content );
            faq.setCategory( category );
            faq.setcDate( cDate );
            faq.setUserID( userID );

            reference.child( faqID ).setValue( faq );
            Toast.makeText( getActivity(), "글이 등록되었습니다", Toast.LENGTH_SHORT ).show();

            etTitle.setText( null );
            etContent.setText( null );
            spnrFaq.setAdapter( null );

            dismiss();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }
}
