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
    private GPSRunnerServiceMessageController messageController;

    public GPSListener(RouteManager route, Context mainContext) {
        messageController = new GPSRunnerServiceMessageController(mainContext);
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
            messageController.sendMessageToGUI(MainActivityReceiver.COMMANDS.WORKOUT_DISTANCE, message);
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
        messageController.sendMessageToGUI(MainActivityReceiver.COMMANDS.GPS_POS, message);
    }

    public void onStatusChanged(String s, int status, Bundle b) {
        switch (status) {
            case LocationProvider.AVAILABLE:
                messageController.sendMessageToGUI(MainActivityReceiver.COMMANDS.GPS_ON, new Intent());
                if (localRoute.getStatus() == DefaultValues.routeStatus.pause) {
                    localRoute.unPause();
                    messageController.sendMessageToGUI(MainActivityReceiver.COMMANDS.WORKOUT_ACTIVE, new Intent());
                }
                break;
            case LocationProvider.OUT_OF_SERVICE:
                messageController.sendMessageToGUI(MainActivityReceiver.COMMANDS.GPS_OFF, new Intent());
                if (localRoute.getStatus() == DefaultValues.routeStatus.start) {
                    localRoute.pause();
                    messageController.sendMessageToGUI(MainActivityReceiver.COMMANDS.WORKOUT_PAUSE, new Intent());
                }
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                messageController.sendMessageToGUI(MainActivityReceiver.COMMANDS.GPS_OFF, new Intent());
                if (localRoute.getStatus() == DefaultValues.routeStatus.start) {
                    localRoute.pause();
                    messageController.sendMessageToGUI(MainActivityReceiver.COMMANDS.WORKOUT_PAUSE, new Intent());
                }
                break;
        }
    }

    public void onProviderDisabled(String s) {
        messageController.sendMessageToGUI(MainActivityReceiver.COMMANDS.GPS_OFF, new Intent());
        if (localRoute.getStatus() == DefaultValues.routeStatus.start) {
            localRoute.pause();
            messageController.sendMessageToGUI("workoutPause", new Intent());
        }
    }

    public void onProviderEnabled(String s) {
        messageController.sendMessageToGUI(MainActivityReceiver.COMMANDS.GPS_ON, new Intent());
        if (localRoute.getStatus() == DefaultValues.routeStatus.pause) {
            localRoute.unPause();
            messageController.sendMessageToGUI("workoutActive", new Intent());
        }
    }
}
    
    
    

