package com.example.saumyamehta.listkeeper;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.saumyamehta.listkeeper.adapters.AppBucketDrops;
import com.example.saumyamehta.listkeeper.adapters.CustomViewTarget;
import com.example.saumyamehta.listkeeper.beans.Drops;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.ybq.android.spinkit.style.ChasingDots;
import com.github.ybq.android.spinkit.style.Circle;
import com.github.ybq.android.spinkit.style.CubeGrid;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.github.ybq.android.spinkit.style.FoldingCube;
import com.github.ybq.android.spinkit.style.RotatingCircle;
import com.github.ybq.android.spinkit.style.WanderingCubes;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.BatchUpdateException;

import br.com.goncalves.pugnotification.notification.Progress;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

public class GoogleSignIn extends AppCompatActivity {
    private static final long SYNC_ID = 1;
    private Button mGoogleSignin;
    private GoogleApiClient mGoogleApiClient;
    public static final int RC_SIGN_IN = 1;
    private FirebaseAuth.AuthStateListener mListener;
    private FirebaseAuth mAuth;
    private ImageView mProgress;
    private ChasingDots mChasing;
    private String SEQ_ID = "one";
    private  ShowcaseView mShowcase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_google_sign_in);

        mGoogleSignin = (Button) findViewById(R.id.button_google);
        AppBucketDrops.setRalewayThin(getApplicationContext(), mGoogleSignin);
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
       mShowcase =  new ShowcaseView.Builder(this)
                .setTarget(new CustomViewTarget(R.id.button_google, 195, 50, this))
                .setContentTitle("Sync your items !")
                .setContentText("Tap the button to sync your acivities")
                .setContentTitlePaint(titlePaint)
                .setContentTextPaint(textPaint)
                .singleShot(SYNC_ID)
                .setStyle(R.style.CustomShowcaseTheme2)
                .build();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        final Animation fade = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.fade);
        mShowcase.startAnimation(fade);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();

            }
        });
        mListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    Toast.makeText(GoogleSignIn.this, "Welcome " + firebaseAuth.getCurrentUser().getDisplayName(), Toast.LENGTH_SHORT).show();
                    mChasing.start();
                    mGoogleSignin.setVisibility(View.GONE);

                    new AsyncTaskActivity().execute();
                } else {

                }
            }
        };
        initBackgroundImage();
        mProgress = (ImageView) findViewById(R.id.progressBar1);
        mChasing = new ChasingDots();
        mChasing.setBounds(0, 0, 100, 100);
        mProgress.setImageDrawable(mChasing);

    }

    private void signIn() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    private void initBackgroundImage() {
        final RelativeLayout imgBack = (RelativeLayout) findViewById(R.id.rels);

        Glide.with(this).load(R.drawable.background).asBitmap().into(new SimpleTarget<Bitmap>(100, 100) {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                imgBack.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.background));
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);

            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            mChasing = new ChasingDots();
                            mChasing.setBounds(0, 0, 100, 100);
                            mProgress.setImageDrawable(mChasing);
                            mChasing.start();

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(GoogleSignIn.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }

    private class AsyncTaskActivity extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Handler h = new Handler();

            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(GoogleSignIn.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }, 3000);


        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
}
