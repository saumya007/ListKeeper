package com.example.saumyamehta.listkeeper.widgets;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;

import com.example.saumyamehta.listkeeper.extras.Util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by saumyamehta on 6/22/17.
 */

public class BucketRecyclerView extends RecyclerView {
    private List<View> mEmptyViews = Collections.emptyList();
    private List<View> mNonEmptyViews = Collections.emptyList();
    private AdapterDataObserver mAdapterObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            // super.onChanged();
            toggleViews();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            // super.onItemRangeChanged(positionStart, itemCount);
            toggleViews();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            // super.onItemRangeChanged(positionStart, itemCount, payload);
            toggleViews();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            // super.onItemRangeInserted(positionStart, itemCount);
            toggleViews();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            //super.onItemRangeRemoved(positionStart, itemCount);
            toggleViews();
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            //super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            toggleViews();
        }
    };

    public  void toggleViews() {
        if (getAdapter() != null && !mEmptyViews.isEmpty() && !mNonEmptyViews.isEmpty()) {
            if (getAdapter().getItemCount() == 0) {
                Util.showViews(mEmptyViews);
                setVisibility(GONE);
                Util.hideViews(mNonEmptyViews);
            } else {
                Util.showViews(mNonEmptyViews);
                setVisibility(VISIBLE);
                Util.hideViews(mEmptyViews);
            }
        }
    }

    public BucketRecyclerView(Context context) {
        super(context);
    }

    public BucketRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BucketRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(mAdapterObserver);
        }
        mAdapterObserver.onChanged();
    }

    public void hideIfempty(View... views) {
        mNonEmptyViews = Arrays.asList(views);
    }

    public void showIfempty(View... mEmptyviews) {
        mEmptyViews = Arrays.asList(mEmptyviews);
    }
}
