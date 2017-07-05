package com.example.saumyamehta.listkeeper.adapters;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by saumyamehta on 6/27/17.
 */

public class AppBucketDrops extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
    public static void save(Context context, int filterOption) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("filter", filterOption);
        editor.apply();
    }

    public static int load(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int filteroption = sharedPreferences.getInt("filter", Filter.NONE);
        Log.e("lol", "load: " + filteroption);
        return filteroption;
    }

    public static void setRalewayThin(Context context, TextView textView) {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(),"fonts/Raleway-Thin.ttf");
        textView.setTypeface(typeface);
    }

    public static void setRalewayThin(Context context, TextView... textView) {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(),"fonts/Raleway-Thin.ttf");
        for(TextView tv: textView)
        {
            tv.setTypeface(typeface);
        }
    }
}
