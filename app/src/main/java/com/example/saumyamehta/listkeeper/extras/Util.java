package com.example.saumyamehta.listkeeper.extras;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;

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

}
