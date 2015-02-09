package com.sylwke3100.freetrackgps;

public class RouteElement {
	public double latitude, longitude, altitude;
	public long time;
	public RouteElement(double latitude,
                        double longitude,
                        double altitude,
                        long time) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
		this.time = time;
	}
}
