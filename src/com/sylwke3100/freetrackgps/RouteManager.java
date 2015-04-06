package com.sylwke3100.freetrackgps;

import android.content.Context;
import android.location.Location;

import java.util.Date;


public class RouteManager {
	private Context context;
	private DefaultValues.routeStatus status;
	private long startTime; 
	private Location lastPosition;
	private double distance;
  private LocalNotificationManager localNotify;
  private DatabaseManager currentDB;
  private long currentId;

	public RouteManager(Context C) {
		context = C;
    status = DefaultValues.routeStatus.stop;
    currentDB = new DatabaseManager(C);
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
            RouteElement routePoint = new RouteElement(currentLocation.getLatitude(), currentLocation.getLongitude(), currentLocation.getAltitude(), currentTime);
			if (lastPosition != null)
				distance += lastPosition.distanceTo(currentLocation);
            currentDB.addPoint(currentId, routePoint, distance);
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
