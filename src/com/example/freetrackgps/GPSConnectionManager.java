package com.example.freetrackgps;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.widget.TextView;

import java.util.List;


public class GPSConnectionManager {
    private Context mainContext;
    private SharedPreferences sharedPrefs;
    public class gpsStatus{
        boolean status;
    }
    private gpsStatus gpsCurrentStatus;
    public GPSConnectionManager(Context context){
        mainContext = context;
        sharedPrefs = mainContext.getSharedPreferences("Pref", Activity.MODE_PRIVATE);
        gpsCurrentStatus = new gpsStatus();
    }
    public void onCreateConnection(List<TextView> textViewElements, LocationManager service, RouteManager currentRoute){
        int[] time = mainContext.getResources().getIntArray(R.array.timeArray);
        int[] distance = mainContext.getResources().getIntArray(R.array.distanceArray);
        if (service != null){
            service.requestLocationUpdates(LocationManager.GPS_PROVIDER, time[(sharedPrefs.getInt("time", 1))], distance[(sharedPrefs.getInt("distance",1))], new GPSListner(textViewElements, currentRoute, gpsCurrentStatus) );
            textViewElements.get(1).setText(mainContext.getString(R.string.onLabal));
            Location L= (Location)service.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (L != null){
                gpsCurrentStatus.status = true;
                String message = String.format( " %1$s %2$s", String.format( "%.2f", L.getLongitude()), String.format( "%.2f", L.getLatitude()),  String.format( "%.2f", L.getAltitude()) );
                if (currentRoute.getStatus() != RouteManager.routeStatus.stop)
                    textViewElements.get(0).setText(message);
            }
        }
        else{
            textViewElements.get(1).setText(mainContext.getString(R.string.offLabel));
            service.requestLocationUpdates(LocationManager.GPS_PROVIDER, time[(sharedPrefs.getInt("time", 1))], distance[(sharedPrefs.getInt("distance",1))], new GPSListner(textViewElements, currentRoute, gpsCurrentStatus) );
        }
    }
    public boolean getStatus(){
        return gpsCurrentStatus.status;
    }
}
