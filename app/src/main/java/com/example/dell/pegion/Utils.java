package com.example.dell.pegion;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by DELL on 5/4/2018.
 */

public class Utils {

    public static boolean isConnected( Context context){

        boolean isConnected = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        isConnected = networkInfo!=null && networkInfo.isConnectedOrConnecting();
        return isConnected;
    }
}
