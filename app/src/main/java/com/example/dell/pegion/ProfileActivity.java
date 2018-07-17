package com.example.dell.pegion;

import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dell.pegion.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView userIV;
    private TextView userNameTV,userStatusTV,fndSinceTV;
    private Button friendBtn,declineBtn;
    private LinearLayout fndFeatureLayout;

    private String TAG = "tag_ProfileActivity";
    private String name,status,profileImageUrl;
    private int requestTypeCode = 0; /*0. not friend 1.req sent 2.req received 3.Friends */
    private String userID = null;
    private DatabaseReference reference;
    private DatabaseReference fndReqRef;
    private DatabaseReference fndRef;
    private DatabaseReference notificationRef;
    private DatabaseReference rootRef;
    private String current_userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);



         userID = getIntent().getStringExtra("userId");
        //Toast.makeText(this, ""+userID, Toast.LENGTH_SHORT).show();
        rootRef = FirebaseDatabase.getInstance().getReference();
        reference = FirebaseDatabase.getInstance().getReference().child("users").child(userID);
        fndReqRef = FirebaseDatabase.getInstance().getReference().child("friends_req");
        fndRef = FirebaseDatabase.getInstance().getReference().child("friends");
        notificationRef = FirebaseDatabase.getInstance().getReference().child("notification");

        current_userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

         userIV = findViewById(R.id.user_iv);
        userNameTV = findViewById(R.id.user_name_tv);
        userStatusTV = findViewById(R.id.user_status_tv);
        fndSinceTV = findViewById(R.id.fnd_since_tv);
        friendBtn = findViewById(R.id.friend_btn);
        declineBtn = findViewById(R.id.decline_btn);
        fndFeatureLayout = findViewById(R.id.fnd_feature_linearLayout);

        fndSinceTV.setVisibility(View.GONE);
        friendBtn.setOnClickListener(this);
        declineBtn.setOnClickListener(this);
        requestTypeCode = 0;
        friendBtn.setText(R.string.send_friend_request);
        declineBtn.setVisibility(View.GONE);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("name").getValue(String.class);
                status = dataSnapshot.child("status").getValue(String.class);
                profileImageUrl = dataSnapshot.child("imageUrl").getValue(String.class);

                userNameTV.setText(name);
                userStatusTV.setText(status);
                if (!profileImageUrl.equals("link")){
                    Picasso.get().load(Uri.parse(profileImageUrl))
                            .placeholder(R.drawable.default_person_image).into(userIV);
                }else {
                    userIV.setImageResource(R.drawable.default_person_image);
            }


               }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG,"Data load Failed");
            }
        });

        fndReqRef.child(current_userId).child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                boolean isExists  =  dataSnapshot.child("request_type").exists();
                if (isExists){
                    requestTypeCode =  dataSnapshot.child("request_type").getValue(Integer.class);
                    if (requestTypeCode == 0){
                        friendBtn.setText(R.string.send_friend_request);
                        declineBtn.setVisibility(View.GONE);
                    }else if(requestTypeCode == 1){
                        friendBtn.setText(R.string.cancel_friend_request);
                        declineBtn.setVisibility(View.GONE);
                    }else if (requestTypeCode == 2){
                        friendBtn.setText(R.string.accept_friend_request);
                        declineBtn.setVisibility(View.VISIBLE);
                        declineBtn.setText(R.string.decline_friend_request);
                    }
                }else {
                    declineBtn.setVisibility(View.GONE);
                   /*default */
                }
               // Log.d(TAG,"request type retrived");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG,"request type cancelled: "+databaseError.getMessage());
            }
        });

        fndRef.child(current_userId).child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                boolean isExists = dataSnapshot.child("friends_since").exists();
                if (isExists){
                    String friendSince = dataSnapshot.child("friends_since").getValue(String.class);
                    requestTypeCode = 3;
                    friendBtn.setText(R.string.unfirend);
                   // declineBtn.setVisibility(View.VISIBLE);
                    fndSinceTV.setText("Friend Since: "+friendSince);
                    fndSinceTV.setVisibility(View.VISIBLE);
                    declineBtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG,"friend since data retrive error: "+databaseError.getMessage());
            }
        });




        if(userID.equals(current_userId)){
            fndFeatureLayout.setVisibility(View.INVISIBLE);
        }


    }

    @Override
    public void onClick(View v) {
        if (!Utils.isConnected(this)){
            Toast.makeText(this, "No internet connection,Turn on then try again", Toast.LENGTH_SHORT).show();

        }
        switch (v.getId()){
            case R.id.friend_btn:
                friendBtn.setEnabled(false);
                if (requestTypeCode == 0){/*not friend , have to send friend request*/

                    DatabaseReference notificaitonReference = notificationRef.child(userID).push();
                    String Notificationkey = notificaitonReference.getKey();
                    HashMap<String ,String> notificationDataMap= new HashMap<>();
                    notificationDataMap.put("from",current_userId);
                    notificationDataMap.put("type","request");

                    Map requestMap = new HashMap<>();
                    requestMap.put("friends_req/"+current_userId+"/"+userID+"/request_type",1);
                    requestMap.put("friends_req/"+userID+"/"+current_userId+"/request_type",2);
                    requestMap.put("notification/"+userID+"/"+Notificationkey,notificationDataMap);

                    rootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if (databaseError != null){
                                Log.d(TAG, "friend request freature: "+databaseError.getMessage());
                                Toast.makeText(ProfileActivity.this, "Request sent failed,There was a problem", Toast.LENGTH_LONG).show();
                            }else {
                                Toast.makeText(ProfileActivity.this, "Request sent Successfully", Toast.LENGTH_SHORT).show();
                                friendBtn.setText(R.string.cancel_friend_request);
                            }
                            friendBtn.setEnabled(true);
                        }
                    });



                }else if(requestTypeCode == 1){/*friend request sent have to cancel friend request*/
                    Map map = new HashMap<>();
                    map.put("friends_req/"+current_userId+"/"+userID+"/request_type",null);
                    map.put("friends_req/"+userID+"/"+current_userId+"/request_type",null);
                   // requestMap.put("notification/"+userID+"/"+Notificationkey,notificationDataMap);
                    rootRef.updateChildren(map, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null){
                                Log.d(TAG, "cancel request freature: "+databaseError.getMessage());
                                Toast.makeText(ProfileActivity.this, "Can't cancel request now, There was a problem", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(ProfileActivity.this, "Request canceled successfully", Toast.LENGTH_SHORT).show();
                                friendBtn.setText(R.string.send_friend_request);
                                requestTypeCode = 0;
                            }

                            friendBtn.setEnabled(true);

                        }
                    });


                }else if(requestTypeCode == 2){/*friend request received have to accept friend request*/

                    String time = Utils.getCurrentTime();

                    Map map = new HashMap<>();
                    map.put("friends_req/"+current_userId+"/"+userID+"/request_type",null);
                    map.put("friends_req/"+userID+"/"+current_userId+"/request_type",null);
                    map.put("friends/"+current_userId+"/"+userID+"/friends_since",time);
                    map.put("friends/"+userID+"/"+current_userId+"/friends_since",time);

                    rootRef.updateChildren(map, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null){
                                Log.d(TAG, "request accept freature: "+databaseError.getMessage());
                                Toast.makeText(ProfileActivity.this, "Can't accept request now, There was a problem", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(ProfileActivity.this, "Hurrah! You are friends Now", Toast.LENGTH_LONG).show();
                                requestTypeCode = 3;
                                friendBtn.setText(R.string.unfirend);
                                declineBtn.setVisibility(View.GONE);
                            }

                            friendBtn.setEnabled(true);
                        }
                    });

                }else if (requestTypeCode == 3){/*firends, make unfriend now*/

                    Map map = new HashMap<>();
                    map.put("friends/"+current_userId+"/"+userID+"/friends_since",null);

                    rootRef.updateChildren(map, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null){
                                Log.d(TAG, "unfriend freature: "+databaseError.getMessage());
                                Toast.makeText(ProfileActivity.this, "Can't unfriend now, There was a problem", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(ProfileActivity.this, "Successfully unfriend", Toast.LENGTH_SHORT).show();
                                requestTypeCode = 0;
                                friendBtn.setText(R.string.send_friend_request);
                                declineBtn.setVisibility(View.GONE);
                                fndSinceTV.setText("friend since");
                                fndSinceTV.setVisibility(View.GONE);
                            }
                            friendBtn.setEnabled(true);
                        }
                    });


                }


                break;
            case R.id.decline_btn:
                fndReqRef.child(current_userId).child(userID).child("request_type").removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    fndReqRef.child(userID).child(current_userId).child("request_type").removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        Toast.makeText(ProfileActivity.this, "Request declined", Toast.LENGTH_LONG).show();
                                                       //  friendBtn.setText(R.string.send_friend_request);
                                                        // requestTypeCode = 0;
                                                    }else {
                                                        Log.d(TAG,"Request decline failed on sender side");
                                                    }
                                                    friendBtn.setText(R.string.send_friend_request);
                                                    requestTypeCode = 0;
                                                    friendBtn.setEnabled(true);
                                                    declineBtn.setVisibility(View.GONE);

                                                }
                                            });
                                }else {
                                    Toast.makeText(ProfileActivity.this, "Request decline failed", Toast.LENGTH_LONG).show();
                                    Log.d(TAG,"Request decline failed");
                                    friendBtn.setEnabled(true);
                                }
                            }
                        });
                break;

                default:

        }
    }
}
