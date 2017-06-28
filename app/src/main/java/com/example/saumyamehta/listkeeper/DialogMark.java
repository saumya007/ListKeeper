package com.example.saumyamehta.listkeeper;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.saumyamehta.listkeeper.adapters.CompleteListener;

/**
 * Created by saumyamehta on 6/23/17.
 */

public class DialogMark extends DialogFragment {
    private Button mBtnCompleted;
    private ImageButton mBtnClose;
    private CompleteListener mCompleteListener;

    public DialogMark() {
    }

    private View.OnClickListener mButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.btn_completed:
                    markCompleted();
                    break;
                case R.id.btn_close:

                    break;
            }
            dismiss();
        }
    };

    private void markCompleted() {
        Bundle args = getArguments();
        if (mCompleteListener != null && args != null) {
            int position = args.getInt("POSITION");
            mCompleteListener.onComplete(position);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_mark, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBtnClose = (ImageButton) view.findViewById(R.id.btn_close);
        mBtnCompleted = (Button) view.findViewById(R.id.btn_completed);
        mBtnClose.setOnClickListener(mButtonListener);
        mBtnCompleted.setOnClickListener(mButtonListener);

    }

    public void setCompleteListener(CompleteListener mCompleteListener) {
        this.mCompleteListener = mCompleteListener;
    }
}
