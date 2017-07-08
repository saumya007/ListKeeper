package com.example.saumyamehta.listkeeper;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.text.TextPaint;
import android.util.Log;
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
import com.example.saumyamehta.listkeeper.adapters.CustomViewTarget;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;


public class DialogMark extends DialogFragment {
    private static final long MARK_ID = 0;
    private static final long COMPLETE = 1;
    private Button mBtnCompleted;
    private ImageButton mBtnClose;
    private CompleteListener mCompleteListener;
    private MaterialShowcaseView showcaseView;
    private ImageView handy;
    private TextView helper;
    int count = 0;

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
        handy = (ImageView) view.findViewById(R.id.imageView2);
        helper = (TextView) view.findViewById(R.id.pointer_helper);
        AppBucketDrops.setRalewayThin(getActivity(), mBtnCompleted, helper);
        if (handy.getVisibility() == View.VISIBLE) {
            final Animation slide_down = AnimationUtils.loadAnimation(getActivity(),
                    R.anim.swipe_down);

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
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (pref.getBoolean("activity_executed", false)) {
            handy.setVisibility(View.GONE);
            helper.setVisibility(View.GONE);
        } else {
            SharedPreferences.Editor ed = pref.edit();
            ed.putBoolean("activity_executed", true);
            ed.commit();
        }
    }

    public void setCompleteListener(CompleteListener mCompleteListener) {
        this.mCompleteListener = mCompleteListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogTheme);
    }

}
