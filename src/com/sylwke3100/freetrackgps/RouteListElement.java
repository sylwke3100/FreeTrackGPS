package com.sylwke3100.freetrackgps;

public class RouteListElement {
    int id;
    long startTime;
    double distance;
    String name = "";

    public RouteListElement(int id, long startTime, double distance) {
        this.id = id;
        this.startTime = startTime;
        this.distance = distance;
    }

    public RouteListElement(int id, long startTime, double distance, String name) {
        this.id = id;
        this.startTime = startTime;
        this.distance = distance;
        this.name = name;
    }

    public String getPreparedName() {
        if (name == null)
            return "";
        else if (name.isEmpty())
            return "";
        else
            return " - " + name;
    }
}
