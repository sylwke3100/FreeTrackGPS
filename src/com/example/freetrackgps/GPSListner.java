package com.example.freetrackgps;

import android.app.Activity;
import android.app.Fragment;
import android.app.PendingIntent;
import android.content.Context;
import android.location.*;
import android.widget.TextView;

import java.util.List;

import android.os.Bundle;


public class GPSListner implements LocationListener  {
    private LocalNotificationManager notify;
    private Context currentContext;
	private TextView gpsPosition, gpsStatus, workoutDistance, workoutSpeed;
	private RouteManager localRoute;
    private GPSConnectionManager.gpsStatus gpsCurrentStatus;
	public GPSListner(List<TextView> E, RouteManager Rt, GPSConnectionManager.gpsStatus gpsCurrentStatus, Context mainContext){
		this.gpsPosition = E.get(0);
		this.gpsStatus = E.get(1);
		this.workoutDistance = E.get(2);
        this.workoutSpeed = E.get(3);
		localRoute = Rt;
        this.gpsCurrentStatus  = gpsCurrentStatus;
        notify = new LocalNotificationManager(mainContext, R.drawable.icon, mainContext.getString(R.string.app_name));
        this.currentContext = mainContext;
        localRoute.setNotifiy(notify);
	}
    public void onLocationChanged(Location location) {
        String message = String.format( " %1$s %2$s %3$s",String.format( "%.2f", location.getLongitude()), String.format( "%.2f",location.getLatitude()), String.format( "%.2f",location.getAltitude()));
        if (localRoute != null && location != null && location.hasAltitude() == true ){
        	localRoute.addPoint(location);
        	this.workoutDistance.setText(String.format("%.2f km", localRoute.getDistance()));
            this.workoutSpeed.setText(String.format("%d", (int)location.getSpeed() ) +" km/h");
            if(localRoute.getStatus() == RouteManager.routeStatus.start) {
                notify.setContent(currentContext.getString(R.string.workoutDistanceLabel) + ": " + String.format("%.2f km", localRoute.getDistance())+" Speed: "+String.format("%d", (int)location.getSpeed() ) +" km/h");
                notify.sendNotyfi();
            }
        }
        this.gpsPosition.setText(message);
    }
    public void onStatusChanged(String s, int i, Bundle b) {
    	switch(i){
    	case LocationProvider.AVAILABLE:
    		this.gpsStatus.setText(currentContext.getString(R.string.onLabal));
            gpsCurrentStatus.status = true;
    		if (localRoute.getStatus() == RouteManager.routeStatus.pause)
    			localRoute.unpause();
    		break;
    	case LocationProvider.OUT_OF_SERVICE:
    		this.gpsStatus.setText(currentContext.getString(R.string.offLabel));
            gpsCurrentStatus.status = false;
    		if (localRoute.getStatus() == RouteManager.routeStatus.start)
    			localRoute.pause();
    		break;
    	case LocationProvider.TEMPORARILY_UNAVAILABLE:
    		this.gpsStatus.setText(currentContext.getString(R.string.offLabel));
            gpsCurrentStatus.status = false;
    		if (localRoute.getStatus() == RouteManager.routeStatus.start)
    			localRoute.pause();
    		break;
    	}
    }
    public void onProviderDisabled(String s) {
    	this.gpsStatus.setText("Off");
        gpsCurrentStatus.status = false;
    	if (localRoute.getStatus() == RouteManager.routeStatus.start)
    		localRoute.pause();
    }
    public void onProviderEnabled(String s) {
    	this.gpsStatus.setText("On");
        gpsCurrentStatus.status = true;
    	if (localRoute.getStatus() == RouteManager.routeStatus.pause)
    		localRoute.unpause();
    }		
}
    
    
    

