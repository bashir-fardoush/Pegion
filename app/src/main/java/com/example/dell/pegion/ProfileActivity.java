package com.example.dell.pegion.models;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.dell.pegion.R;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        String userID = getIntent().getStringExtra("userId");
        Toast.makeText(this, ""+userID, Toast.LENGTH_SHORT).show();
    }
}
