package com.example.freetrackgps;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Environment;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class WorkoutsPreviewOperations {
    private DatabaseManager currentDataBase;
    private List<RouteListElement> elements;
    private Context localContext;
    private static final SimpleDateFormat formatDate = new SimpleDateFormat("dd.MM.yyyy HH:mm ");
    private SimpleDateFormat fileGpxFormat = new SimpleDateFormat("yyyy-MM-dd-HH_mm_ss");
    WorkoutsPreviewOperations(Context context){
        currentDataBase = new DatabaseManager(context);
        localContext = context;
    }

    public ArrayList<HashMap<String,String>> getUpdatedWorkoutsList(){
        elements = currentDataBase.getRoutesList();
        ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
        for(RouteListElement element: elements){
            HashMap<String,String> simpleItem = new HashMap<String, String>();
            simpleItem.put("time", formatDate.format(element.startTime));
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
        gpx = new GPXWriter(fileNameBuffer.toString(), object.startTime);
        for (RouteElement point: pointsWorkout){
            gpx.addPoint(point);
        }
        if(gpx.save() == true)
            Toast.makeText(localContext, localContext.getString(R.string.SaveTrueInfo) + " " + fileNameBuffer.toString(), Toast.LENGTH_LONG).show();
        else
            Toast.makeText(localContext, localContext.getString(R.string.SaveFalseInfo), Toast.LENGTH_LONG).show();

    }
}