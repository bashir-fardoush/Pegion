package com.example.dell.pegion;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusChangeActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputLayout statusInputLayout;
    private Button saveBtn;
    private Toolbar statusToolbar;
    private String status = null;

    private DatabaseReference reference = null;
    private FirebaseUser user;
    private String userID = null;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_change);

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        status = getIntent().getExtras().getString(AccountSettingsActivity.USER_STATUS_KEY);
        reference = FirebaseDatabase.getInstance().getReference().child("users").child(userID).child("status");

        statusToolbar = findViewById(R.id.status_change_toolbar);
        setSupportActionBar(statusToolbar);
        getSupportActionBar().setTitle("Change status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setTitle("Saving Status");
        dialog.setMessage("Please wait while saving status");

        statusInputLayout = findViewById(R.id.status_input_layout);
        saveBtn = findViewById(R.id.status_save_btn);
        saveBtn.setOnClickListener(this);

        statusInputLayout.getEditText().setText(status);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


         switch (item.getItemId()){
             case android.R.id.home:
                 //startActivity(new Intent(this, AccountSettingsActivity.class));
                 this.finish();
                 break;
         }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.status_save_btn:
                if (!Utils.isConnected(getApplicationContext())){
                    Toast.makeText(this, R.string.no_connection, Toast.LENGTH_SHORT).show();
                    break;
                }



                status = statusInputLayout.getEditText().getText().toString();

                if (status != null && !status.equals("")){
                    dialog.show();

                    reference.setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                dialog.dismiss();
                                Toast.makeText(StatusChangeActivity.this, "Status saved successfully", Toast.LENGTH_SHORT).show();
                                finish();
                            }else {
                                dialog.dismiss();
                                Toast.makeText(StatusChangeActivity.this, "Can't save status", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else {

                    Toast.makeText(this, "Empty status not allowed", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }
}
