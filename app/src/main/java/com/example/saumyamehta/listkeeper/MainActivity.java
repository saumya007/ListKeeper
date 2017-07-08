package com.example.saumyamehta.listkeeper;

import android.app.Activity;
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
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import com.example.saumyamehta.listkeeper.adapters.ConfirmListener;
import com.example.saumyamehta.listkeeper.adapters.CustomShowcaseActivity;
import com.example.saumyamehta.listkeeper.adapters.CustomViewTarget;
import com.example.saumyamehta.listkeeper.adapters.Divider;
import com.example.saumyamehta.listkeeper.adapters.Filter;
import com.example.saumyamehta.listkeeper.adapters.MarkListener;
import com.example.saumyamehta.listkeeper.adapters.ResetListener;
import com.example.saumyamehta.listkeeper.adapters.SimpleTouchCallBack;
import com.example.saumyamehta.listkeeper.beans.Drops;
import com.example.saumyamehta.listkeeper.extras.Util;
import com.example.saumyamehta.listkeeper.services.NotificationService;
import com.example.saumyamehta.listkeeper.widgets.BucketRecyclerView;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.ShowcaseViewApi;
import com.github.amlcurran.showcaseview.targets.ActionViewTarget;
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
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;
import uk.co.deanwild.materialshowcaseview.target.ViewTarget;

import static android.support.v4.app.DialogFragment.STYLE_NO_FRAME;

public class MainActivity extends AppCompatActivity{
    private static final String TAG = "hahah";
    private static final long ROW_LIST =2;
    private static final long FIRST_LIST = 3;
    private static final long FOOTER_LIST =4 ;
    private static final long ADD_DROPS_MENU = 5;
    private static final long MENU_DROPS = 6;
    private static final long LOGOUT_DROPS = 7;
    private static final long COMPLETE = 8;
    private Toolbar mToolbar;
    private Button mButton;
    private BucketRecyclerView mBucketRecyclerView;
    private DatabaseReference mDatabase;
    private ArrayList<Drops> mResults;
    private AdapterDrops mAdapterdrops;
    private View mEmptyview;
    private boolean mark;
    private int counter = 0;

    private FirebaseUser mUser;
    private Menu menu;
    private ShowcaseView showcaseView;
    public static  ArrayList<Drops> mRes;
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

        @Override
        public void onConfirmed(int position) {
            showDialogConfirm(position);
        }
    };


    private CompleteListener mCompleteListener = new CompleteListener() {
        @Override
        public void onComplete(int position) {
            mAdapterdrops.markComplete(position);

        }
    };
    private ConfirmListener mConfirmListener = new ConfirmListener() {
        @Override
        public void onConfirm(int position) {
            mAdapterdrops.confirm(position);
        }

        @Override
        public void onCancel(int position) {
            mAdapterdrops.cancelled(position);
        }
    };
    private ResetListener mResetListener = new ResetListener() {
        @Override
        public void onReset() {
            AppBucketDrops.save(MainActivity.this, Filter.NONE);
            loadResults(Filter.NONE);
        }
    };
    private String SEQ_ID = "one";

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
        mRes = new ArrayList<>();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences sharedPreferences = getSharedPreferences("My Pref", MODE_PRIVATE);
        final Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Thin.ttf");
        int mFilteroption = AppBucketDrops.load(this);
        loadResults(mFilteroption);
        if (sharedPreferences == null) {
            Util.scheduleAlarms(this);

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
            SimpleTouchCallBack mSimpletouchcallback = new SimpleTouchCallBack(getApplicationContext(), mAdapterdrops);
            ItemTouchHelper mItemtouchhelper = new ItemTouchHelper(mSimpletouchcallback);
            mItemtouchhelper.attachToRecyclerView(mBucketRecyclerView);

            initBackgroundImage();
//

        } else if (mFilteroption == Filter.NONE) {

            if (mUser != null) {
                mDatabase = FirebaseDatabase.getInstance().getReference().child(mUser.getDisplayName()).child("Drops");
            }
            final Animation fade = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.fade);
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
                    }
                    else if (mResults.size() == 0 && mark) {

                            Drops mDrops = new Drops(dataSnapshot.child("what").getValue().toString(), Long.parseLong(dataSnapshot.child("added").getValue().toString()),
                                    Long.parseLong(dataSnapshot.child("when").getValue().toString()), Boolean.parseBoolean(dataSnapshot.child("completed").getValue().toString()));
                                mResults.add(mDrops);
                                Log.e("Size", mResults.size() + "");
                                mAdapterdrops.notifyDataSetChanged();
                                Log.d("Yo", "objects" + mDrops.getWhat());
                        if(mResults.size()>0) {
                            String fontPath = "fonts/Raleway-Thin.ttf";
                            Typeface mTypeFace = Typeface.createFromAsset(getAssets(), fontPath);

                            TextPaint titlePaint = new TextPaint();
                            titlePaint.setTextSize(150);
                            titlePaint.setColor(ContextCompat.getColor(getApplicationContext(), R.color.text_title));
                            titlePaint.setAntiAlias(true);
                            titlePaint.setTypeface(mTypeFace);
                            TextPaint textPaint = new TextPaint();
                            textPaint.setTextSize(55);
                            textPaint.setColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
                            textPaint.setAntiAlias(true);
                            textPaint.setTypeface(mTypeFace);
                            showcaseView = new ShowcaseView.Builder(MainActivity.this, true)
                                    .setTarget(new CustomViewTarget(R.id.recycler, -80, -90, MainActivity.this))
                                    .setContentTitle("Activities")
                                    .setContentText("Here the activities entered by users are stored here. Click on the task to complete it or swipe right to delete the task")
                                    .setContentTitlePaint(titlePaint)
                                    .setContentTextPaint(textPaint)
                                    .setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            showcaseView.hide();
                                            switch (counter) {
                                                case 0:
                                                    String fontPath = "fonts/Raleway-Thin.ttf";
                                                    Typeface mTypeFace = Typeface.createFromAsset(getAssets(), fontPath);
                                                    TextPaint titlePaint = new TextPaint();
                                                    titlePaint.setTextSize(150);
                                                    titlePaint.setColor(ContextCompat.getColor(getApplicationContext(), R.color.text_title));
                                                    titlePaint.setAntiAlias(true);
                                                    titlePaint.setTypeface(mTypeFace);
                                                    TextPaint textPaint = new TextPaint();
                                                    textPaint.setTextSize(55);
                                                    textPaint.setColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
                                                    textPaint.setAntiAlias(true);
                                                    textPaint.setTypeface(mTypeFace);
                                                    showcaseView = new ShowcaseView.Builder(MainActivity.this)
                                                            .setTarget(new CustomViewTarget(R.id.footer_btn, 150, 100, MainActivity.this))
                                                            .setContentTitle("Add A Drop")
                                                            .setContentText("Click the button to add a new task")
                                                            .setContentTitlePaint(titlePaint)
                                                            .setContentTextPaint(textPaint)
                                                            .setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    switch (counter) {
                                                                        case 0:
                                                                            showcaseView.hide();
                                                                            String fontPath = "fonts/Raleway-Thin.ttf";
                                                                            Typeface mTypeFace = Typeface.createFromAsset(getAssets(), fontPath);
                                                                            TextPaint titlePaint = new TextPaint();
                                                                            titlePaint.setTextSize(150);
                                                                            titlePaint.setColor(ContextCompat.getColor(getApplicationContext(), R.color.text_title));
                                                                            titlePaint.setAntiAlias(true);
                                                                            titlePaint.setTypeface(mTypeFace);
                                                                            TextPaint textPaint = new TextPaint();
                                                                            textPaint.setTextSize(55);
                                                                            textPaint.setColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
                                                                            textPaint.setAntiAlias(true);
                                                                            textPaint.setTypeface(mTypeFace);
                                                                            showcaseView = new ShowcaseView.Builder(MainActivity.this, true)
                                                                                    .setTarget(new CustomViewTarget(R.id.add_menu, 50, 10, MainActivity.this))
                                                                                    .setContentTitle("Add A Drop")
                                                                                    .setContentText("Click the button to add a new task")
                                                                                    .setContentTitlePaint(titlePaint)
                                                                                    .setContentTextPaint(textPaint)
                                                                                    .setOnClickListener(new View.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(View v) {
                                                                                            switch (counter) {
                                                                                                case 0:
                                                                                                    showcaseView.hide();
                                                                                                    String fontPath = "fonts/Raleway-Thin.ttf";
                                                                                                    Typeface mTypeFace = Typeface.createFromAsset(getAssets(), fontPath);
                                                                                                    TextPaint titlePaint = new TextPaint();
                                                                                                    titlePaint.setTextSize(150);
                                                                                                    titlePaint.setColor(ContextCompat.getColor(getApplicationContext(), R.color.text_title));
                                                                                                    titlePaint.setAntiAlias(true);
                                                                                                    titlePaint.setTypeface(mTypeFace);
                                                                                                    TextPaint textPaint = new TextPaint();
                                                                                                    textPaint.setTextSize(55);
                                                                                                    textPaint.setColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
                                                                                                    textPaint.setAntiAlias(true);
                                                                                                    textPaint.setTypeface(mTypeFace);
                                                                                                    showcaseView = new ShowcaseView.Builder(MainActivity.this, true)
                                                                                                            .setTarget(new CustomViewTarget(R.id.add_menu, 270, 10, MainActivity.this))
                                                                                                            .setContentTitle("Sort")
                                                                                                            .setContentText("Organise your Activities in 4 customiszable modes")
                                                                                                            .setContentTitlePaint(titlePaint)
                                                                                                            .setContentTextPaint(textPaint)
                                                                                                            .setOnClickListener(new View.OnClickListener() {
                                                                                                                @Override
                                                                                                                public void onClick(View v) {
                                                                                                                    switch (counter) {
                                                                                                                        case 0:
                                                                                                                            showcaseView.hide();
                                                                                                                            String fontPath = "fonts/Raleway-Thin.ttf";
                                                                                                                            Typeface mTypeFace = Typeface.createFromAsset(getAssets(), fontPath);
                                                                                                                            TextPaint titlePaint = new TextPaint();
                                                                                                                            titlePaint.setTextSize(150);
                                                                                                                            titlePaint.setColor(ContextCompat.getColor(getApplicationContext(), R.color.text_title));
                                                                                                                            titlePaint.setAntiAlias(true);
                                                                                                                            titlePaint.setTypeface(mTypeFace);
                                                                                                                            TextPaint textPaint = new TextPaint();
                                                                                                                            textPaint.setTextSize(55);
                                                                                                                            textPaint.setColor(ContextCompat.getColor(getApplicationContext(), android.R.color.white));
                                                                                                                            textPaint.setAntiAlias(true);
                                                                                                                            textPaint.setTypeface(mTypeFace);
                                                                                                                            showcaseView = new ShowcaseView.Builder(MainActivity.this, true)
                                                                                                                                    .setTarget(new CustomViewTarget(R.id.add_menu, -120, 10, MainActivity.this))
                                                                                                                                    .setContentTitle("Logout")
                                                                                                                                    .setContentText("Click the button to sign out")
                                                                                                                                    .setContentTitlePaint(titlePaint)
                                                                                                                                    .setContentTextPaint(textPaint)
                                                                                                                                    .setStyle(R.style.CustomShowcaseTheme2)
                                                                                                                                    .singleShot(LOGOUT_DROPS)
                                                                                                                                    .build();
                                                                                                                            showcaseView.startAnimation(fade);
                                                                                                                            break;
                                                                                                                    }
                                                                                                                }
                                                                                                            })
                                                                                                            .setStyle(R.style.CustomShowcaseTheme2)
                                                                                                            .singleShot(MENU_DROPS)
                                                                                                            .build();
                                                                                                    showcaseView.startAnimation(fade);
                                                                                                    break;
                                                                                            }
                                                                                        }
                                                                                    })
                                                                                    .setStyle(R.style.CustomShowcaseTheme2)
                                                                                    .singleShot(ADD_DROPS_MENU)
                                                                                    .build();
                                                                            showcaseView.startAnimation(fade);
                                                                            break;
                                                                    }
                                                                }
                                                            })
                                                            .setStyle(R.style.CustomShowcaseTheme2)
                                                            .singleShot(FOOTER_LIST)
                                                            .build();
                                                    showcaseView.startAnimation(fade);
                                                    break;
                                            }
                                        }
                                    })
                                    .singleShot(ROW_LIST)
                                    .setStyle(R.style.CustomShowcaseTheme2)
                                    .build();
                            showcaseView.startAnimation(fade);
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
            Log.e("rec", mBucketRecyclerView.getAdapter() + "");
            SimpleTouchCallBack mSimpletouchcallback = new SimpleTouchCallBack(getApplicationContext(), mAdapterdrops);
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
        if (AppBucketDrops.load(getApplicationContext()) == Filter.NONE) {
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
                    }
//                    else if (mResults.size() == 0 && mark) {
//
//                        Drops mDrops = new Drops(dataSnapshot.child("what").getValue().toString(), Long.parseLong(dataSnapshot.child("added").getValue().toString()),
//                                Long.parseLong(dataSnapshot.child("when").getValue().toString()), Boolean.parseBoolean(dataSnapshot.child("completed").getValue().toString()));
//                        if (!mResults.contains(mDrops)) {
//                            mResults.add(mDrops);
//                            Log.e("Size", mResults.size() + "");
//                            mAdapterdrops.notifyDataSetChanged();
//                            Log.d("Yo", "objects" + mDrops.getWhat());
//                        }
//                    }

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
            SimpleTouchCallBack mSimpletouchcallback = new SimpleTouchCallBack(getApplicationContext(), mAdapterdrops);
            ItemTouchHelper mItemtouchhelper = new ItemTouchHelper(mSimpletouchcallback);
            mItemtouchhelper.attachToRecyclerView(mBucketRecyclerView);
            initBackgroundImage();
        }
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
                menu.findItem(R.id.add_menu).setVisible(true);
                mFilteroption = Filter.LEAST_TIME_LEFT;
                break;
            case R.id.most_remaining:
                menu.findItem(R.id.add_menu).setVisible(true);
                mFilteroption = Filter.MOST_TIME_LEFT;

                break;
            case R.id.completed:
                menu.findItem(R.id.add_menu).setVisible(false);
                mFilteroption = Filter.COMPLETED;
                break;
            case R.id.incomplete:
                menu.findItem(R.id.add_menu).setVisible(false);
                mFilteroption = Filter.INCOMPLETE;
                break;
            case R.id.none:
                menu.findItem(R.id.add_menu).setVisible(true);
                mFilteroption = Filter.NONE;
                break;
            case R.id.profile:
                logout();
                break;
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
                            SimpleTouchCallBack mSimpletouchcallback = new SimpleTouchCallBack(getApplicationContext(), mAdapterdrops);
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
                        SimpleTouchCallBack mSimpletouchcallback = new SimpleTouchCallBack(getApplicationContext(), mAdapterdrops);
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
                            SimpleTouchCallBack mSimpletouchcallback = new SimpleTouchCallBack(getApplicationContext(), mAdapterdrops);
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
                            SimpleTouchCallBack mSimpletouchcallback = new SimpleTouchCallBack(getApplicationContext(), mAdapterdrops);
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
                final int[] count = new int[1];
                mAdapterdrops = new AdapterDrops(mBucketRecyclerView, getApplicationContext(), mResults, mAddListener, mMarkListener, mResetListener);
                mAdapterdrops.setHasStableIds(true);
                if (mUser != null) {
                    mDatabase = FirebaseDatabase.getInstance().getReference().child(mUser.getDisplayName()).child("Drops");
                    mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                Drops drops = new Drops(ds.child("what").getValue().toString(), Long.parseLong(ds.child("added").getValue().toString()), Long.parseLong(ds.child("when").getValue().toString()), Boolean.parseBoolean(ds.child("completed").getValue().toString()));
                                Log.e("Drops", drops.getWhat());
                                mAdapterdrops = new AdapterDrops(mBucketRecyclerView, getApplicationContext(), mResults, mAddListener, mMarkListener, mResetListener);
                                mAdapterdrops.setHasStableIds(true);
                                for(int i=0;i<mResults.size();i++)
                                {
                                    if(!mResults.get(i).getWhat().equals(drops.getWhat().toString()))
                                    {
                                        count[0]++;
                                    }
                                }
                                if(count[0]>=mResults.size())
                                {
                                    mResults.add(drops);
                                }
                                mAdapterdrops.notifyDataSetChanged();
                                mBucketRecyclerView.setAdapter(mAdapterdrops);
                                mAdapterdrops.setAddlistener(mAddListener);
                                SimpleTouchCallBack mSimpletouchcallback = new SimpleTouchCallBack(getApplicationContext(), mAdapterdrops);
                                ItemTouchHelper mItemtouchhelper = new ItemTouchHelper(mSimpletouchcallback);
                                mItemtouchhelper.attachToRecyclerView(mBucketRecyclerView);

                            }
                            Log.e("count", mAdapterdrops.getItemCount() + "" + "size" + mResults.size());

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
                mark = true;
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

    private void showDialogConfirm(int position) {
        DialogConfirm dialogMark = new DialogConfirm();
        Bundle mBundle = new Bundle();
        mBundle.putInt("POSITION", position);
        dialogMark.setArguments(mBundle);
        dialogMark.setConfirmListener(mConfirmListener);
        dialogMark.show(getSupportFragmentManager(), "Confirm");
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
        finish();
    }

}
