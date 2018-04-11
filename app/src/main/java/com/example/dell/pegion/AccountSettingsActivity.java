package com.example.dell.pegion;

import android.content.Intent;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountSettingsActivity extends AppCompatActivity implements View.OnClickListener {
    private CircleImageView userImage;
    private TextView userNameTV, userStatusTV;
    private Button statusChangeBtn,imageChangeBtn;

    private DatabaseReference parentReference, childReference;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private String userId = null;
    private String userStatus = null;
    private String userName = null;
    public static final String USER_STATUS_KEY ="user status";

    private ContentLoadingProgressBar loadingProgressBar;

//    private String childUsers = "users";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();
        parentReference = FirebaseDatabase.getInstance().getReference();
        childReference = parentReference.child("users").child(userId);


        userImage = findViewById(R.id.profile_settings_image);
        userNameTV = findViewById(R.id.user_name);
        userStatusTV = findViewById(R.id.user_status);
        imageChangeBtn = findViewById(R.id.image_change_btn);
        statusChangeBtn = findViewById(R.id.status_change_btn);

        imageChangeBtn.setOnClickListener(this);
        statusChangeBtn.setOnClickListener(this);


            loadingProgressBar = new ContentLoadingProgressBar(this);


            childReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                 userName = dataSnapshot.child("name").getValue().toString();
                 userStatus = dataSnapshot.child("status").getValue().toString();
                Log.d("settings_dataload","Success");
                updateUI(userName, userStatus);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("settings_dataload","failed");
            }
        });
    }

    private void updateUI(String userName, String userStatus) {
        userNameTV.setText(userName);
        userStatusTV.setText(userStatus);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.image_change_btn:
                // change image here
                Toast.makeText(this, "Image change will be added soon", Toast.LENGTH_SHORT).show();
                break;
            case R.id.status_change_btn:
                Intent intent = new Intent(this, StatusChangeActivity.class);
                intent.putExtra(USER_STATUS_KEY,userStatus);
                startActivity(intent);
                
                break;
        }

    }
}
