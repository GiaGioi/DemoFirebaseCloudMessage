package com.gioidev.demofirebasecloudmessage.Sevice;

import android.app.Notification;
import android.os.Build;
import android.support.v4.app.NotificationManagerCompat;

import com.gioidev.demofirebasecloudmessage.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyfirebaseInstantSevice extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        super.onMessageReceived(remoteMessage);
        showNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
    }
    public void showNotification(String title, String message){
        Notification.Builder builder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this,"My notification")
                    .setContentTitle(title)
                    .setSmallIcon(R.drawable.ic_error)
                    .setAutoCancel(true)
                    .setContentText(message);
        }
        NotificationManagerCompat compat = NotificationManagerCompat.from(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            compat.notify(123,builder.build());
        }

    }
}
