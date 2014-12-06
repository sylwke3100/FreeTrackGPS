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
	private Context context;
	private ArrayList<RouteElement> points = new ArrayList<RouteElement>();
	private int status;
	private long startTime; 
	private Location lastPosition;
	private double distance;
	private SimpleDateFormat p = new SimpleDateFormat("yyyy-MM-dd-HH_mm_ss");
	private GPXWriter GPX;
	private StringBuffer B;
	public RouteManager(Context C) {
		context = C;
	}
	public void start(){
		startTime = System.currentTimeMillis();
		status = 2;
		distance = 0.0;
		points.clear();
		B = new StringBuffer();
		ContextWrapper c = new ContextWrapper(context);
		File dir = new File(Environment.getExternalStorageDirectory()+"/workout/");
		if(!(dir.exists() && dir.isDirectory()))
		   dir.mkdir();
		B.append(Environment.getExternalStorageDirectory()+"/workout/");
		B.append(p.format(new Date(startTime))+ ".gpx");
		GPX = new GPXWriter(B.toString());
	}
	public void addPoint(Location currentLocation){
		Date D = new Date();
		if (status == 2){
			long currentTime = D.getTime();
			GPX.addPoint(currentLocation.getLatitude(), currentLocation.getLongitude(), currentLocation.getAltitude(), currentTime );	
			if (lastPosition != null)
				distance += lastPosition.distanceTo(currentLocation);
		}
		lastPosition = currentLocation;
	}
	public void pause(){
		status = 1;
	}
	public void unpause(){
		status = 2;
	}
	public double getDistance(){
		return distance;
	}
	public int getStatus(){
		return status;
	}
	public void stop(){
		status = 0;
		distance = 0.0;
		lastPosition = null;
		if(GPX.save() == true)
			Toast.makeText(context, "Save workout in "+ B.toString(), Toast.LENGTH_LONG).show();
		else
			Toast.makeText(context, "Error workout in ", Toast.LENGTH_LONG).show();
		B = null;
		GPX = null;
	}
}
