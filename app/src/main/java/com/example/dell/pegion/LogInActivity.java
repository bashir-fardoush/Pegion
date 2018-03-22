package com.example.dell.pegion;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
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

public class LogInActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private TextInputLayout emailInputLayout,passwordInputLayout;
    private Button logInBtn;
    private TextView registerTV;
    private android.support.v7.widget.Toolbar logInToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        logInToolbar = findViewById(R.id.login_toolbar);
        setSupportActionBar(logInToolbar);
        getSupportActionBar().setTitle("LogIn to Pegion");

        mAuth = FirebaseAuth.getInstance();
        emailInputLayout = findViewById(R.id.logIn_email_IL);
        passwordInputLayout = findViewById(R.id.logIn_password_IL);
        logInBtn = findViewById(R.id.logIn_btn);
        registerTV = findViewById(R.id.logIn_register_Option);
        logInBtn.setOnClickListener(this);
        registerTV.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.logIn_btn:
                logIn();
                break;
            case R.id.logIn_register_Option:
                Intent registerIntent = new Intent(this, RegisterActivity.class);
                startActivity(registerIntent);
                finish();
                break;
        }
    }
    private void logIn(){
        //show a round loading progressbar
        String email= emailInputLayout.getEditText().getText().toString();
        String password= passwordInputLayout.getEditText().getText().toString();

        mAuth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Intent startIntent = new Intent(LogInActivity.this,StartActivity.class);
                startActivity(startIntent);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LogInActivity.this, "Login failed:  "+e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("loginAct_Failed",e.getMessage());
            }
        });

    }
}
