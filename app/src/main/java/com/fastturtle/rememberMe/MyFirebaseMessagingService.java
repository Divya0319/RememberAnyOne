package com.fastturtle.rememberMe;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.fastturtle.rememberMe.activities.AddUserActivity;
import com.fastturtle.rememberMe.activities.DashBoardActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    String TAG = "Firebase_MSG";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        if (message.getNotification() != null) {
            NotificationHelper notificationHelper = new NotificationHelper(this);
            notificationHelper.createNotification(message.getNotification().getTitle(), message.getNotification().getBody());
            Log.d(TAG, "Title: " + message.getNotification().getTitle());
            Log.d(TAG, "Message Body: " + message.getNotification().getBody());
        } else {
            Log.d(TAG, "From: " + "null");
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }

    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, AddUserActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                .setSmallIcon(R.drawable.icon_capture)
                .setContentTitle("FCM Message")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(1 /* ID of notification */, notificationBuilder.build());
    }
}
