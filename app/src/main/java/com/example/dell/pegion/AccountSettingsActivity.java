package com.example.dell.pegion;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountSettingsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int GALLERY_PICK_CODE = 101;
    private static final String TAG ="AccountSettingsActivity" ;
    private CircleImageView profileIV;
    private TextView userNameTV, userStatusTV;
    private Button statusChangeBtn,imageChangeBtn;

    private DatabaseReference parentReference, childReference;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private String userId = null;
    private String userStatus = null;
    private String userName = null;
    private String profileImageUrl = null;
    public static final String USER_STATUS_KEY ="user status";
    public static final int APP_DETAILS_SETTINGS_REQUEST_CODE = 101;

    private StorageReference profileImageRef;

    private ContentLoadingProgressBar loadingProgressBar;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user.getUid();
        parentReference = FirebaseDatabase.getInstance().getReference();
        childReference = parentReference.child("users").child(userId);

        profileImageRef = FirebaseStorage.getInstance().getReference();


        profileIV = findViewById(R.id.profile_settings_image);
        userNameTV = findViewById(R.id.user_name);
        userStatusTV = findViewById(R.id.user_status);
        imageChangeBtn = findViewById(R.id.image_change_btn);
        statusChangeBtn = findViewById(R.id.status_change_btn);

        imageChangeBtn.setOnClickListener(this);
        statusChangeBtn.setOnClickListener(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait while uploading image...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(true);


            loadingProgressBar = new ContentLoadingProgressBar(this);


            childReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                 userName = dataSnapshot.child("name").getValue().toString();
                 userStatus = dataSnapshot.child("status").getValue().toString();
                profileImageUrl = dataSnapshot.child("imageUrl").getValue().toString();

                updateUI(userName, userStatus,profileImageUrl);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("settings_dataload","failed");
            }
        });
    }

    private void updateUI(String userName, String userStatus,String profileImageUrl) {
        userNameTV.setText(userName);
        userStatusTV.setText(userStatus);
        Picasso.get().load(profileImageUrl).into(profileIV);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.image_change_btn:
                //checkStoragePermission();
                if(!Utils.isConnected(getApplicationContext())){
                    Toast.makeText(this, R.string.no_connection, Toast.LENGTH_LONG).show();
                    break;
                }

                /*pick and get cropped image*/
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(this);

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

    private void selectPhotoFromGallery() {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

       if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
           CropImage.ActivityResult result = CropImage.getActivityResult(data);

           if (resultCode == RESULT_OK){
               Uri imageUri = result.getUri();
               uploadImage(imageUri);
           }else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
               Exception error = result.getError();
               Log.d(TAG, error.getMessage());
           }
       }



    }

    private void uploadImage(Uri imageUri) {

        progressDialog.show();
        StorageReference profileImagePath = profileImageRef.child("pegion").child(userId).child("profile_images").child("profile_image.jpg");
        profileImagePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
           @Override
           public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
               if (task.isSuccessful()){
                   Toast.makeText(AccountSettingsActivity.this, "Upload Successfull",Toast.LENGTH_LONG).show();
                  String downloadUri = String.valueOf(task.getResult().getDownloadUrl());
                   saveUriInDatabase(downloadUri);
               }else {
                   progressDialog.dismiss();
                   String errorMsg = task.getException().getMessage();
                   Log.d(TAG,"Image Upload failed\n"+errorMsg);
                   Toast.makeText(AccountSettingsActivity.this, "Upload failed", Toast.LENGTH_LONG).show();
               }
           }
       });
    }

    private void saveUriInDatabase(String downloadUri) {

        childReference.child("imageUrl").setValue(downloadUri).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    progressDialog.dismiss();
                    Toast.makeText(AccountSettingsActivity.this, "Successfully Uploaded", Toast.LENGTH_LONG).show();
                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(AccountSettingsActivity.this, "Upload Failed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",getPackageName(),null);
        intent.setData(uri);
        startActivityForResult(intent,APP_DETAILS_SETTINGS_REQUEST_CODE);

    }


}
