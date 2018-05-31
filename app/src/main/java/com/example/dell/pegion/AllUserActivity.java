package com.example.dell.pegion;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.example.dell.pegion.models.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AllUserActivity extends AppCompatActivity {

    private RecyclerView allUserRV;
    private  Toolbar toolbar;
    private DatabaseReference dbRef;
    private static final String TAG = "tag_AllUserActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_all_user);

        toolbar = findViewById(R.id.all_user_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("All Users");

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbRef = FirebaseDatabase.getInstance().getReference().child("users");

        allUserRV = findViewById(R.id.all_users_rv);
        allUserRV.hasFixedSize();
        allUserRV.setLayoutManager(new LinearLayoutManager(this));


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<User,UserViewHolder> recyclerAdapter = new FirebaseRecyclerAdapter<User, UserViewHolder>
                (User.class,R.layout.single_user_layout,UserViewHolder.class,dbRef) {
            @Override
            protected void populateViewHolder(UserViewHolder viewHolder, User model, int position) {

                viewHolder.setUserName(model.getName());
                viewHolder.setUserStatus(model.getStatus());
                viewHolder.setUserImage(model.getThumbImageUrl(),AllUserActivity.this);

            }
        };


        allUserRV.setAdapter(recyclerAdapter);


    }
}
