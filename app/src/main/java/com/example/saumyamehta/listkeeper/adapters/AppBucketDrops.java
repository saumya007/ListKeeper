package com.example.saumyamehta.listkeeper.adapters;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by saumyamehta on 6/27/17.
 */

public class AppBucketDrops extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
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
}
