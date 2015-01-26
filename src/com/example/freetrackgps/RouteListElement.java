package com.example.freetrackgps;

public class RouteListElement {
    int id;
    long startTime;
    double distance;
    public RouteListElement(int id, long startTime, double distance){
        this.id = id;
        this.startTime = startTime;
        this.distance = distance;
    }
}
