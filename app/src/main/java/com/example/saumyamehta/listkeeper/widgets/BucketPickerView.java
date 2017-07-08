package com.example.saumyamehta.listkeeper.widgets;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.saumyamehta.listkeeper.R;
import com.example.saumyamehta.listkeeper.adapters.AppBucketDrops;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by saumyamehta on 6/28/17.
 */

public class BucketPickerView extends LinearLayout implements View.OnTouchListener {
    private TextView mTextDate, mTextMonth, mTextYear, mTextHour, mTextMin;
    private Calendar mCalendar;
    private SimpleDateFormat mFormatter;
    public static final int LEFT = 0;
    public static final int TOP = 1;
    public static final int RIGHT = 2;
    public static final int BOTTOM = 3;
    private boolean mIncrement;
    private boolean mDecrement;
    private int MESSAGE_WHAT = 123;
    private int mCurrentactive;
    public static final int DELAY = 250;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (mIncrement) {
                increment(mCurrentactive);
            }
            if (mDecrement) {
                decrement(mCurrentactive);
            }
            if (mIncrement || mDecrement) {
                mHandler.sendEmptyMessageDelayed(MESSAGE_WHAT, DELAY);
            }
            return true;
        }
    });

    public BucketPickerView(Context context) {
        super(context);
        init(context);
    }

    public BucketPickerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BucketPickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.bucket_picker_view, this);
        mCalendar = Calendar.getInstance();
        mFormatter = new SimpleDateFormat("MMM");
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTextDate = (TextView) this.findViewById(R.id.tv_date);
        mTextMonth = (TextView) this.findViewById(R.id.tv_month);
        mTextYear = (TextView) this.findViewById(R.id.tv_year);
        mTextHour = (TextView) this.findViewById(R.id.tv_hours);
        mTextMin = (TextView) this.findViewById(R.id.tv_mins);
        AppBucketDrops.setRalewayThin(getContext(), mTextDate, mTextMonth, mTextYear, mTextHour, mTextMin);
        int date = mCalendar.get(Calendar.DATE);
        int month = mCalendar.get(Calendar.MONTH);
        int year = mCalendar.get(Calendar.YEAR);
        int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
        int min = mCalendar.get(Calendar.MINUTE);
        update(date, month, year, hour, min, 0);
        mTextDate.setOnTouchListener(this);
        mTextMonth.setOnTouchListener(this);
        mTextYear.setOnTouchListener(this);
        mTextHour.setOnTouchListener(this);
        mTextMin.setOnTouchListener(this);
    }

    private void update(int date, int month, int year, int hour, int min, int sec) {
        mCalendar.set(Calendar.DATE, date);
        mCalendar.set(Calendar.MONTH, month);
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MINUTE, min);
        mCalendar.set(Calendar.HOUR_OF_DAY, hour);
        mCalendar.set(Calendar.SECOND, sec);
        mTextMonth.setText(mFormatter.format(mCalendar.getTime()));
        mTextDate.setText(date + "");
        mTextYear.setText(year + "");
        mTextHour.setText(hour + "");
        mTextMin.setText(min + "");
    }

    public long getTime() {
        return mCalendar.getTimeInMillis();
    }
    public String getExactTime()
    {
        return mTextHour.getText().toString() + " : "+ mTextHour.getText().toString();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.tv_date:
                processForEvents(mTextDate, event);
                break;
            case R.id.tv_month:
                processForEvents(mTextMonth, event);
                break;
            case R.id.tv_year:
                processForEvents(mTextYear, event);
                break;
            case R.id.tv_hours:
                processForEvents(mTextHour, event);
                break;
            case R.id.tv_mins:
                processForEvents(mTextMin, event);
                break;
        }

        return true;
    }

    private void processForEvents(TextView mTextView, MotionEvent motionEvent) {
        mCurrentactive = mTextView.getId();
        Drawable[] drawables = mTextView.getCompoundDrawables();
        if (hasDrawableTop(drawables) && hasDrawableBottom(drawables)) {
            Rect topBounds = drawables[TOP].getBounds();
            Rect bottomBounds = drawables[BOTTOM].getBounds();
            float x = motionEvent.getX();
            float y = motionEvent.getY();
            if (topDrawableHit(mTextView, topBounds.height(), x, y)) {
                if (isActionDown(motionEvent)) {
                    increment(mCurrentactive);
                    mIncrement = true;
                    mHandler.removeMessages(MESSAGE_WHAT);
                    mHandler.sendEmptyMessageDelayed(MESSAGE_WHAT, DELAY);
                    toggleDrawable(mTextView, true);
                } else if (isActionUp(motionEvent)) {
                    mIncrement = false;
                    toggleDrawable(mTextView, false);
                }

            } else if (bottomDraawableHit(mTextView, bottomBounds.height(), x, y)) {

                if (isActionDown(motionEvent)) {
                    decrement(mCurrentactive);
                    mDecrement = true;
                    mHandler.removeMessages(MESSAGE_WHAT);
                    mHandler.sendEmptyMessageDelayed(MESSAGE_WHAT, DELAY);
                    toggleDrawable(mTextView, true);

                } else if (isActionUp(motionEvent)) {
                    mDecrement = false;
                    toggleDrawable(mTextView, false);

                }

            } else {
                mIncrement = false;
                mDecrement = false;
                toggleDrawable(mTextView, false);

            }
        }

    }

    private boolean isActionUp(MotionEvent mEvent) {
        return mEvent.getAction() == MotionEvent.ACTION_UP || mEvent.getAction() == MotionEvent.ACTION_CANCEL;

    }

    private boolean isActionDown(MotionEvent motionEvent) {
        return motionEvent.getAction() == MotionEvent.ACTION_DOWN;
    }

    private void increment(int id) {
        switch (id) {
            case R.id.tv_date:
                mCalendar.add(Calendar.DATE, 1);
                break;
            case R.id.tv_month:
                mCalendar.add(Calendar.MONTH, 1);
                break;
            case R.id.tv_year:
                mCalendar.add(Calendar.YEAR, 1);
                break;
            case R.id.tv_hours:
                mCalendar.add(Calendar.HOUR_OF_DAY, 1);
                break;
            case R.id.tv_mins:
                mCalendar.add(Calendar.MINUTE, 1);
                break;
        }
        set(mCalendar);
    }

    private void set(Calendar mCalendar) {
        int date = mCalendar.get(Calendar.DATE);
        int month = mCalendar.get(Calendar.MONTH);
        int year = mCalendar.get(Calendar.YEAR);
        int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
        int min = mCalendar.get(Calendar.MINUTE);
        mTextDate.setText(date + "");
        mTextYear.setText(year + "");
        mTextMonth.setText(mFormatter.format(mCalendar.getTime()));
        mTextHour.setText(hour + "");
        mTextMin.setText(min + "");
    }

    private void decrement(int id) {
        switch (id) {
            case R.id.tv_date:
                mCalendar.add(Calendar.DATE, -1);
                break;
            case R.id.tv_month:
                mCalendar.add(Calendar.MONTH, -1);
                break;
            case R.id.tv_year:
                mCalendar.add(Calendar.YEAR, -1);
                break;
            case R.id.tv_hours:
                mCalendar.add(Calendar.HOUR_OF_DAY, -1);
                break;
            case R.id.tv_mins:
                mCalendar.add(Calendar.MINUTE, -1);
                break;
        }
        set(mCalendar);

    }

    private boolean topDrawableHit(TextView mTextView, int drawableHeight, float x, float y) {
        int xmin = mTextView.getPaddingLeft();
        int xmax = mTextView.getWidth() - mTextView.getPaddingRight();
        int ymin = mTextView.getPaddingTop();
        int ymax = mTextView.getPaddingTop() + drawableHeight;

        return x > xmin && x < xmax && y > ymin && y < ymax;
    }

    private boolean bottomDraawableHit(TextView mTextView, int drawableHeight, float x, float y) {
        int xmin = mTextView.getPaddingLeft();
        int xmax = mTextView.getWidth() - mTextView.getPaddingRight();
        int ymax = mTextView.getHeight() - mTextView.getPaddingBottom();
        int ymin = ymax - drawableHeight;

        return x > xmin && x < xmax && y > ymin && y < ymax;
    }

    private boolean hasDrawableTop(Drawable[] drawables) {
        return drawables[TOP] != null;
    }

    private boolean hasDrawableBottom(Drawable[] drawables) {
        return drawables[BOTTOM] != null;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("super", super.onSaveInstanceState());
        bundle.putInt("date", mCalendar.get(Calendar.DATE));
        bundle.putInt("month", mCalendar.get(Calendar.MONTH));
        bundle.putInt("year", mCalendar.get(Calendar.YEAR));
        bundle.putInt("hour", mCalendar.get(Calendar.HOUR_OF_DAY));
        bundle.putInt("min", mCalendar.get(Calendar.MINUTE));
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Parcelable) {
            Bundle bundle = (Bundle) state;
            state = bundle.getParcelable("super");
            int date = bundle.getInt("date");
            int month = bundle.getInt("month");
            int year = bundle.getInt("year");
            int hour = bundle.getInt("hour");
            int min = bundle.getInt("min");
            update(date, month, year, hour, min, 0);
        }
        super.onRestoreInstanceState(state);
    }

    private void toggleDrawable(TextView mTextView, boolean pressed) {
        if (pressed) {
            if (mIncrement) {
                mTextView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.up_pressed, 0, R.drawable.down_normal);
            }
            if (mDecrement) {
                mTextView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.up_normal, 0, R.drawable.down_pressed);
            }

        } else {
            mTextView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.up_normal, 0, R.drawable.down_normal);
        }
    }
}
