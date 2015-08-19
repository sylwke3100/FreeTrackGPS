package com.sylwke3100.freetrackgps;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.os.Bundle;


public class GPSListener implements LocationListener  {
    private LocalNotificationManager notify;
    private Context currentContext;
    private MainActivityGuiOperations operations;
    private RouteManager localRoute;
    private GPSConnectionManager.gpsStatus gpsCurrentStatus;
    public GPSListener(MainActivityGuiOperations listenerOperations, RouteManager Rt,
        GPSConnectionManager.gpsStatus gpsCurrentStatus, Context mainContext){
        this.operations = listenerOperations;
        localRoute = Rt;
        this.gpsCurrentStatus  = gpsCurrentStatus;
        notify = new LocalNotificationManager(mainContext, R.drawable.icon, mainContext.getString(R.string.app_name));
        this.currentContext = mainContext;
        localRoute.setNotifyInstance(notify);
    }
    public void onLocationChanged(Location location) {
        if (localRoute != null && location != null && location.hasAltitude() == true ){
            localRoute.addPoint(location);
            this.operations.setWorkoutDistance(localRoute.getDistanceInKm());
            this.operations.setWorkoutSpeed(location.getSpeed());
            if(localRoute.getStatus() == DefaultValues.routeStatus.start) {
                if(localRoute.getPointStatus() == DefaultValues.areaStatus.ok)
                    notify.setContent(currentContext.getString(R.string.workoutDistanceLabel) + ": " + String.format("%.2f km ", localRoute.getDistanceInKm()));
                if(localRoute.getPointStatus() == DefaultValues.areaStatus.prohibited)
                    notify.setContent(currentContext.getString(R.string.ignorePointNotify));
                notify.sendNotify();
            }
        }
        this.operations.setGpsPosition(location.getLatitude(), location.getLongitude());
    }
    public void onStatusChanged(String s,
        int i,
        Bundle b) {
        switch(i){
            case LocationProvider.AVAILABLE:
                this.operations.setOnGPS();
                gpsCurrentStatus.status = true;
                if (localRoute.getStatus() == DefaultValues.routeStatus.pause)
                    localRoute.unPause();
                break;
            case LocationProvider.OUT_OF_SERVICE:
                this.operations.setOffGPS();
                gpsCurrentStatus.status = false;
                if (localRoute.getStatus() == DefaultValues.routeStatus.start)
                    localRoute.pause();
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                this.operations.setOffGPS();
                gpsCurrentStatus.status = false;
                if (localRoute.getStatus() == DefaultValues.routeStatus.start)
                    localRoute.pause();
                break;
        }
    }
    public void onProviderDisabled(String s) {
        this.operations.setOffGPS();
        gpsCurrentStatus.status = false;
        if (localRoute.getStatus() == DefaultValues.routeStatus.start)
            localRoute.pause();
    }
    public void onProviderEnabled(String s) {
        this.operations.setOnGPS();
        gpsCurrentStatus.status = true;
        if (localRoute.getStatus() == DefaultValues.routeStatus.pause)
            localRoute.unPause();
    }
}
    
    
    

