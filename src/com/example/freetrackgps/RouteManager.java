package com.example.freetrackgps;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.PendingIntent;
import android.os.Environment;
import android.widget.Toast;

import android.content.Context;
import android.content.ContextWrapper;

import java.io.File;

import android.location.Location;


public class RouteManager {
    public enum routeStatus{
        stop,
        pause,
        start
    }
	private Context context;
	private ArrayList<RouteElement> points = new ArrayList<RouteElement>();
	private routeStatus status;
	private long startTime; 
	private Location lastPosition;
	private double distance;
	private SimpleDateFormat p = new SimpleDateFormat("yyyy-MM-dd-HH_mm_ss");
	private GPXWriter gpx;
    private LocalNotificationManager localNotify;
	private StringBuffer fileNameBuffer;
	public RouteManager(Context C) {
		context = C;
        status = routeStatus.stop;
	}
	public void start(){
		startTime = System.currentTimeMillis();
		status = routeStatus.start;
		distance = 0.0;
		points.clear();
		fileNameBuffer = new StringBuffer();
		ContextWrapper c = new ContextWrapper(context);
		File dir = new File(Environment.getExternalStorageDirectory()+"/workout/");
		if(!(dir.exists() && dir.isDirectory()))
		   dir.mkdir();
		fileNameBuffer.append(Environment.getExternalStorageDirectory() + "/workout/");
		fileNameBuffer.append(p.format(new Date(startTime)) + ".gpx");
		gpx = new GPXWriter(fileNameBuffer.toString());
        localNotify.setContent(context.getString(R.string.workoutDistanceLabel)+": " +  String.format("%.2fm", distance));
        localNotify.sendNotyfi();
	}
	public void addPoint(Location currentLocation){
		Date D = new Date();
		if (status == routeStatus.start){
			long currentTime = D.getTime();
			gpx.addPoint(currentLocation.getLatitude(), currentLocation.getLongitude(), currentLocation.getAltitude(), currentTime);	
			if (lastPosition != null)
				distance += lastPosition.distanceTo(currentLocation);
		}
		lastPosition = currentLocation;
	}
	public void pause(){
		status = routeStatus.pause;
	}
	public void unpause(){
		status = routeStatus.start;
	}
	public double getDistance(){
		return distance;
	}
	public routeStatus getStatus(){
		return status;
	}
	public void stop(){
		status = routeStatus.stop;
		distance = 0.0;
		lastPosition = null;
		if(gpx.save() == true)
			Toast.makeText(context, context.getString(R.string.SaveTrueInfo)+" "+ fileNameBuffer.toString(), Toast.LENGTH_LONG).show();
		else
			Toast.makeText(context, context.getString(R.string.SaveFalseInfo), Toast.LENGTH_LONG).show();
		fileNameBuffer = null;
		gpx = null;
        localNotify.deleteNotify();
	}
    public void setNotifiy(LocalNotificationManager notifiy){
        this.localNotify = notifiy;
    }
}
