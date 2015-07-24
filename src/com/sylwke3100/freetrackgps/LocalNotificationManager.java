package com.sylwke3100.freetrackgps;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;

public class LocalNotificationManager {
    private SharedPreferences pref;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder globalNotification;
    private Context context;
    public  LocalNotificationManager(Context currentContext,
                                     int smallIcon,
                                     String title){
        globalNotification = new NotificationCompat.Builder(currentContext);
        globalNotification.setContentTitle(title);
        globalNotification.setSmallIcon(smallIcon);
        notificationManager = (NotificationManager) currentContext.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent mainActivityIntent = new Intent(currentContext, MainActivity.class);
        mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        globalNotification.setAutoCancel(false);
        globalNotification.setOngoing(true);
        mainActivityIntent.setAction(Intent.ACTION_MAIN);
        mainActivityIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PendingIntent pendingIntent = PendingIntent.getActivity(currentContext, 0, mainActivityIntent, 0);
        globalNotification.setContentIntent(pendingIntent);
        pref = currentContext.getSharedPreferences("Pref", Activity.MODE_PRIVATE);
        this.context = currentContext;
    }
    public void setContent(String content){
        globalNotification.setContentText(content);
    }
    public void setContent(int resurce){
        globalNotification.setContentText(context.getString(resurce));
    }
    public void sendNotify(){
        if (pref.getBoolean("showNotificationWorkout", true))
            notificationManager.notify(0, globalNotification.build());
    }
    public void deleteNotify(){
        notificationManager.cancel(0);
    }

}
