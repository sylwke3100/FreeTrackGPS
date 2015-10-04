package com.sylwke3100.freetrackgps;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Vibrator;

public class VibrateNotificationManager {
    private Vibrator notificationService;
    private SharedPreferences sharePrefs;
    private double lastDistance = 0, distanceToNotification = 0;
    private Location lastPoint;
    private boolean isPoint = false;

    public VibrateNotificationManager(Context mainContext) {
        notificationService = (Vibrator) mainContext.getSystemService(Context.VIBRATOR_SERVICE);
        sharePrefs = mainContext.getSharedPreferences("Pref", Activity.MODE_PRIVATE);
        lastPoint = new Location(LocationManager.GPS_PROVIDER);
    }

    private boolean getStatusSetting() {
        return sharePrefs.getBoolean("vibrateNotification", false);
    }

    public void run() {
        if (getStatusSetting())
            notificationService.vibrate(200);
    }

    public void proccesNotify(Location current) {
        if (isPoint) {
            float currentDistance = current.distanceTo(lastPoint);
            if (distanceToNotification < 1000) {
                distanceToNotification += currentDistance - lastDistance;
                lastDistance = currentDistance;
            } else {
                run();
                clear(false);
            }
        } else {
            lastPoint = current;
            isPoint = true;
        }
    }

    public void clear(boolean clearPermanently) {
        distanceToNotification = 0;
        if (clearPermanently)
            lastDistance = 0;
        isPoint = false;
    }
}
