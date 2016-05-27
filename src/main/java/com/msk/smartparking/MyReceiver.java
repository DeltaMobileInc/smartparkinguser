package com.msk.smartparking;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReceiver extends BroadcastReceiver {
    public MyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(context);
        builder.setContentTitle("Smart Parking");
        builder.setContentText("You have 15 mins to enter the parking lot");
        builder.setSmallIcon(R.drawable.fourwheeler);

        Notification notification = builder.build();
        notificationManager.notify(1, notification);
    }
}
