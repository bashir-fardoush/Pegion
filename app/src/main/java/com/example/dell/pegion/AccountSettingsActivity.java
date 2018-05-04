package com.example.dell.pegion;

import android.*;
import android.Manifest;
import android.app.Instrumentation;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AlertDialog;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;

import javax.xml.transform.Result;

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
    public static final int APP_DETAILS_SETTINGS_REQUEST_CODE = 101;

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
                checkStoragePermission();
                if(!Utils.isConnected(getApplicationContext())){
                    Toast.makeText(this, R.string.no_connection, Toast.LENGTH_LONG).show();
                    break;
                }

                // change image here
                Toast.makeText(this, "Image change will be added soon", Toast.LENGTH_SHORT).show();
                break;
            case R.id.status_change_btn:
                if(!Utils.isConnected(getApplicationContext())){
                    Toast.makeText(this, R.string.no_connection, Toast.LENGTH_LONG).show();
                    break;
                }
                Intent intent = new Intent(this, StatusChangeActivity.class);
                intent.putExtra(USER_STATUS_KEY,userStatus);
                startActivity(intent);
                
                break;
        }

    }

    private void checkStoragePermission() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Log.d("permission","Storage permission granted");

                        selectPhotoFromGallery();

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                       if (response.isPermanentlyDenied()){
                           showSettingsDialog();
                       }


                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permission required");
        builder.setMessage("Must need Storage permission to access photos for your profile. You can grant them in app settings.");
        builder.setPositiveButton("Goto Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                openSettings();

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.show();


    }

   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == APP_DETAILS_SETTINGS_REQUEST_CODE){
            if (resultCode == RESULT_OK){
                Log.d("permission","storage permission granted from settings");
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            }
        }

        // use less for permission
    }*/

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",getPackageName(),null);
        intent.setData(uri);
        startActivityForResult(intent,APP_DETAILS_SETTINGS_REQUEST_CODE);

    }

    private void selectPhotoFromGallery() {

        Toast.makeText(this, "Select Photo from Gallery will be added soon", Toast.LENGTH_SHORT).show();
    }
}
