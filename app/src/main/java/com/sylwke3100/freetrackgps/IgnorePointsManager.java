package com.sylwke3100.freetrackgps;


import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IgnorePointsManager {
    private Context localContext;
    private IgnorePointsDatabaseController ignorePointsController;
    private List<IgnorePointsListElement> localListIgnore;

    public IgnorePointsManager(Context globalContext) {
        localContext = globalContext;
        ignorePointsController = new IgnorePointsDatabaseController(globalContext);

    }

    public ArrayList<HashMap<String, String>> getIgnorePointsPreparedList() {
        ArrayList<HashMap<String, String>> baseList = new ArrayList<HashMap<String, String>>();
        loadList();
        for (IgnorePointsListElement element : localListIgnore) {
            baseList.add(element.getPreparedHashMapToView());
        }
        return baseList;
    }

    public void loadList(){
        localListIgnore = ignorePointsController.getList();
    }

    public IgnorePointsListElement getIgnorePoint(int id){
        return localListIgnore.get(id);
    }

    public void deleteIgnorePoint(IgnorePointsListElement point){
        ignorePointsController.deletePoint(point.latitude, point.longitude);
    }

    public boolean addIgnorePoint(double latitude,double longitude,String name){
        return ignorePointsController.addPoint(latitude, longitude, name);
    }
    public boolean findIgnorePoint(Location cLocation) {
        for (IgnorePointsListElement element : ignorePointsController.getList()) {
            Location current = new Location(LocationManager.GPS_PROVIDER);
            current.setLatitude(element.latitude);
            current.setLongitude(element.longitude);
            float distance = current.distanceTo(cLocation);
            if (distance < 100)
                return true;
        }
        return false;
    }


}
