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
import com.example.freetrackgps.DefaultValues;


public class RouteManager {
	private Context context;
	private ArrayList<RouteElement> points = new ArrayList<RouteElement>();
	private DefaultValues.routeStatus status;
	private long startTime; 
	private Location lastPosition;
	private double distance;
	private SimpleDateFormat p = new SimpleDateFormat("yyyy-MM-dd-HH_mm_ss");
	private GPXWriter gpx;
    private LocalNotificationManager localNotify;
	private StringBuffer fileNameBuffer;
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
		points.clear();
		fileNameBuffer = new StringBuffer();
		ContextWrapper c = new ContextWrapper(context);
		File dir = new File(Environment.getExternalStorageDirectory()+"/workout/");
		if(!(dir.exists() && dir.isDirectory()))
		   dir.mkdir();
		fileNameBuffer.append(Environment.getExternalStorageDirectory() + "/workout/");
		fileNameBuffer.append(p.format(new Date(startTime)) + ".gpx");
		gpx = new GPXWriter(fileNameBuffer.toString());
        localNotify.setContent(context.getString(R.string.workoutDistanceLabel)+": " +  String.format("%.2fkm", getDistance()));
        localNotify.sendNotyfi();
	}
	public void addPoint(Location currentLocation){
		Date D = new Date();
		if (status == DefaultValues.routeStatus.start){
			long currentTime = D.getTime();
			gpx.addPoint(currentLocation.getLatitude(), currentLocation.getLongitude(), currentLocation.getAltitude(), currentTime);	
			if (lastPosition != null)
				distance += lastPosition.distanceTo(currentLocation);
            currentDB.addPoint(currentId, new RouteElement(currentLocation.getLatitude(), currentLocation.getLongitude(), currentLocation.getAltitude(), currentTime), distance);
		}
		lastPosition = currentLocation;
	}
	public void pause(){
		status = DefaultValues.routeStatus.pause;
	}
	public void unpause(){
		status = DefaultValues.routeStatus.start;
	}
	public double getDistance(){
		return distance/1000;
	}
	public DefaultValues.routeStatus getStatus(){
		return status;
	}
	public void stop(){
		status = DefaultValues.routeStatus.stop;
		distance = 0.0;
		lastPosition = null;
		if(gpx.save() == true)
			Toast.makeText(context, context.getString(R.string.SaveTrueInfo)+" "+ fileNameBuffer.toString(), Toast.LENGTH_LONG).show();
		else
			Toast.makeText(context, context.getString(R.string.SaveFalseInfo), Toast.LENGTH_LONG).show();
		fileNameBuffer = null;
		gpx = null;
        localNotify.deleteNotify();
        currentId = -1;
	}
    public void setNotifiy(LocalNotificationManager notifiy){
        this.localNotify = notifiy;
    }
}
