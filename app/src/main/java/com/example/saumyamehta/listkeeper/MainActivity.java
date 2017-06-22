package com.example.saumyamehta.listkeeper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.example.saumyamehta.listkeeper.adapters.AdapterDrops;

public class MainActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private Button mButton;
    private RecyclerView mrecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mButton = (Button) findViewById(R.id.button_add);
        mrecyclerView = (RecyclerView) findViewById(R.id.recycler);
        LinearLayoutManager mLinearlayoutmanager = new LinearLayoutManager(this);
        mrecyclerView.setLayoutManager(mLinearlayoutmanager);
        mrecyclerView.setAdapter(new AdapterDrops(this));
        setSupportActionBar(mToolbar);
        initBackgroundImage();
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogAdd();
            }
        });
    }

    private void showDialogAdd() {
        DialogAdd addDialog = new DialogAdd();
        addDialog.show(getSupportFragmentManager(), "Add");
    }

    private void initBackgroundImage() {
        ImageView imgBack = (ImageView) findViewById(R.id.iv_background);

        Glide.with(this)
                .load(R.drawable.background)
                .centerCrop()
                .into(imgBack);
    }
}
