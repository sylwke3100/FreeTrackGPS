package com.sylwke3100.freetrackgps;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.os.Bundle;


public class GPSListener implements LocationListener {
    private LocalNotificationManager notify;
    private Context currentContext;
    private MainActivityGuiOperations guiOperations;
    private RouteManager localRoute;
    private GPSConnectionManager.gpsStatus gpsCurrentStatus;
    private LocationSharing currentLocation;

    public GPSListener(MainActivityGuiOperations listenerOperations, RouteManager route,
        GPSConnectionManager.gpsStatus gpsCurrentStatus, Context mainContext) {
        this.guiOperations = listenerOperations;
        localRoute = route;
        this.gpsCurrentStatus = gpsCurrentStatus;
        notify = new LocalNotificationManager(mainContext, R.drawable.icon,
            mainContext.getString(R.string.app_name));
        this.currentContext = mainContext;
        localRoute.setNotifyInstance(notify);
        currentLocation = new LocationSharing(mainContext);
    }

    public void onLocationChanged(Location location) {
        if (location != null)
            currentLocation.setCurrentLocation(location.getLatitude(), location.getLongitude());
        if (localRoute != null && location != null && location.hasAltitude() == true) {
            localRoute.addPoint(location);
            this.guiOperations.setWorkoutDistance(localRoute.getDistanceInKm());
            if (localRoute.getStatus() == DefaultValues.routeStatus.start) {
                if (localRoute.getPointStatus() == DefaultValues.areaStatus.ok)
                    notify.setContent(
                        currentContext.getString(R.string.workoutDistanceLabel) + ": " + String
                            .format("%.2f km ", localRoute.getDistanceInKm()));
                if (localRoute.getPointStatus() == DefaultValues.areaStatus.prohibited)
                    notify.setContent(currentContext.getString(R.string.ignorePointNotify));
                notify.sendNotify();
            }
        }
        this.guiOperations.setGpsPosition(location.getLatitude(), location.getLongitude());
    }

    public void onStatusChanged(String s, int i, Bundle b) {
        switch (i) {
            case LocationProvider.AVAILABLE:
                this.guiOperations.setOnGPS();
                gpsCurrentStatus.status = true;
                if (localRoute.getStatus() == DefaultValues.routeStatus.pause) {
                    localRoute.unPause();
                    guiOperations.setWorkoutActive();
                }
                break;
            case LocationProvider.OUT_OF_SERVICE:
                this.guiOperations.setOffGPS();
                gpsCurrentStatus.status = false;
                if (localRoute.getStatus() == DefaultValues.routeStatus.start) {
                    localRoute.pause();
                    guiOperations.setWorkoutPause();
                }
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                this.guiOperations.setOffGPS();
                gpsCurrentStatus.status = false;
                if (localRoute.getStatus() == DefaultValues.routeStatus.start) {
                    localRoute.pause();
                    guiOperations.setWorkoutPause();
                }
                break;
        }
    }

    public void onProviderDisabled(String s) {
        this.guiOperations.setOffGPS();
        gpsCurrentStatus.status = false;
        if (localRoute.getStatus() == DefaultValues.routeStatus.start) {
            localRoute.pause();
            guiOperations.setWorkoutPause();
        }
    }

    public void onProviderEnabled(String s) {
        this.guiOperations.setOnGPS();
        gpsCurrentStatus.status = true;
        if (localRoute.getStatus() == DefaultValues.routeStatus.pause) {
            localRoute.unPause();
            guiOperations.setWorkoutActive();
        }
    }
}
    
    
    

