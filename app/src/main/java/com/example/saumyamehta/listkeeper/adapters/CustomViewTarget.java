package com.example.saumyamehta.listkeeper.adapters;

import android.app.Activity;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.saumyamehta.listkeeper.widgets.BucketRecyclerView;
import com.github.amlcurran.showcaseview.targets.Target;

/**
 * Created by saumyamehta on 7/7/17.
 */

public class CustomViewTarget implements Target {

    private final View mView;
    private int offsetX;
    private int offsetY;

    public CustomViewTarget(View view) {
        mView = view;
    }

    public CustomViewTarget(int viewId, int offsetX, int offsetY, Activity activity) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        mView = activity.findViewById(viewId);
    }

    @Override
    public Point getPoint() {
        int[] location = new int[2];

        mView.getLocationInWindow(location);
        int x = location[0] + mView.getWidth() / 4 + offsetX;
        int y = location[1] + mView.getHeight() / 8 + offsetY;
        return new Point(x, y);
    }
}