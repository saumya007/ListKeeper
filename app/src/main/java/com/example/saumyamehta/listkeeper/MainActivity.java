package com.example.saumyamehta.listkeeper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
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
import com.example.saumyamehta.listkeeper.extras.Util;
import com.example.saumyamehta.listkeeper.services.NotificationService;
import com.example.saumyamehta.listkeeper.widgets.BucketRecyclerView;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import br.com.goncalves.pugnotification.interfaces.ImageLoader;

import static android.support.v4.app.DialogFragment.STYLE_NO_FRAME;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "hahah";
    private Toolbar mToolbar;
    private Button mButton;
    private BucketRecyclerView mBucketRecyclerView;
    private DatabaseReference mDatabase;
    private ArrayList<Drops> mResults;
    private AdapterDrops mAdapterdrops;
    private View mEmptyview;
    private boolean mark;

    private FirebaseUser mUser;
    private Menu menu;

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
        FirebaseDatabase.getInstance().getReference().keepSynced(true);
        mResults = new ArrayList<>();
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView mTitle = (TextView) mToolbar.findViewById(R.id.toolbar_title);
        AppBucketDrops.setRalewayThin(getApplicationContext(), mTitle);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        mButton = (Button) findViewById(R.id.button_add);
        mBucketRecyclerView = (BucketRecyclerView) findViewById(R.id.recycler);
        mBucketRecyclerView.addItemDecoration(new Divider(this, LinearLayoutManager.VERTICAL));
        mEmptyview = findViewById(R.id.empty_drops);
        LinearLayoutManager mLinearlayoutmanager = new LinearLayoutManager(this);
        mBucketRecyclerView.setLayoutManager(mLinearlayoutmanager);
        mBucketRecyclerView.hideIfempty(mToolbar);
        mBucketRecyclerView.showIfempty(mEmptyview);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences sharedPreferences = getSharedPreferences("My Pref", MODE_PRIVATE);
        int mFilteroption = AppBucketDrops.load(this);
        loadResults(mFilteroption);
        if (sharedPreferences == null) {
            Log.e("mRes", mResults.size() + "");
            if (mUser != null) {
                mDatabase = FirebaseDatabase.getInstance().getReference().child(mUser.getDisplayName()).child("Drops");
            }
            mDatabase.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Log.d("Yo", "I am here");
                    Drops mDrops = new Drops(dataSnapshot.child("what").getValue().toString(), Long.parseLong(dataSnapshot.child("added").getValue().toString()),
                            Long.parseLong(dataSnapshot.child("when").getValue().toString()), Boolean.parseBoolean(dataSnapshot.child("completed").getValue().toString()));
                    for (int i = 0; i < mResults.size(); i++) {
                        if (!mResults.get(i).getWhat().toString().equals(mDrops.getWhat().toString())) {
                            mResults.add(mDrops);
                            mAdapterdrops.notifyDataSetChanged();
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
            mAdapterdrops = new AdapterDrops(mBucketRecyclerView, this, mResults, mAddListener, mMarkListener, mResetListener);

            mBucketRecyclerView.setAdapter(mAdapterdrops);
            mAdapterdrops.setAddlistener(mAddListener);
            SimpleTouchCallBack mSimpletouchcallback = new SimpleTouchCallBack(mAdapterdrops);
            ItemTouchHelper mItemtouchhelper = new ItemTouchHelper(mSimpletouchcallback);
            mItemtouchhelper.attachToRecyclerView(mBucketRecyclerView);

            initBackgroundImage();
        } else if (mFilteroption == Filter.NONE) {
            if (mUser != null) {
                mDatabase = FirebaseDatabase.getInstance().getReference().child(mUser.getDisplayName()).child("Drops");
            }
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

        Util.scheduleAlarms(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Thin.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mNewTitle.setSpan(new StyleSpan(Typeface.BOLD), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        for (int i = 0; i < menu.size(); i++) {
            MenuItem mi = menu.getItem(i);

            applyFontToMenuItem(mi);
        }
        this.menu = menu;
        if (mUser != null) {
            Glide.with(this).load(mUser.getPhotoUrl()).asBitmap().into(new SimpleTarget<Bitmap>(100, 100) {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                    menu.findItem(R.id.profile).setIcon(new BitmapDrawable(getResources(), resource));
                }
            });
        }
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
            case R.id.profile:
                logout();
            default:
                handled = false;
                break;
        }
        AppBucketDrops.save(getApplicationContext(), mFilteroption);
        loadResults(mFilteroption);
        return handled;
    }

    private void logout() {

        FirebaseAuth.getInstance().signOut();
        Intent i = new Intent(MainActivity.this, GoogleSignIn.class);
        startActivity(i);
    }

    private void loadResults(int filterOption) {
        switch (filterOption) {

            case Filter.LEAST_TIME_LEFT:
                if (mUser != null) {
                    mDatabase = FirebaseDatabase.getInstance().getReference().child(mUser.getDisplayName()).child("Drops");
                }
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
                if (mUser != null) {
                    mDatabase = FirebaseDatabase.getInstance().getReference().child(mUser.getDisplayName()).child("Drops");
                }
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

                if (mUser != null) {
                    mDatabase = FirebaseDatabase.getInstance().getReference().child(mUser.getDisplayName()).child("Drops");
                }
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

                if (mUser != null) {
                    mDatabase = FirebaseDatabase.getInstance().getReference().child(mUser.getDisplayName()).child("Drops");
                }
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
                if (mUser != null) {
                    mDatabase = FirebaseDatabase.getInstance().getReference().child(mUser.getDisplayName()).child("Drops");
                }
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            Drops drops = new Drops(ds.child("what").getValue().toString(), Long.parseLong(ds.child("added").getValue().toString()), Long.parseLong(ds.child("when").getValue().toString()), Boolean.parseBoolean(ds.child("completed").getValue().toString()));
                            Log.e("Drops", drops.getWhat());
                            if (mResults.size() > 0) {
                                for (int i = 0; i < mResults.size(); i++) {
                                    if (!mResults.get(i).getWhat().equals(drops.getWhat())) {
                                        mResults.add(drops);
                                        mAdapterdrops = new AdapterDrops(mBucketRecyclerView, getApplicationContext(), mResults, mAddListener, mMarkListener, mResetListener);
                                        mAdapterdrops.setHasStableIds(true);

                                        mBucketRecyclerView.setAdapter(mAdapterdrops);
                                        mAdapterdrops.setAddlistener(mAddListener);
                                        SimpleTouchCallBack mSimpletouchcallback = new SimpleTouchCallBack(mAdapterdrops);
                                        ItemTouchHelper mItemtouchhelper = new ItemTouchHelper(mSimpletouchcallback);
                                        mItemtouchhelper.attachToRecyclerView(mBucketRecyclerView);
                                        mAdapterdrops.notifyDataSetChanged();
                                    }
                                }
                            }
                        }
                        Log.e("count", mAdapterdrops.getItemCount() + "" + "size" + mResults.size());

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                mark = true;
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

    }


}
