package com.sylwke3100.freetrackgps;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class WorkoutsPreviewOperations {
    private SharedPreferences sharePrefs;
    private DatabaseManager currentDataBase;
    private List<RouteListElement> elements;
    private Context localContext;
    private static final SimpleDateFormat formatDate = new SimpleDateFormat("dd.MM.yyyy HH:mm ");
    private SimpleDateFormat fileGpxFormat = new SimpleDateFormat("yyyy-MM-dd-HH_mm_ss");
    private DatabaseTimeFilter timeFilter;
    private DatabaseNameFilter nameFilter;
    WorkoutsPreviewOperations(Context context){
        currentDataBase = new DatabaseManager(context);
        localContext = context;
        sharePrefs = context.getSharedPreferences("Pref", Activity.MODE_PRIVATE);
        timeFilter = new DatabaseTimeFilter(context);
        nameFilter = new DatabaseNameFilter(context);
    }

    public ArrayList<HashMap<String,String>> getUpdatedWorkoutsList(){
        List<DatabaseFilter> filtersList = new LinkedList<DatabaseFilter>();
        filtersList.add(timeFilter);
        filtersList.add(nameFilter);
        elements = currentDataBase.getRoutesList(filtersList);
        ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
        for(RouteListElement element: elements){
            HashMap<String,String> simpleItem = new HashMap<String, String>();
            simpleItem.put("time", formatDate.format(element.startTime) + element.getPreparedName());
            simpleItem.put("distance",  String.format("%.2f km", element.distance/1000));
            list.add(simpleItem);
        }
        return list;
    }

    public void deleteWorkout(int id){
        currentDataBase.deleteRoute(elements.get(id).id);
    }

    public void exportWorkout(int id){
        RouteListElement object= elements.get(id);
        List<RouteElement> pointsWorkout = currentDataBase.getPointsInRoute(object.id);
        GPXWriter gpx;
        StringBuffer fileNameBuffer = new StringBuffer();
        File dir = new File(Environment.getExternalStorageDirectory() + DefaultValues.defaultFolderWithWorkout);
        if(!(dir.exists() && dir.isDirectory()))
            dir.mkdir();
        fileNameBuffer.append(Environment.getExternalStorageDirectory() + DefaultValues.defaultFolderWithWorkout);
        fileNameBuffer.append(fileGpxFormat.format(new Date(object.startTime)) + "." + DefaultValues.defaultFileFormat);
        gpx = new GPXWriter(fileNameBuffer.toString(), object.startTime, object.name);
        for (RouteElement point: pointsWorkout){
            gpx.addPoint(point);
        }
        if(gpx.save())
            Toast.makeText(localContext, localContext.getString(R.string.SaveTrueInfo) + " " + fileNameBuffer.toString(), Toast.LENGTH_LONG).show();
        else
            Toast.makeText(localContext, localContext.getString(R.string.SaveFalseInfo), Toast.LENGTH_LONG).show();
    }

    public void setTimeOneFilter(long time){
        timeFilter.setViewFilter(time);

    }

    public void setNameFilter(String name){
        nameFilter.setViewFilter(name);
    }


    public boolean getStatusTimeFilter() {;
        if (timeFilter.isActive())
            return true;
        else
            return false;
    }

    public boolean getStatusNameFilter() {
        if (nameFilter.isActive())
            return true;
        else
            return false;
    }

    public String getFilterName(){
        return sharePrefs.getString("filterName", "");
    }

    public String getWorkoutName(int id){
        RouteListElement object= elements.get(id);
        return object.name;
    }

    public void updateWorkoutName(int id,
                                  String name){
        RouteListElement object= elements.get(id);
        currentDataBase.updateNameWorkout(object.id, name);
    }
}
