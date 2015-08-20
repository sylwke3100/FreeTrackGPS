package com.sylwke3100.freetrackgps;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;


public class GPSConnectionManager {
    private Context mainContext;
    private SharedPreferences sharedPrefs;

    public class gpsStatus{
        boolean status;
    }
    private gpsStatus gpsCurrentStatus;
    public GPSConnectionManager(Context context){
        this.mainContext = context;
        sharedPrefs = mainContext.getSharedPreferences("Pref", Activity.MODE_PRIVATE);
        gpsCurrentStatus = new gpsStatus();
    }
    public void onCreateConnection(MainActivityGuiOperations operations,
        LocationManager service,
        RouteManager currentRoute){
        int[] time = mainContext.getResources().getIntArray(R.array.timeArray);
        int[] distance = mainContext.getResources().getIntArray(R.array.distanceArray);
        if (service != null){
            service.requestLocationUpdates(LocationManager.GPS_PROVIDER, time[(sharedPrefs.getInt("time", DefaultValues.defaultMinSpeedIndex))], distance[(sharedPrefs.getInt("distance",1))], new GPSListener(operations, currentRoute, gpsCurrentStatus, this.mainContext) );
            operations.setOnGPS();
            Location L= (Location)service.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            gpsCurrentStatus.status = true;
            if (L != null){
                if (currentRoute.getStatus() != DefaultValues.routeStatus.stop)
                    operations.setGpsPosition(L.getLatitude(), L.getLongitude());
            }
        }
        else{
            operations.setOffGPS();
            service.requestLocationUpdates(LocationManager.GPS_PROVIDER, time[(sharedPrefs.getInt("time", DefaultValues.defaultMinSpeedIndex))], distance[(sharedPrefs.getInt("distance",1))], new GPSListener(operations, currentRoute, gpsCurrentStatus, this.mainContext) );
        }
    }
    public boolean getStatus(){
        return gpsCurrentStatus.status;
    }
}
