package com.sylwke3100.freetrackgps;


import android.content.Context;

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
        localListIgnore = ignorePointsController.getList();
        for (IgnorePointsListElement element : localListIgnore) {
            baseList.add(element.getPreparedHashMapToView());
        }
        return baseList;
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

}
