package com.example.saumyamehta.listkeeper;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.saumyamehta.listkeeper.adapters.AppBucketDrops;
import com.example.saumyamehta.listkeeper.beans.Drops;
import com.example.saumyamehta.listkeeper.widgets.BucketPickerView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

//import io.realm.Realm;import io.realm.RealmConfiguration;


/**
 * Created by saumyamehta on 6/21/17.
 */

public class DialogAdd extends DialogFragment {
    private EditText mInputWhat;
    private ImageButton mBtnClose;
    private TextView title;
    private BucketPickerView mInputWhen;
    private Button mButtonAdd;
    private DatabaseReference mDatabase;
    FirebaseAuth mAuth;

    public DialogAdd() {

    }

    private View.OnClickListener mButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.btn_add_it:

                    addAction();
                    break;
                case R.id.btnClose:

                    break;
            }
            dismiss();
        }
    };

    private void addAction() {
        String what = mInputWhat.getText().toString();
        long now = System.currentTimeMillis();
        final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
//        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
//        Realm.setDefaultConfiguration(realmConfiguration);
//        Realm realm = Realm.getDefaultInstance();
        final Drops drop = new Drops(what, now, mInputWhen.getTime(), false);

        if (mUser != null) {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child(mUser.getDisplayName()).child("Drops").push().setValue(drop);
        }

        Log.e("what", what);
        //        realm.copyToRealm(drop);
//        realm.commitTransaction();
//        realm.close();
    }

    @Nullable

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.dialog_add, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mInputWhat = (EditText) view.findViewById(R.id.et_drop);
        title = (TextView) view.findViewById(R.id.title);
        mBtnClose = (ImageButton) view.findViewById(R.id.btnClose);
        mInputWhen = (BucketPickerView) view.findViewById(R.id.date_picker1);
        mButtonAdd = (Button) view.findViewById(R.id.btn_add_it);
        mBtnClose.setOnClickListener(mButtonListener);
        mButtonAdd.setOnClickListener(mButtonListener);
        AppBucketDrops.setRalewayThin(getActivity(), mInputWhat, mButtonAdd, title);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogTheme);
        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase.getInstance().getReference().keepSynced(true);

    }
}
