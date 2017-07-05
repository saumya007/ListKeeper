package com.example.saumyamehta.listkeeper.extras;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

import com.example.saumyamehta.listkeeper.services.NotificationService;

import java.util.List;

/**
 * Created by saumyamehta on 6/22/17.
 */

public class Util {
    public static void showViews(List<View> viewList) {
        for (View view : viewList) {
            view.setVisibility(View.VISIBLE);
        }
    }

    public static void hideViews(List<View> views) {
        for (View view : views) {
            view.setVisibility(View.GONE);
        }
    }
    public static void setBackground(View v, Drawable drawable)
    {
        if(moreThanjellybean())
        {
            v.setBackground(drawable);
        }
        else
        {
            v.setBackgroundDrawable(drawable);
        }
    }
    public static boolean moreThanjellybean()
    {
        return Build.VERSION.SDK_INT >15;
    }

    public static void scheduleAlarms(Context context) {
        AlarmManager mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent mIntent  = new Intent(context, NotificationService.class);
        PendingIntent mPending = PendingIntent.getService(context,100,mIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,1000,5000,mPending);
    }
}
