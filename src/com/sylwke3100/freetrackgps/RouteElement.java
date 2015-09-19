package com.sylwke3100.freetrackgps;

import java.text.SimpleDateFormat;

public class RouteElement {
    public double latitude, longitude, altitude;
    public long time;
    private static final SimpleDateFormat dateFormat =
        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public RouteElement(double latitude, double longitude, double altitude, long time) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.time = time;
    }

    public String getLatitude(){
        return Double.toString(latitude);
    }

    public String getLongitude(){
        return Double.toString(longitude);
    }

    public String getAltitude(){
        return Double.toString(altitude);
    }

    public String getPointTime(){
        return dateFormat.format(time);
    }
}
