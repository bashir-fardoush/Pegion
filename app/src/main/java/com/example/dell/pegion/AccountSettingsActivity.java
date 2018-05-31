package com.example.dell.pegion;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.attribute.FileAttribute;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class AccountSettingsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int GALLERY_PICK_CODE = 101;
    private static final String TAG ="tag_AS_Activity" ;
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

        if (profileImageUrl != "link"){
            Picasso.get().load(profileImageUrl).into(profileIV);
        }


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
    private byte[] getCompressedImageBytes(Uri imageUri){
        Bitmap compressedImageBitmap = null;
        File file = new File(imageUri.getPath());

        try {
            compressedImageBitmap = new Compressor(this)
                    .setMaxHeight(200)
                    .setMaxWidth(200)
                    .setQuality(70)
                    .compressToBitmap(file);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG,"compress failed");
        }


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        compressedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        final byte[] data = baos.toByteArray();
        return data;
    }

    private void uploadImage(Uri imageUri)  {

        final byte[] data = getCompressedImageBytes(imageUri);

        if (data == null){
            Toast.makeText(this, "Unable to process image for uploading", Toast.LENGTH_SHORT).show();
           return;
        }


        progressDialog.show();
        final StorageReference profileImagePath = profileImageRef.child("pegion").child(userId).child("profile_images").child("profile_image.jpg");
        final StorageReference profileThumbImagePath = profileImageRef.child("pegion").child(userId).child("profile_images").child("thumb_image.jpg");

        profileImagePath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
           @Override
           public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
               if (task.isSuccessful()){

                  Toast.makeText(AccountSettingsActivity.this, "Upload Successfull",Toast.LENGTH_LONG).show();
                  final String profileDownloadUrl = String.valueOf(task.getResult().getDownloadUrl());

                  profileThumbImagePath.putBytes(data).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                      @Override
                      public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                          if (thumb_task.isSuccessful()){
                              String thumbDownloadUri = thumb_task.getResult().getDownloadUrl().toString();
                              saveUriInDatabase(profileDownloadUrl,thumbDownloadUri);
                          }
                          else {
                              progressDialog.dismiss();
                              String errorMsg = thumb_task.getException().getMessage();
                              Log.d(TAG," thumb Image Upload failed\n"+errorMsg);
                              Toast.makeText(AccountSettingsActivity.this, "Upload failed", Toast.LENGTH_LONG).show();
                          }
                      }
                  });

               }else {
                   progressDialog.dismiss();
                   String errorMsg = task.getException().getMessage();
                   Log.d(TAG,"Image Upload failed\n"+errorMsg);
                   Toast.makeText(AccountSettingsActivity.this, "Upload failed", Toast.LENGTH_LONG).show();
               }
           }
       });
    }

    private void saveUriInDatabase(String profileDownloadUrl,String thumbDownloadUri) {

        Map<String,Object> map = new HashMap<>();
        map.put("imageUrl",profileDownloadUrl);
        map.put("thumbImageUrl",thumbDownloadUri);

        childReference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
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
