package com.sylwke3100.freetrackgps;


import java.util.HashMap;

public class IgnorePointsListElement {
    public Double latitude;
    public Double longitude;
    public String name;

    public IgnorePointsListElement(Double latitude, Double longitude, String name) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
    }

    public HashMap<String, String> getPreparedHashMapToView() {
        HashMap<String, String> pointHashMap = new HashMap<String, String>();
        pointHashMap.put("points", Double.toString(latitude) + "-" + Double.toString(longitude));
        pointHashMap.put("name", name);
        return pointHashMap;
    }
}
