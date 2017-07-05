package com.example.saumyamehta.listkeeper.services;

import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import android.content.Context;

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
        if(mUser!=null)
        {
            mDatabase = FirebaseDatabase.getInstance().getReference().child(mUser.getDisplayName()).child("Drops");
        }
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        for (Drops current : mResults) {
            if (isNotificationNeeded(current.getAdded(), current.getWhen())) {
                fireNotification(current);
            }
        }
    }

    private void fireNotification(Drops mDrops) {
        String message = "Congrats you are nearing your goal"+ mDrops.getWhat();
        PugNotification.with(this)
                .load()
                .title("Achievement")
                .message(message)
                .bigTextStyle("You are about to complete your goal")
                .smallIcon(R.drawable.ic_drop)
                .flags(Notification.DEFAULT_ALL)
                .autoCancel(true)
                .click(MainActivity.class)
                .simple()
                .build();
    }

    private boolean isNotificationNeeded(long added, long when) {
        long now = System.currentTimeMillis();
        if (now > when) {
            return false;
        } else {
            long diff = (long) (0.9 * (when - now));
            return (now > (added + diff)) ? true : false;
        }
    }


}
