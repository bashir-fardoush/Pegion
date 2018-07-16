package com.example.dell.pegion;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by DELL on 7/12/2018.
 */

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {


    private static final String CHANNEL_ID = "friend_request_notification_channel";
    private static final String TAG = "tag_FMService";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);


//        Toast.makeText(this, "Totification Message Received", Toast.LENGTH_LONG).show();
        Log.d(TAG,"Notification Message Received" );

        Map<String , String > data = remoteMessage.getData();

        String notification_title = data.get("title");
        String notification_text = data.get("body");
        String click_action = data.get("click_action");
        String friend_request_sender_id =data.get("from_user_id");





        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(click_action);
        intent.putExtra("userId",friend_request_sender_id);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(notification_title)
                .setContentText(notification_text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        int notificationId = (int) System.currentTimeMillis();
        notificationManager.notify(notificationId, mBuilder.build());


    }


}
