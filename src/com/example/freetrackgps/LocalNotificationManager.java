package com.example.freetrackgps;

import android.app.Activity;
import android.content.Context;
import android.app.NotificationManager;
import android.content.Intent;
import android.app.PendingIntent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public class LocalNotificationManager {
    private SharedPreferences pref;
    private NotificationManager notifcationManager;
    private NotificationCompat.Builder mainNotification;
    public  LocalNotificationManager(Context currentContext, int smallIcon, String title){
        mainNotification = new NotificationCompat.Builder(currentContext);
        mainNotification.setContentTitle(title);
        mainNotification.setSmallIcon(smallIcon);
        notifcationManager = (NotificationManager) currentContext.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(currentContext, MainActivity.class);
        TaskStackBuilder currentstackBuilder = TaskStackBuilder.create(currentContext);
        currentstackBuilder.addParentStack(MainActivity.class);
        currentstackBuilder.addNextIntent(intent);
        PendingIntent pending = currentstackBuilder.getPendingIntent(0,  PendingIntent.FLAG_UPDATE_CURRENT);
        mainNotification.setContentIntent(pending);
        pref = currentContext.getSharedPreferences("Pref", Activity.MODE_PRIVATE);
    }
    public void setContent(String content){
        mainNotification.setContentText(content);
    }
    public void sendNotyfi(){
        if (pref.getBoolean("showNotificationWorkout", true))
            notifcationManager.notify(0, mainNotification.build());
    }
    public void deleteNotify(){
        notifcationManager.cancel(0);
    }

}
