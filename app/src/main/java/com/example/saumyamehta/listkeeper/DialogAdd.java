package com.example.saumyamehta.listkeeper;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.saumyamehta.listkeeper.beans.Drops;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

//import io.realm.Realm;import io.realm.RealmConfiguration;


/**
 * Created by saumyamehta on 6/21/17.
 */

public class DialogAdd extends DialogFragment {
    private EditText mInputWhat;
    private ImageButton mBtnClose;
    private DatePicker mInputWhen;
    private Button mButtonAdd;
    private DatabaseReference mDatabase;
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
//        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
//        Realm.setDefaultConfiguration(realmConfiguration);
//        Realm realm = Realm.getDefaultInstance();
        Drops drop = new Drops(what, now, 0, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Drops").push().setValue(drop);
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
        mBtnClose = (ImageButton) view.findViewById(R.id.btnClose);
        mInputWhen = (DatePicker) view.findViewById(R.id.date_picker1);
        mButtonAdd = (Button) view.findViewById(R.id.btn_add_it);
        mBtnClose.setOnClickListener(mButtonListener);
        mButtonAdd.setOnClickListener(mButtonListener);
    }
}
