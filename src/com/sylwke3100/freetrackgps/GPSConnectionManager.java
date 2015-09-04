package com.sylwke3100.freetrackgps;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;


public class GPSConnectionManager {
    private Context mainContext;
    private SharedPreferences sharedPrefs;


    public class gpsStatus {
        boolean status;
    }


    private gpsStatus gpsCurrentStatus;

    public GPSConnectionManager(Context context) {
        this.mainContext = context;
        sharedPrefs = mainContext.getSharedPreferences("Pref", Activity.MODE_PRIVATE);
        gpsCurrentStatus = new gpsStatus();
    }

    public void onCreateConnection(MainActivityGuiOperations operations, LocationManager service,
        RouteManager currentRoute) {
        int[] timeSettingArray = mainContext.getResources().getIntArray(R.array.timeArray);
        int[] distanceSettingArray = mainContext.getResources().getIntArray(R.array.distanceArray);
        if (service != null) {
            service.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                timeSettingArray[(sharedPrefs.getInt("time", DefaultValues.defaultMinSpeedIndex))],
                distanceSettingArray[(sharedPrefs.getInt("distance", 1))],
                new GPSListener(operations, currentRoute, gpsCurrentStatus, this.mainContext));
            operations.setOnGPS();
            Location lastLocation =
                (Location) service.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            gpsCurrentStatus.status = true;
            if (lastLocation != null) {
                if (currentRoute.getStatus() != DefaultValues.routeStatus.stop)
                    operations
                        .setGpsPosition(lastLocation.getLatitude(), lastLocation.getLongitude());
            }
        } else {
            operations.setOffGPS();
            service.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                timeSettingArray[(sharedPrefs.getInt("time", DefaultValues.defaultMinSpeedIndex))],
                distanceSettingArray[(sharedPrefs.getInt("distance", 1))],
                new GPSListener(operations, currentRoute, gpsCurrentStatus, this.mainContext));
        }
    }

    public boolean getStatus() {
        return gpsCurrentStatus.status;
    }
}
