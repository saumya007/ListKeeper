package com.example.saumyamehta.listkeeper.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.saumyamehta.listkeeper.R;

/**
 * Created by saumyamehta on 6/22/17.
 */

public class AdapterDrops extends RecyclerView.Adapter<AdapterDrops.DropHolder> {
    private LayoutInflater mInflater;

    public AdapterDrops(Context context) {
        mInflater = LayoutInflater.from(context);


    }

    @Override
    public DropHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = mInflater.inflate(R.layout.row_drop, parent, false);
        DropHolder mDropHolder = new DropHolder(rowView);
        return mDropHolder;
    }

    @Override
    public void onBindViewHolder(DropHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class DropHolder extends RecyclerView.ViewHolder {
        private ImageView mIconimage;
        private TextView mTextwhat, mTextwhen;

        public DropHolder(View itemView) {
            super(itemView);
            mIconimage = (ImageView) itemView.findViewById(R.id.iv_drop);
            mTextwhat = (TextView) itemView.findViewById(R.id.tv_what);
            mTextwhen = (TextView) itemView.findViewById(R.id.tv_when);
        }
    }
}
