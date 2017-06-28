package com.example.saumyamehta.listkeeper.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.saumyamehta.listkeeper.MainActivity;
import com.example.saumyamehta.listkeeper.R;
import com.example.saumyamehta.listkeeper.beans.Drops;
import com.example.saumyamehta.listkeeper.extras.Util;
import com.example.saumyamehta.listkeeper.widgets.BucketRecyclerView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by saumyamehta on 6/22/17.
 */

public class AdapterDrops extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements SwipeListener {
    private ResetListener mResetListener;
    private LayoutInflater mInflater;
    private ArrayList<Drops> mResults = new ArrayList<>();
    private ArrayList<Drops> mResults1 = new ArrayList<>();
    public static final int ITEM = 0;
    public static final int FOOTER = 2;
    public static final int NO_ITEMS = 1;
    public static final int COUNT_FOOTER = 1;
    public static final int COUNT_NO_ITEMS = 1;
    private AdapterListener mAddlistener;
    private DatabaseReference mDatabase;
    private boolean before = false;
    private MarkListener markListener;
    private int mFilteroption;
    private BucketRecyclerView mRecycler;
    private Context context;

    public AdapterDrops(Context context, ArrayList<Drops> results, DatabaseReference mDatabase) {
        mInflater = LayoutInflater.from(context);
        mResults = results;
        this.mDatabase = mDatabase;
    }

    public AdapterDrops(BucketRecyclerView mrecyclerview, Context context, ArrayList<Drops> results, AdapterListener mAddlistener, MarkListener markListener, ResetListener mResetListener) {
        mInflater = LayoutInflater.from(context);
        mResults = results;
        this.mAddlistener = mAddlistener;
        this.markListener = markListener;
        mFilteroption = AppBucketDrops.load(context);
        mRecycler = mrecyclerview;
        this.context = context;
        this.mResetListener = mResetListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == FOOTER) {
            View rowView = mInflater.inflate(R.layout.footer, parent, false);
            return new FooterHolder(rowView);
        } else if (viewType == NO_ITEMS) {
            View rowView = mInflater.inflate(R.layout.no_items, parent, false);
            return new NoItemsHolder(rowView);
        } else {
            View rowView = mInflater.inflate(R.layout.row_drop, parent, false);
            return new DropHolder(rowView, markListener);

        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DropHolder) {
            Drops mDrops = mResults.get(position);
            DropHolder mDropholder = (DropHolder) holder;
            mDropholder.setWhat(mDrops.getWhat());
            mDropholder.setWhen(mDrops.getWhen());
            mDropholder.setBackground(mDrops.isCompleted());
        }
    }

    @Override
    public long getItemId(int position) {
        if (position < mResults.size()) {
            return mResults.get(position).getAdded();
        }
        return RecyclerView.NO_ID;
    }

    public void setAddlistener(AdapterListener mAddlistener) {
        this.mAddlistener = mAddlistener;
    }

    @Override
    public int getItemCount() {
        if (!mResults.isEmpty()) {
            return mResults.size() + COUNT_FOOTER;
        } else {
            if (mFilteroption == Filter.NONE || mFilteroption == Filter.LEAST_TIME_LEFT || mFilteroption == Filter.MOST_TIME_LEFT) {
                return 0;
            } else {
                return COUNT_FOOTER + COUNT_NO_ITEMS;
            }
        }
    }

    @Override
    public void onSwipe(final int position, final RecyclerView.ViewHolder viewholder) {
        Log.e("oh", "oh1");
        if (position < mResults.size()) {
            final boolean[] removed = {false};
            Log.e("oh", "oh");
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Drops");
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (ds.child("what").getValue().toString().equals(mResults.get(position).getWhat())) {
                            Log.e("hola", position + "pos" + ds.child("what").getValue().toString() + "what" + mResults.get(position).getWhat() +
                                    "res");
                            ds.getRef().removeValue();
                            Log.e("ref", ds.getRef() + "");

                        }

                    }
                    mRecycler.removeViewAt(position);
                    mResults.remove(position);
                    notifyItemRemoved(position);
                    resetFilterifempty();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }

    private void resetFilterifempty() {
        if (mResults.isEmpty() && (mFilteroption == Filter.COMPLETED || mFilteroption == Filter.INCOMPLETE)) {
            mResetListener.onReset();
        }
    }

    public void markComplete(final int position) {
        if (position < mResults.size()) {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Drops");
            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (ds.child("what").getValue().toString().equals(mResults.get(position).getWhat())) {
                            Log.e("hola", position + "pos" + ds.child("what").getValue().toString() + "what" + mResults.get(position).getWhat() +
                                    "res");
                            ds.child("completed").getRef().setValue("true");

                            Log.e("ahaha", "hre");
                        }
                    }
                    mResults.get(position).setCompleted(true);
                    notifyItemChanged(position);
                    int filteroption = AppBucketDrops.load(context);
                    if (filteroption == Filter.INCOMPLETE) {
                        mRecycler.removeViewAt(position);
                        mResults.remove(position);
                        notifyDataSetChanged();
                        mRecycler.invalidate();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }
    }

    public static class DropHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mIconimage;
        private TextView mTextwhat, mTextwhen;
        private MarkListener mMarklistener;
        Context mContext;
        View mItemView;

        public DropHolder(View itemView, MarkListener markListener) {
            super(itemView);
            mContext = itemView.getContext();
            mIconimage = (ImageView) itemView.findViewById(R.id.iv_drop);
            mTextwhat = (TextView) itemView.findViewById(R.id.tv_what);
            mTextwhen = (TextView) itemView.findViewById(R.id.tv_when);
            itemView.setOnClickListener(this);
            mMarklistener = markListener;
            mItemView = itemView;
        }

        @Override
        public void onClick(View v) {
            mMarklistener.onMark(getAdapterPosition());
        }

        public void setWhat(String what) {
            mTextwhat.setText(what);
        }

        public void setBackground(boolean completed) {
            Drawable drawable;
            if (completed) {
                drawable = ContextCompat.getDrawable(mContext, R.color.bg_row_drop_completed);

            } else {
                drawable = ContextCompat.getDrawable(mContext, R.color.bg_drop_row_dark);

            }
            Util.setBackground(mItemView, drawable);

        }

        public void setWhen(long when) {
            mTextwhen.setText(DateUtils.getRelativeTimeSpanString(when, System.currentTimeMillis(), DateUtils.DAY_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL));
        }
    }

    public class NoItemsHolder extends RecyclerView.ViewHolder {

        public NoItemsHolder(View itemView) {
            super(itemView);
        }

    }

    public class FooterHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Button mAddbtn;

        public FooterHolder(View itemView) {
            super(itemView);
            mAddbtn = (Button) itemView.findViewById(R.id.footer_btn);
            mAddbtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mAddlistener.add();
        }
    }

    @Override
    public int getItemViewType(int position) {

        if (!mResults.isEmpty()) {
            if (position < mResults.size()) {
                return ITEM;
            } else {
                return FOOTER;
            }

        } else {
            if (mFilteroption == Filter.COMPLETED || mFilteroption == Filter.INCOMPLETE) {
                if (position == 0) {
                    return NO_ITEMS;
                } else {
                    return FOOTER;
                }
            } else {
                return ITEM;
            }
        }
    }
}
