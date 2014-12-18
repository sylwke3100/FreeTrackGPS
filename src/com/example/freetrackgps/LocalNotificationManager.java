package com.example.freetrackgps;

import android.content.Context;
import android.app.NotificationManager;
import android.content.Intent;
import android.app.PendingIntent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public class LocalNotificationManager {
    private Context localContext;
    private int icon;
    private String title;
    private NotificationManager notifcationManager;
    private NotificationCompat.Builder mainNotification;
    public  LocalNotificationManager(Context currentContext, int icon, String title){
        this.localContext = currentContext;
        this.icon = icon;
        this.title = title;
        mainNotification = new NotificationCompat.Builder(currentContext);
        mainNotification.setContentTitle(this.title);
        mainNotification.setSmallIcon(R.drawable.icon);
        notifcationManager = (NotificationManager) currentContext.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(localContext, MainActivity.class);
        TaskStackBuilder currentstackBuilder = TaskStackBuilder.create(localContext);
        currentstackBuilder.addParentStack(MainActivity.class);
        currentstackBuilder.addNextIntent(intent);
        PendingIntent pending = currentstackBuilder.getPendingIntent(0,  PendingIntent.FLAG_UPDATE_CURRENT);
        mainNotification.setContentIntent(pending);
    }
    public void setContent(String content){
        mainNotification.setContentText(content);
    }
    public void sendNotyfi(){
        notifcationManager.notify(0, mainNotification.build());
    }
    public void deleteNotify(){
        notifcationManager.cancel(0);
    }

}
