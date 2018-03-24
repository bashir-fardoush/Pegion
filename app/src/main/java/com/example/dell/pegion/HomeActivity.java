package com.example.dell.pegion;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private FirebaseUser user=null;
    private android.support.v7.widget.Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SectionPagerAdapter pagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        toolbar = findViewById(R.id.home_activity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);
        tabLayout = findViewById(R.id.home_tab_layout);
        viewPager = findViewById(R.id.tab_pager);

        pagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        tabLayout.setupWithViewPager(viewPager);


    }

    @Override
    protected void onStart() {
        super.onStart();
        if(user == null){
           sendToLogIn(true);
        }else if (!user.isEmailVerified()){
           sendToRegister(true);
        }else {

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
