package com.example.saumyamehta.listkeeper.services;

import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;

import com.example.saumyamehta.listkeeper.CustomTypefaceSpan;
import com.example.saumyamehta.listkeeper.GoogleSignIn;
import com.example.saumyamehta.listkeeper.MainActivity;
import com.example.saumyamehta.listkeeper.R;
import com.example.saumyamehta.listkeeper.beans.Drops;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

import br.com.goncalves.pugnotification.notification.PugNotification;

public class NotificationService extends IntentService {
    private DatabaseReference mDatabase;
    ArrayList<Drops> mResults;
    private FirebaseUser mUser;

    public NotificationService() {
        super("NotificationService");
        mResults = new ArrayList<>();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase.getInstance().getReference().keepSynced(true);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (mUser != null) {
            mDatabase = FirebaseDatabase.getInstance().getReference().child(mUser.getDisplayName()).child("Drops");
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot mDatasnapshot : dataSnapshot.getChildren()) {
                        boolean b = Boolean.parseBoolean(mDatasnapshot.child("completed").toString());
                        if (!b) {
                            Drops mDrops = new Drops(mDatasnapshot.child("what").getValue().toString(), Long.parseLong(mDatasnapshot.child("added").getValue().toString()),
                                    Long.parseLong(mDatasnapshot.child("when").getValue().toString()), Boolean.parseBoolean(mDatasnapshot.child("completed").getValue().toString()));
                            mResults.add(mDrops);
                        }
                    }
                    for (Drops current : mResults) {
                        if (isNotificationNeeded(current.getAdded(), current.getWhen())) {
                            fireNotification(current);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }

    private void fireNotification(Drops mDrops) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mDrops.getWhen());
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Thin.ttf");
        SpannableString mNewTitle = new SpannableString(mDrops.getWhat().toString());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mNewTitle.setSpan(new StyleSpan(Typeface.BOLD), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mNewTitle.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getApplicationContext(),R.color.btn_add_selected)),0,mNewTitle.length(),Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        String message = "Congrats you are nearing your goal " + mNewTitle + " timed at : "+ calendar.get(Calendar.HOUR_OF_DAY) + " : " + calendar.get(Calendar.MINUTE);
        PugNotification.with(this)
                .load()
                .title("Achievement")
                .message(message)
                .color(R.color.btn_add_selected)
                .bigTextStyle(message)
                .smallIcon(R.drawable.ic_drop)
                .flags(Notification.DEFAULT_ALL)
                .autoCancel(true)
                .click(GoogleSignIn.class)
                .simple()
                .build();
    }

    private boolean isNotificationNeeded(long added, long when) {
        long now = System.currentTimeMillis();
        if (now > when) {
            return false;
        } else {
            return (now >=(when-600000)) ? true : false;
        }
    }


}
