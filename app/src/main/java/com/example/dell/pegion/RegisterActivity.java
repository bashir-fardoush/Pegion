package com.example.dell.pegion;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private String childUsers = "users";
    //private String childUserId =
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private TextInputLayout nameIL,emailIL,passwordIL,confirmPassIL;
    private Button registerBtn;
    private TextView logInTV;
    private android.support.v7.widget.Toolbar regToolbar;

    private AlertDialog.Builder builder;
    private ProgressDialog registerProgressdialog;

    private DatabaseReference databaseReference = null;


    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        regToolbar = findViewById(R.id.register_toolbar);
        setSupportActionBar(regToolbar);
        getSupportActionBar().setTitle("Create an Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        nameIL = findViewById(R.id.register_name_IL);
        emailIL = findViewById(R.id.register_email_IL);
        passwordIL = findViewById(R.id.register_password_IL);
        confirmPassIL = findViewById(R.id.register_confirm_password_IL);
        registerBtn = findViewById( R.id.register_btn);
        logInTV = findViewById(R.id.register_login_option);

        registerBtn.setOnClickListener(this);
        logInTV.setOnClickListener(this);

        builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Verify Email");
        builder.setMessage("A verification mail sent to your email, please follow the mail. Be patient it may take some while");
        builder.setPositiveButton("Refresh", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {
                mAuth.getCurrentUser().reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (mAuth.getCurrentUser().isEmailVerified()){
                            Toast.makeText(RegisterActivity.this, "Congratulation, Your Email Verified", Toast.LENGTH_LONG).show();
                            Log.d("registerAct","Email Verified");
                            mAuth.signOut();
                            startActivity(new Intent(RegisterActivity.this, LogInActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                            finish();
                        }
                        dialogInterface.cancel();
                        builder.show();
                    }
                });
            }
        }).setNegativeButton("Sent Again", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                verifyUserEmail();
                dialogInterface.cancel();
            }
        });

        registerProgressdialog = new ProgressDialog(this);
        registerProgressdialog.setTitle("Creating your Account");
        registerProgressdialog.setCancelable(false);
        registerProgressdialog.setIndeterminate(true);


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (user !=null && !user.isEmailVerified())
        {
            builder.show();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.register_btn:
                validateSignCredential(); //validate then register
                break;
            case R.id.register_login_option:
                Intent logInIntent = new Intent(this, LogInActivity.class);
                logInIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(logInIntent);
                finish();
                break;

        }
    }

    private void validateSignCredential() {
        String name = nameIL.getEditText().getText().toString();
        String email = emailIL.getEditText().getText().toString();
        String password = passwordIL.getEditText().getText().toString();
        String confirmedPassword = confirmPassIL.getEditText().getText().toString();

        if (email.isEmpty()) {
            emailIL.getEditText().setError(getString(R.string.email_required));
        }else if(password.length()<6 ){
            passwordIL.getEditText().setError(getString(R.string.password_length_constraint));
        }else if (!password.equals(confirmedPassword)){
            passwordIL.getEditText().setError(getString(R.string.password_not_matched));
            confirmPassIL.getEditText().setError(getString(R.string.password_not_matched));
        }
        else {
            register(name,email,password);
            registerProgressdialog.show();
        }
    }

    private void register(final String name, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                String userID = mAuth.getCurrentUser().getUid();
                DatabaseReference reference = databaseReference.child(childUsers).child(userID);
                HashMap<String, String> userMap = new HashMap<>();
                userMap.put("name",name);
                userMap.put("imageUrl","default_image_link");
                userMap.put("thumbImageUrl","default_link");
                userMap.put("status","HI i am using Pegion");

                reference.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){
                            registerProgressdialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Account Created Successfully", Toast.LENGTH_LONG).show();
                            Log.d("userID",mAuth.getCurrentUser().getUid());
                            verifyUserEmail();
                        }
                        else {
                            Toast.makeText(RegisterActivity.this, "failed to store info", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                registerProgressdialog.dismiss();
                Toast.makeText(RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("registerAct",e.getMessage());
            }
        });

    }

    private void verifyUserEmail() {

        mAuth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                builder.show();
                Log.d("registerAct","dialog show from verifyUserEmail method");
                Toast.makeText(RegisterActivity.this, "Verification email sent to your mail", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this, "Failed to send verification mail", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
