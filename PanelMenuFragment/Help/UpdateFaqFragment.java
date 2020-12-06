package com.example.tkitaka_fb.PanelMenuFragment.Help;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

public class UpdateFaqFragment extends DialogFragment implements View.OnClickListener {

    public static UpdateFaqFragment newInstance(){
        return new UpdateFaqFragment();
    }

    public void onCreate(Bundle savedInstanceState){
        super.onCreate( savedInstanceState );
        setStyle( DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle );
    }

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference reference, UserRef, FaqRef;

    EditText etTitle, etContent;
    Spinner spnrFaq;
    Button btnCancel, btnConfirm;

    ArrayAdapter<CharSequence> adapterFaq;

    String userID;

    String uFaqID, ucDate, uTitle, uContent, uCategory;

    String title, content, category, cDate;

    String selectedFaq;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate( R.layout.fragment_faq_write, container, false );

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userID = user.getUid();
        database = FirebaseDatabase.getInstance();

        UserRef = database.getReference("User");
        reference = database.getReference("Faq");
        FaqRef = database.getReference("Faq").child( "faqID" );

        etTitle = (EditText) view.findViewById( R.id.etTitle );
        etContent = (EditText) view.findViewById( R.id.etContent );
        spnrFaq = (Spinner) view.findViewById( R.id.spnrFaq );
        btnCancel = (Button) view.findViewById( R.id.btnCancel );
        btnConfirm = (Button) view.findViewById( R.id.btnConfirm );

        if (getArguments() != null){
            Bundle bundle = getArguments();
            uFaqID = bundle.getString( "uFaqID" );
            ucDate = bundle.getString("ucDate");
            uTitle = bundle.getString( "uTitle" );
            uContent = bundle.getString("uContent");
            uCategory = bundle.getString( "uCategory" );
        }

        adapterFaq = ArrayAdapter.createFromResource( getContext(),
                R.array.faq, android.R.layout.simple_spinner_item);
//        adapterFaq.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
        spnrFaq.setAdapter( adapterFaq );
//        spnrFaq.setSelected( ((ArrayAdapter<String>)spnrFaq.getAdapter()).getPosition( uCategory ) );

        setHasOptionsMenu( true );
        setCancelable( false );

//        spnrFaq.setOnItemClickListener( new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                selectedFaq = parent.getItemAtPosition( position ).toString();
//            }
//        } );

        etTitle.setText( uTitle );
        etContent.setText( uContent );

        btnCancel.setOnClickListener( this );
        btnConfirm.setOnClickListener( this );

        return view;
    }

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
                    updateFaq();
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private void updateFaq(){
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
            faq.setFaqID( uFaqID );
            faq.setTitle( title );
            faq.setContent( content );
            faq.setCategory( category );
//            faq.setcDate( cDate );
            faq.setUserID( userID );


            // reference.child( faqID ).setValue( faq );
            reference.child( uFaqID ).setValue( faq );
            Toast.makeText( getActivity(), "수정되었습니다", Toast.LENGTH_SHORT ).show();

            etTitle.setText( null );
            etContent.setText( null );
            spnrFaq.setAdapter( null );

            dismiss();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
