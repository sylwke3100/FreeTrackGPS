package com.example.freetrackgps;

public class RouteElement {
	public double lat, lon, alt;
	public long time;
	public RouteElement(double la, double lo, double al, long t) {
		this.lat = la;
		this.lon = lo;
		this.alt = al;
		this.time = t;
	}
}
