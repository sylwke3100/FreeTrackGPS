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
            message.putExtra("dist", localRoute.getDistanceInKm());
            sendMessageToUi(MainActivityReceiver.COMMANDS.WORKOUT_DISTANCE, message);
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
        message.putExtra("lat", location.getLatitude());
        message.putExtra("lon", location.getLongitude());
        sendMessageToUi(MainActivityReceiver.COMMANDS.GPS_POS, message);
    }

    private void sendMessageToUi(String command, Intent intent) {
        intent.putExtra("command", command);
        intent.setAction(MainActivity.MAINACTIVITY_ACTION);
        currentContext.sendBroadcast(intent);
    }

    public void onStatusChanged(String s, int i, Bundle b) {
        switch (i) {
            case LocationProvider.AVAILABLE:
                sendMessageToUi(MainActivityReceiver.COMMANDS.GPS_ON, new Intent());
                if (localRoute.getStatus() == DefaultValues.routeStatus.pause) {
                    localRoute.unPause();
                    sendMessageToUi(MainActivityReceiver.COMMANDS.WORKOUT_ACTIVE, new Intent());
                }
                break;
            case LocationProvider.OUT_OF_SERVICE:
                sendMessageToUi(MainActivityReceiver.COMMANDS.GPS_OFF, new Intent());
                if (localRoute.getStatus() == DefaultValues.routeStatus.start) {
                    localRoute.pause();
                    sendMessageToUi(MainActivityReceiver.COMMANDS.WORKOUT_PAUSE, new Intent());
                }
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                sendMessageToUi(MainActivityReceiver.COMMANDS.GPS_OFF, new Intent());
                if (localRoute.getStatus() == DefaultValues.routeStatus.start) {
                    localRoute.pause();
                    sendMessageToUi(MainActivityReceiver.COMMANDS.WORKOUT_PAUSE, new Intent());
                }
                break;
        }
    }

    public void onProviderDisabled(String s) {
        sendMessageToUi(MainActivityReceiver.COMMANDS.GPS_OFF, new Intent());
        if (localRoute.getStatus() == DefaultValues.routeStatus.start) {
            localRoute.pause();
            sendMessageToUi("workoutPause", new Intent());
        }
    }

    public void onProviderEnabled(String s) {
        sendMessageToUi(MainActivityReceiver.COMMANDS.GPS_ON, new Intent());
        if (localRoute.getStatus() == DefaultValues.routeStatus.pause) {
            localRoute.unPause();
            sendMessageToUi("workoutActive", new Intent());
        }
    }
}
    
    
    

