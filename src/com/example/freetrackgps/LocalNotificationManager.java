package com.example.freetrackgps;

import android.content.Context;
import android.app.NotificationManager;
import android.content.Intent;
import android.app.PendingIntent;
import android.support.v4.app.NotificationCompat;

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
        PendingIntent pending = PendingIntent.getActivity(localContext, 0, new Intent(),0);
        mainNotification.setContentIntent(pending);
    }
    public void setContent(String content){
        mainNotification.setContentText(content);
    }
    public void sendNotyfi(){
        notifcationManager.notify(0, mainNotification.build());
    }

}
