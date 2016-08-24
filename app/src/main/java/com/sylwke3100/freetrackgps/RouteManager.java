package com.sylwke3100.freetrackgps;

import android.content.Context;
import android.location.Location;

import java.util.Date;


public class RouteManager {
    private DefaultValues.areaStatus currentStatus;
    private Context context;
    private DefaultValues.routeStatus status;
    private long startTime;
    private Location lastPosition;
    private double distance;
    private AreaNotificationManager areaNotification;
    private long currentId;
    private IgnorePointsManager ignorePointsManager;
    private VibrateNotificationManager vibrateNotificationManager;
    private WorkoutDatabaseController workoutController;
    private IgnorePointsDatabaseController ignorePointsController;

    public RouteManager(Context globalContext) {
        context = globalContext;
        status = DefaultValues.routeStatus.stop;
        workoutController = new WorkoutDatabaseController(globalContext);
        currentStatus = DefaultValues.areaStatus.ok;
        vibrateNotificationManager = new VibrateNotificationManager(globalContext);
        ignorePointsManager = new IgnorePointsManager(globalContext);
    }

    public DefaultValues.areaStatus getPointStatus() {
        return currentStatus;
    }


    public void start() {
        ignorePointsManager.loadList();
        startTime = System.currentTimeMillis();
        currentId = workoutController.start(startTime);
        status = DefaultValues.routeStatus.start;
        distance = 0.0;
        areaNotification.setContent(context.getString(R.string.workoutDistanceLabel) + ": " + String
                .format("%.2f km", getDistanceInKm()));
        areaNotification.sendNotify();
    }

    public void addPoint(Location currentLocation) {
        Date currentDate = new Date();
        if (status == DefaultValues.routeStatus.start) {
            long currentTime = currentDate.getTime();
            if (ignorePointsManager.findIgnorePoint(currentLocation) == false) {
                RouteElement routePoint =
                        new RouteElement(currentLocation.getLatitude(), currentLocation.getLongitude(),
                                currentLocation.getAltitude(), currentTime);
                if (lastPosition != null)
                    distance += lastPosition.distanceTo(currentLocation);
                vibrateNotificationManager.activateNotify(getDistanceInKm());
                workoutController.addPoint(currentId, routePoint, distance);
                currentStatus = DefaultValues.areaStatus.ok;
                lastPosition = currentLocation;
            } else
                currentStatus = DefaultValues.areaStatus.prohibited;
        }
    }

    public void pause() {
        status = DefaultValues.routeStatus.pause;
    }

    public void unPause() {
        status = DefaultValues.routeStatus.start;
        lastPosition = null;
    }

    public long getCurrentId(){
        return currentId;
    }

    public double getDistanceInKm() {
        return distance / 1000;
    }

    public DefaultValues.routeStatus getStatus() {
        return status;
    }

    public void stop() {
        status = DefaultValues.routeStatus.stop;
        distance = 0.0;
        lastPosition = null;
        areaNotification.deleteNotify();
        currentId = -1;
        vibrateNotificationManager.clear();
    }

    public void setAreaNotifyInstance(AreaNotificationManager notify) {
        this.areaNotification = notify;
        areaNotification.deleteNotify();
    }
}
