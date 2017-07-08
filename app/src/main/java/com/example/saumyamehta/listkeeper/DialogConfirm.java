package com.example.saumyamehta.listkeeper;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.saumyamehta.listkeeper.adapters.AppBucketDrops;
import com.example.saumyamehta.listkeeper.adapters.CompleteListener;
import com.example.saumyamehta.listkeeper.adapters.ConfirmListener;

import org.w3c.dom.Text;

/**
 * Created by saumyamehta on 6/23/17.
 */

public class DialogConfirm extends DialogFragment {
    private Button mBtnCompleted;
    private Button mBtnClose;
    private TextView mTextTitle, mTextMessage, helper, helper1;
    private ConfirmListener mConfirmListener;
    private ImageView handy, handy1;

    public DialogConfirm() {
    }

    private View.OnClickListener mButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.btn_confirm:
                    confirmed();
                    break;
                case R.id.btn_close_dialog:
                    canceled();
                    break;
            }
            dismiss();
        }
    };

    private void confirmed() {
        Bundle args = getArguments();
        if (mConfirmListener != null && args != null) {
            int position = args.getInt("POSITION");
            mConfirmListener.onConfirm(position);
        }
    }

    private void canceled() {
        Bundle args = getArguments();
        if (mConfirmListener != null && args != null) {
            int position = args.getInt("POSITION");
            mConfirmListener.onCancel(position);
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_confirm, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBtnClose = (Button) view.findViewById(R.id.btn_close_dialog);
        mBtnCompleted = (Button) view.findViewById(R.id.btn_confirm);
        mTextTitle = (TextView) view.findViewById(R.id.title);
        mTextMessage = (TextView) view.findViewById(R.id.et_drop);
        mBtnClose.setOnClickListener(mButtonListener);
        mBtnCompleted.setOnClickListener(mButtonListener);
        handy = (ImageView) view.findViewById(R.id.point_confirm);
        handy1 = (ImageView) view.findViewById(R.id.point_cancel);
        helper = (TextView) view.findViewById(R.id.pointer_helper_confirm);
        helper1 = (TextView) view.findViewById(R.id.pointer_helper_cancel);
        handy1.setVisibility(View.GONE);
        helper1.setVisibility(View.GONE);
        AppBucketDrops.setRalewayThin(getActivity(), mBtnCompleted, mBtnClose, mTextMessage, mTextTitle, helper, helper1);

        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (pref.getBoolean("activity_executed_1", false)) {
            handy.setVisibility(View.GONE);
            helper.setVisibility(View.GONE);
            handy1.setVisibility(View.GONE);
            helper1.setVisibility(View.GONE);
        } else {
            if (handy.getVisibility() == View.VISIBLE) {
                final Animation slide_down = AnimationUtils.loadAnimation(getActivity(),
                        R.anim.swipe_down);
                final Animation slide_down1 = AnimationUtils.loadAnimation(getActivity(),
                        R.anim.swipe_down1);

                final Animation slide_up = AnimationUtils.loadAnimation(getActivity(),
                        R.anim.swipe_up);
                handy.startAnimation(slide_down);
                slide_down.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        handy.setVisibility(View.GONE);
                        helper.setVisibility(View.GONE);
                        handy1.setVisibility(View.VISIBLE);
                        helper1.setVisibility(View.VISIBLE);
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                handy1.startAnimation(slide_down1);
                            }
                        });

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                slide_down1.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        handy1.setVisibility(View.GONE);
                        helper1.setVisibility(View.GONE);
                        SharedPreferences.Editor ed = pref.edit();
                        ed.putBoolean("activity_executed_1", true);
                        ed.commit();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

            }

        }
    }

    public void setConfirmListener(ConfirmListener mConfirm) {
        this.mConfirmListener = mConfirm;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogTheme);

    }
}
