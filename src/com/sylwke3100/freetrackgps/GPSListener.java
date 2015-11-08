package com.sylwke3100.freetrackgps;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.os.Bundle;


public class GPSListener implements LocationListener {
    private AreaNotificationManager notify;
    private Context currentContext;
    private RouteManager localRoute;
    private LocationSharing currentLocation;

    public GPSListener(RouteManager route, Context mainContext) {
        localRoute = route;
        notify = new AreaNotificationManager(mainContext, R.drawable.icon,
            mainContext.getString(R.string.app_name));
        this.currentContext = mainContext;
        localRoute.setAreaNotifyInstance(notify);
        currentLocation = new LocationSharing(mainContext);
    }

    public void onLocationChanged(Location location) {
        if (location != null)
            currentLocation.setCurrentLocation(location.getLatitude(), location.getLongitude());
        if (localRoute != null && location != null && location.hasAltitude() == true) {
            localRoute.addPoint(location);
            Intent message = new Intent();
            message.putExtra("command", "workoutDistance");
            message.putExtra("dist", localRoute.getDistanceInKm());
            message.setAction(MainActivity.MAINACTIVITY_ACTION);
            currentContext.sendBroadcast(message);
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
        Intent message = new Intent();
        message.putExtra("command", "gpsPos");
        message.putExtra("lat", location.getLatitude());
        message.putExtra("lon", location.getLongitude());
        message.setAction(MainActivity.MAINACTIVITY_ACTION);
        currentContext.sendBroadcast(message);
    }

    public void onStatusChanged(String s, int i, Bundle b) {
        switch (i) {
            case LocationProvider.AVAILABLE:
                Intent message = new Intent();
                message.putExtra("command", "gpsOn");
                message.setAction(MainActivity.MAINACTIVITY_ACTION);
                currentContext.sendBroadcast(message);
                if (localRoute.getStatus() == DefaultValues.routeStatus.pause) {
                    localRoute.unPause();
                    Intent messageIntent = new Intent();
                    messageIntent.putExtra("command", "workoutActive");
                    messageIntent.setAction(MainActivity.MAINACTIVITY_ACTION);
                    currentContext.sendBroadcast(messageIntent);
                }
                break;
            case LocationProvider.OUT_OF_SERVICE:
                Intent outOfServiceMessage = new Intent();
                outOfServiceMessage.putExtra("command", "gpsOff");
                outOfServiceMessage.setAction(MainActivity.MAINACTIVITY_ACTION);
                currentContext.sendBroadcast(outOfServiceMessage);
                if (localRoute.getStatus() == DefaultValues.routeStatus.start) {
                    localRoute.pause();
                    Intent messageIntent = new Intent();
                    messageIntent.putExtra("command", "workoutPause");
                    messageIntent.setAction(MainActivity.MAINACTIVITY_ACTION);
                    currentContext.sendBroadcast(messageIntent);
                }
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                Intent temporarilyUnavaiablemessage = new Intent();
                temporarilyUnavaiablemessage.putExtra("command", "gpsOff");
                temporarilyUnavaiablemessage.setAction(MainActivity.MAINACTIVITY_ACTION);
                currentContext.sendBroadcast(temporarilyUnavaiablemessage);
                if (localRoute.getStatus() == DefaultValues.routeStatus.start) {
                    localRoute.pause();
                    Intent messageIntent = new Intent();
                    messageIntent.putExtra("command", "workoutPause");
                    messageIntent.setAction(MainActivity.MAINACTIVITY_ACTION);
                    currentContext.sendBroadcast(messageIntent);
                }
                break;
        }
    }

    public void onProviderDisabled(String s) {
        Intent onProviderDisabledMessage = new Intent();
        onProviderDisabledMessage.putExtra("command", "gpsOff");
        onProviderDisabledMessage.setAction(MainActivity.MAINACTIVITY_ACTION);
        currentContext.sendBroadcast(onProviderDisabledMessage);
        if (localRoute.getStatus() == DefaultValues.routeStatus.start) {
            localRoute.pause();
            Intent messageIntent = new Intent();
            messageIntent.putExtra("command", "workoutPause");
            messageIntent.setAction(MainActivity.MAINACTIVITY_ACTION);
            currentContext.sendBroadcast(messageIntent);
        }
    }

    public void onProviderEnabled(String s) {
        Intent onProviderEnabledMessage = new Intent();
        onProviderEnabledMessage.putExtra("command", "gpsOn");
        onProviderEnabledMessage.setAction(MainActivity.MAINACTIVITY_ACTION);
        currentContext.sendBroadcast(onProviderEnabledMessage);
        if (localRoute.getStatus() == DefaultValues.routeStatus.pause) {
            localRoute.unPause();
            Intent messageIntent = new Intent();
            messageIntent.putExtra("command", "workoutActive");
            messageIntent.setAction(MainActivity.MAINACTIVITY_ACTION);
            currentContext.sendBroadcast(messageIntent);
        }
    }
}
    
    
    

