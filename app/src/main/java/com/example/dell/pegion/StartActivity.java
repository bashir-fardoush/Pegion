package com.example.dell.pegion;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import javax.security.auth.login.LoginException;

public class StartActivity extends AppCompatActivity {

    private TextView sampleTV;
    private FirebaseAuth mAuth;
    private FirebaseUser user=null;
    private android.support.v7.widget.Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        toolbar = findViewById(R.id.start_activity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);
        sampleTV = findViewById(R.id.sampleTextId);
        sampleTV.setText("On Start Activity");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(user == null){
           sendToLogIn(true);
        }else if (!user.isEmailVerified()){
           sendToRegister(true);
        }else {
            sampleTV.setText("User Email verified");
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_log_out_btn:
                mAuth.signOut();
                sendToLogIn(true);
                break;
            case R.id.menu_chat_room_btn:
                Toast.makeText(this, "Enter into chat room", Toast.LENGTH_LONG).show();
                break;
        }
        return true;
    }
    private void sendToLogIn(boolean finishCurrentActivity){
        startActivity(new Intent(this, LogInActivity.class));
        if (finishCurrentActivity) finish();
    }
    private void sendToRegister(boolean finishCurrentActivity) {
        startActivity(new Intent(this, RegisterActivity.class));
        if (finishCurrentActivity) finish();
    }
}
