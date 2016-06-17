package com.sylwke3100.freetrackgps;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.StrictMode;
import android.os.Vibrator;
import android.util.Log;

public class VibrateNotificationManager {
    private Vibrator notificationService;
    private SharedPreferences sharePrefs;
    private int distanceToNotification = 0;

    public VibrateNotificationManager(Context mainContext) {
        notificationService = (Vibrator) mainContext.getSystemService(Context.VIBRATOR_SERVICE);
        sharePrefs = mainContext.getSharedPreferences(DefaultValues.prefs, Activity.MODE_PRIVATE);
    }

    private boolean getStatusSetting() {
        return sharePrefs.getBoolean("vibrateNotification", false);
    }

    public void run() {
        if (getStatusSetting())
            notificationService.vibrate(200);
    }

    public void activateNotify(Double distance) {
        int integerDistance = distance.intValue();
        if (integerDistance != distanceToNotification){
            run();
            distanceToNotification = integerDistance;
        }
    }

    public void clear() {
        distanceToNotification = 0;
    }
}
