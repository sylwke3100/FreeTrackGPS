package com.example.freetrackgps;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class WorkoutViewer extends Activity {
    private SimpleAdapter simpleAdapter;
    private static final SimpleDateFormat formatDate = new SimpleDateFormat("HH:mm dd.MM.yyyy");
    private DatabaseManager currentDataBase;
    private ListView listWorkout;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workout_views_activity);
        listWorkout = (ListView) this.findViewById(R.id.listWorkout);
        currentDataBase = new DatabaseManager(getBaseContext());
        onUpdateWorkoutsList();
    }
    private void onUpdateWorkoutsList(){
        List<RouteListElement> elements = currentDataBase.getRoutesList();
        ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
        for(RouteListElement element: elements){
            HashMap<String,String> simpleItem = new HashMap<String, String>();
            simpleItem.put("time", formatDate.format(element.startTime));
            simpleItem.put("distance",  String.format("%.2f km", element.distance/1000));
            list.add(simpleItem);
        }
        simpleAdapter = new SimpleAdapter(this, list, R.layout.lines_layout, new String[]{"time", "distance"},new int[] {R.id.line_time, R.id.line_distance});
        listWorkout.setAdapter(simpleAdapter);
    }
}