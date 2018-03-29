package com.example.dell.pegion;

import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountSettingsActivity extends AppCompatActivity {
    private CircleImageView userImage;
    private TextView userNameTV, userStatusTV;
    private Button statusChangeBtn,imageChangeBtn;

    private DatabaseReference parentReference, childReference;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private String userId = null;

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


            loadingProgressBar = new ContentLoadingProgressBar(this);


            childReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userName = dataSnapshot.child("name").getValue().toString();
                String userStatus = dataSnapshot.child("status").getValue().toString();
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
}
