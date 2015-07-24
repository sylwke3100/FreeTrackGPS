package com.sylwke3100.freetrackgps;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;

import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class RouteManager {
    public static int STATUS_OK = 1;
    public static int STATUS_PROHIBITED = 0;
    private int currentStatus = 1;
    private Context context;
    private DefaultValues.routeStatus status;
    private long startTime;
    private Location lastPosition;
    private double distance;
    private LocalNotificationManager localNotify;
    private DatabaseManager currentDB;
    private long currentId;
    private List<HashMap<String, Double>> globalList;

    public RouteManager(Context C) {
        context = C;
        status = DefaultValues.routeStatus.stop;
        currentDB = new DatabaseManager(C);
        globalList = currentDB.getIgnorePointsList();
    }

    public int getPointStatus(){
        return currentStatus;
    }

    public boolean findPointInIgnore(Location cLocation){
        for(HashMap<String, Double> element: globalList) {
            Location current = new Location(LocationManager.GPS_PROVIDER);
            current.setLatitude(element.get("lat"));
            current.setLongitude(element.get("lon"));
            float distance = current.distanceTo(cLocation);
            if (distance < 100)
                return true;
        }
        return false;
    }
    public void start(){
        startTime = System.currentTimeMillis();
        currentId = currentDB.startWorkout(startTime);
        status = DefaultValues.routeStatus.start;
        distance = 0.0;
        localNotify.setContent(context.getString(R.string.workoutDistanceLabel)+": " +  String.format("%.2f km", getDistanceInKm()));
        localNotify.sendNotify();
    }
    public void addPoint(Location currentLocation){
        Date D = new Date();
        if (status == DefaultValues.routeStatus.start){
            long currentTime = D.getTime();
            if (findPointInIgnore(currentLocation) == false) {
                RouteElement routePoint = new RouteElement(currentLocation.getLatitude(), currentLocation.getLongitude(), currentLocation.getAltitude(), currentTime);
                if (lastPosition != null)
                    distance += lastPosition.distanceTo(currentLocation);
                currentDB.addPoint(currentId, routePoint, distance);
                currentStatus = 1;
            }
            else
                currentStatus = 0;
        }
        lastPosition = currentLocation;
    }
    public void pause(){
        status = DefaultValues.routeStatus.pause;
    }
    public void unPause(){
        status = DefaultValues.routeStatus.start;
    }
    public double getDistanceInKm(){
        return distance/1000;
    }
    public DefaultValues.routeStatus getStatus(){
        return status;
    }
    public void stop(){
        status = DefaultValues.routeStatus.stop;
        distance = 0.0;
        lastPosition = null;
        localNotify.deleteNotify();
        currentId = -1;
    }
    public void setNotifyInstance(LocalNotificationManager notify){
        this.localNotify = notify;
    }
}
