package com.example.dell.pegion;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by DELL on 4/11/2018.
 */

public class MyApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}