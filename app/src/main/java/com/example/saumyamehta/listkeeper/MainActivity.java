package com.example.saumyamehta.listkeeper;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.saumyamehta.listkeeper.adapters.AdapterDrops;
import com.example.saumyamehta.listkeeper.adapters.AdapterListener;
import com.example.saumyamehta.listkeeper.adapters.AppBucketDrops;
import com.example.saumyamehta.listkeeper.adapters.CompleteListener;
import com.example.saumyamehta.listkeeper.adapters.Divider;
import com.example.saumyamehta.listkeeper.adapters.Filter;
import com.example.saumyamehta.listkeeper.adapters.MarkListener;
import com.example.saumyamehta.listkeeper.adapters.ResetListener;
import com.example.saumyamehta.listkeeper.adapters.SimpleTouchCallBack;
import com.example.saumyamehta.listkeeper.beans.Drops;
import com.example.saumyamehta.listkeeper.widgets.BucketRecyclerView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private Button mButton;
    private BucketRecyclerView mBucketRecyclerView;
    private DatabaseReference mDatabase;
    private ArrayList<Drops> mResults;
    private AdapterDrops mAdapterdrops;
    private View mEmptyview;
    private boolean mark;
    private AdapterListener mAddListener = new AdapterListener() {
        @Override
        public void add() {
            showDialogAdd();
        }
    };
    private MarkListener mMarkListener = new MarkListener() {
        @Override
        public void onMark(int position) {
            showDialogMark(position);
        }
    };
    private CompleteListener mCompleteListener = new CompleteListener() {
        @Override
        public void onComplete(int position) {
            mAdapterdrops.markComplete(position);

        }
    };
    private ResetListener mResetListener = new ResetListener() {
        @Override
        public void onReset() {
            AppBucketDrops.save(MainActivity.this, Filter.NONE);
            loadResults(Filter.NONE);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mResults = new ArrayList<>();
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mButton = (Button) findViewById(R.id.button_add);
        mBucketRecyclerView = (BucketRecyclerView) findViewById(R.id.recycler);
        mBucketRecyclerView.addItemDecoration(new Divider(this, LinearLayoutManager.VERTICAL));
        mEmptyview = findViewById(R.id.empty_drops);
        LinearLayoutManager mLinearlayoutmanager = new LinearLayoutManager(this);

        mBucketRecyclerView.setLayoutManager(mLinearlayoutmanager);
        mBucketRecyclerView.hideIfempty(mToolbar);
        mBucketRecyclerView.showIfempty(mEmptyview);
        SharedPreferences sharedPreferences = getSharedPreferences("My Pref", MODE_PRIVATE);
        int mFilteroption = AppBucketDrops.load(this);
        loadResults(mFilteroption);
        if (sharedPreferences == null) {
            Log.e("mRes", mResults.size() + "");
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Drops");
            mDatabase.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Log.d("Yo", "I am here");
                    Drops mDrops = new Drops(dataSnapshot.child("what").getValue().toString(), Long.parseLong(dataSnapshot.child("added").getValue().toString()),
                            Long.parseLong(dataSnapshot.child("when").getValue().toString()), Boolean.parseBoolean(dataSnapshot.child("completed").getValue().toString()));
                    if (!mResults.contains(mDrops)) {
                        mResults.add(mDrops);
                        mAdapterdrops.notifyDataSetChanged();
                        Log.d("Yo", "objects" + mDrops.getWhat());
                    }

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            mAdapterdrops = new AdapterDrops(mBucketRecyclerView, this, mResults, mAddListener, mMarkListener, mResetListener);

            mBucketRecyclerView.setAdapter(mAdapterdrops);
            mAdapterdrops.setAddlistener(mAddListener);
            SimpleTouchCallBack mSimpletouchcallback = new SimpleTouchCallBack(mAdapterdrops);
            ItemTouchHelper mItemtouchhelper = new ItemTouchHelper(mSimpletouchcallback);
            mItemtouchhelper.attachToRecyclerView(mBucketRecyclerView);

            initBackgroundImage();
        } else if (mFilteroption == Filter.NONE) {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Drops");
            mDatabase.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Log.d("Yo", "I am here");
                    if (mResults.size() > 0) {
                        Drops mDrops = new Drops(dataSnapshot.child("what").getValue().toString(), Long.parseLong(dataSnapshot.child("added").getValue().toString()),
                                Long.parseLong(dataSnapshot.child("when").getValue().toString()), Boolean.parseBoolean(dataSnapshot.child("completed").getValue().toString()));
                        int count = 0;
                        for (Drops drops : mResults) {

                            Log.e("ahhahaha", drops.getWhat() + "hulu" + mDrops.getWhat() + "");
                            if (!drops.getWhat().equals(mDrops.getWhat())) {
                                count++;
                            }
                            if (count >= mResults.size()) {
                                mResults.add(mDrops);
                                mAdapterdrops.notifyDataSetChanged();
                                Log.d("Yo", "objects" + mDrops.getWhat());
                                count = 0;
                            }
                        }
//                        if (!mResults.contains(mDrops)) {
//                            mResults.add(mDrops);
//                            Log.e("Size", mResults.size() + "");
//                            mAdapterdrops.notifyDataSetChanged();
//                            Log.d("Yo", "objects" + mDrops.getWhat());
//                        }
                    } else if (mResults.size() == 0 && mark) {
                        Drops mDrops = new Drops(dataSnapshot.child("what").getValue().toString(), Long.parseLong(dataSnapshot.child("added").getValue().toString()),
                                Long.parseLong(dataSnapshot.child("when").getValue().toString()), Boolean.parseBoolean(dataSnapshot.child("completed").getValue().toString()));
                        if (!mResults.contains(mDrops)) {
                            mResults.add(mDrops);
                            Log.e("Size", mResults.size() + "");
                            mAdapterdrops.notifyDataSetChanged();
                            Log.d("Yo", "objects" + mDrops.getWhat());
                        }
                    }

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            mAdapterdrops = new AdapterDrops(mBucketRecyclerView, getApplicationContext(), mResults, mAddListener, mMarkListener, mResetListener);
            mAdapterdrops.setHasStableIds(true);
            mBucketRecyclerView.setAdapter(mAdapterdrops);
            mAdapterdrops.setAddlistener(mAddListener);
            SimpleTouchCallBack mSimpletouchcallback = new SimpleTouchCallBack(mAdapterdrops);
            ItemTouchHelper mItemtouchhelper = new ItemTouchHelper(mSimpletouchcallback);
            mItemtouchhelper.attachToRecyclerView(mBucketRecyclerView);
            initBackgroundImage();
        }
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogAdd();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        boolean handled = true;
        int mFilteroption = Filter.NONE;
        switch (id) {
            case R.id.add_menu:
                showDialogAdd();
                break;
            case R.id.least_remaining:
                mFilteroption = Filter.LEAST_TIME_LEFT;
                break;
            case R.id.most_remaining:
                mFilteroption = Filter.MOST_TIME_LEFT;
                break;
            case R.id.completed:
                mFilteroption = Filter.COMPLETED;
                break;
            case R.id.incomplete:
                mFilteroption = Filter.INCOMPLETE;
                break;
            case R.id.none:
                mFilteroption = Filter.NONE;
            default:
                handled = false;
                break;
        }
        AppBucketDrops.save(getApplicationContext(), mFilteroption);
        loadResults(mFilteroption);
        return handled;
    }

    private void loadResults(int filterOption) {
        switch (filterOption) {

            case Filter.LEAST_TIME_LEFT:
                mDatabase = FirebaseDatabase.getInstance().getReference().child("Drops");
                mDatabase.orderByChild("when").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        mResults.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                            Log.e("ds", ds + "");
                            Drops drops = new Drops(ds.child("what").getValue().toString(), Long.parseLong(ds.child("added").getValue().toString()), Long.parseLong(ds.child("when").getValue().toString()), Boolean.parseBoolean(ds.child("completed").getValue().toString()));
                            mResults.add(drops);
                            mAdapterdrops = new AdapterDrops(mBucketRecyclerView, getApplicationContext(), mResults, mAddListener, mMarkListener, mResetListener);
                            mAdapterdrops.setHasStableIds(true);
                            mBucketRecyclerView.setAdapter(mAdapterdrops);
                            mAdapterdrops.setAddlistener(mAddListener);
                            SimpleTouchCallBack mSimpletouchcallback = new SimpleTouchCallBack(mAdapterdrops);
                            ItemTouchHelper mItemtouchhelper = new ItemTouchHelper(mSimpletouchcallback);
                            mItemtouchhelper.attachToRecyclerView(mBucketRecyclerView);
                            mAdapterdrops.notifyDataSetChanged();
                            LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
                            mBucketRecyclerView.setLayoutManager(llm);
                            llm.findLastVisibleItemPosition();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                break;
            case Filter.MOST_TIME_LEFT:
                mDatabase = FirebaseDatabase.getInstance().getReference().child("Drops");

                mDatabase.orderByChild("when").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        dataSnapshot.getRef().orderByChild("when");
                        mResults.clear();
                        ArrayList<Drops> descen = new ArrayList<Drops>();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Drops drops = new Drops(ds.child("what").getValue().toString(), Long.parseLong(ds.child("added").getValue().toString()), Long.parseLong(ds.child("when").getValue().toString()), Boolean.parseBoolean(ds.child("completed").getValue().toString()));

                            descen.add(drops);
                        }
                        for (int i = 0; i < descen.size(); i++) {
                            mResults.add(descen.get(descen.size() - i - 1));
                        }
                        mAdapterdrops = new AdapterDrops(mBucketRecyclerView, getApplicationContext(), mResults, mAddListener, mMarkListener, mResetListener);
                        mAdapterdrops.setHasStableIds(true);
                        mBucketRecyclerView.setAdapter(mAdapterdrops);


                        mAdapterdrops.setAddlistener(mAddListener);
                        SimpleTouchCallBack mSimpletouchcallback = new SimpleTouchCallBack(mAdapterdrops);
                        ItemTouchHelper mItemtouchhelper = new ItemTouchHelper(mSimpletouchcallback);
                        mItemtouchhelper.attachToRecyclerView(mBucketRecyclerView);
                        mAdapterdrops.notifyDataSetChanged();
                        initBackgroundImage();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                break;
            case Filter.COMPLETED:

                mDatabase = FirebaseDatabase.getInstance().getReference().child("Drops");
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mResults.clear();
                        boolean b = false;
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            b = Boolean.parseBoolean(ds.child("completed").getValue().toString());
                            Log.e("b", Boolean.parseBoolean(ds.child("completed").getValue().toString()) + "");
                            mAdapterdrops = new AdapterDrops(mBucketRecyclerView, getApplicationContext(), mResults, mAddListener, mMarkListener, mResetListener);
                            initBackgroundImage();
                            mAdapterdrops.setHasStableIds(true);

                            if (b) {
                                Drops drops = new Drops(ds.child("what").getValue().toString(), Long.parseLong(ds.child("added").getValue().toString()), Long.parseLong(ds.child("when").getValue().toString()), Boolean.parseBoolean(ds.child("completed").getValue().toString()));
                                mResults.add(drops);
                                mAdapterdrops.notifyDataSetChanged();
                            }
                            Log.e("Mres size", mResults.size() + "");
                            mBucketRecyclerView.setAdapter(mAdapterdrops);
                            mAdapterdrops.setAddlistener(mAddListener);
                            SimpleTouchCallBack mSimpletouchcallback = new SimpleTouchCallBack(mAdapterdrops);
                            ItemTouchHelper mItemtouchhelper = new ItemTouchHelper(mSimpletouchcallback);
                            mItemtouchhelper.attachToRecyclerView(mBucketRecyclerView);

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
//
                break;
            case Filter.INCOMPLETE:

                mDatabase = FirebaseDatabase.getInstance().getReference().child("Drops");
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        mResults.clear();

                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                            boolean b = Boolean.parseBoolean(ds.child("completed").getValue().toString());
                            mAdapterdrops = new AdapterDrops(mBucketRecyclerView, getApplicationContext(), mResults, mAddListener, mMarkListener, mResetListener);
                            mAdapterdrops.setHasStableIds(true);

                            Log.e("b", b + "");
                            if (!b) {

                                Drops drops = new Drops(ds.child("what").getValue().toString(), Long.parseLong(ds.child("added").getValue().toString()), Long.parseLong(ds.child("when").getValue().toString()), Boolean.parseBoolean(ds.child("completed").getValue().toString()));
                                mResults.add(drops);
                                mAdapterdrops.notifyDataSetChanged();
                            }
                            Log.e("Mres size", mResults.size() + "");
                            mBucketRecyclerView.setAdapter(mAdapterdrops);
                            mAdapterdrops.setAddlistener(mAddListener);
                            SimpleTouchCallBack mSimpletouchcallback = new SimpleTouchCallBack(mAdapterdrops);
                            ItemTouchHelper mItemtouchhelper = new ItemTouchHelper(mSimpletouchcallback);
                            mItemtouchhelper.attachToRecyclerView(mBucketRecyclerView);
                            initBackgroundImage();

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                break;
            case Filter.NONE:
                mResults.clear();
                mAdapterdrops = new AdapterDrops(mBucketRecyclerView, getApplicationContext(), mResults, mAddListener, mMarkListener, mResetListener);
                mAdapterdrops.setHasStableIds(true);

                mAdapterdrops.notifyDataSetChanged();
                mDatabase = FirebaseDatabase.getInstance().getReference().child("Drops");
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Drops drops = new Drops(ds.child("what").getValue().toString(), Long.parseLong(ds.child("added").getValue().toString()), Long.parseLong(ds.child("when").getValue().toString()), Boolean.parseBoolean(ds.child("completed").getValue().toString()));
                            if (!mResults.contains(drops)) {
                                mResults.add(drops);
                                mAdapterdrops = new AdapterDrops(mBucketRecyclerView, getApplicationContext(), mResults, mAddListener, mMarkListener, mResetListener);
                                mAdapterdrops.setHasStableIds(true);

                                mBucketRecyclerView.setAdapter(mAdapterdrops);
                                mAdapterdrops.setAddlistener(mAddListener);
                                SimpleTouchCallBack mSimpletouchcallback = new SimpleTouchCallBack(mAdapterdrops);
                                ItemTouchHelper mItemtouchhelper = new ItemTouchHelper(mSimpletouchcallback);
                                mItemtouchhelper.attachToRecyclerView(mBucketRecyclerView);
                                mAdapterdrops.notifyDataSetChanged();
                                mark = true;
                            }

                        }
                        Log.e("count", mAdapterdrops.getItemCount() + "" + "size" + mResults.size());

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                break;
            default:

                break;
        }
    }

    private void showDialogAdd() {
        DialogAdd addDialog = new DialogAdd();
        addDialog.show(getSupportFragmentManager(), "Add");
    }


    private void showDialogMark(int position) {
        DialogMark dialogMark = new DialogMark();
        Bundle mBundle = new Bundle();
        mBundle.putInt("POSITION", position);
        dialogMark.setArguments(mBundle);
        dialogMark.setCompleteListener(mCompleteListener);
        dialogMark.show(getSupportFragmentManager(), "Mark");
    }

    private void initBackgroundImage() {
        ImageView imgBack = (ImageView) findViewById(R.id.iv_background);

        Glide.with(this)
                .load(R.drawable.background)
                .centerCrop()
                .into(imgBack);
    }

    @Override
    public void onBackPressed() {
        mBucketRecyclerView.removeAllViewsInLayout();
        mBucketRecyclerView.removeAllViews();
        super.onBackPressed();
    }
}
