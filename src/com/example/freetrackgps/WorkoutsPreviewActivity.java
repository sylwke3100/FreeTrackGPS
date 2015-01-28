package com.example.freetrackgps;

import android.app.Activity;
import android.content.ContextWrapper;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.HashMap;

public class WorkoutsPreviewActivity extends Activity {
    private SimpleAdapter simpleAdapter;
    private static final SimpleDateFormat formatDate = new SimpleDateFormat("dd.MM.yyyy HH:mm ");
    private DatabaseManager currentDataBase;
    private ListView listWorkout;
    private List<RouteListElement> elements;
    private SimpleDateFormat fileGpxFormat = new SimpleDateFormat("yyyy-MM-dd-HH_mm_ss");
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workout_views_activity);
        listWorkout = (ListView) this.findViewById(R.id.listWorkout);
        currentDataBase = new DatabaseManager(getBaseContext());
        registerForContextMenu(listWorkout);
        onUpdateWorkoutsList();
    }
    private void onUpdateWorkoutsList(){
        elements = currentDataBase.getRoutesList();
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

    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_view_menu, menu);
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.action_workout_delete:
                currentDataBase.deleteRoute(elements.get(info.position).id);
                this.onUpdateWorkoutsList();
                return true;
            case R.id.action_workout_export:
                onExportWorkout(elements.get(info.position));
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void onExportWorkout(RouteListElement object){
        List<RouteElement> pointsWorkout = currentDataBase.getPointsInRoute(object.id);
        GPXWriter gpx;
        StringBuffer fileNameBuffer = new StringBuffer();
        ContextWrapper c = new ContextWrapper(getBaseContext());
        File dir = new File(Environment.getExternalStorageDirectory()+"/workout/");
        if(!(dir.exists() && dir.isDirectory()))
            dir.mkdir();
        fileNameBuffer.append(Environment.getExternalStorageDirectory() + "/workout/");
        fileNameBuffer.append(fileGpxFormat.format(new Date(object.startTime)) + ".gpx");
        gpx = new GPXWriter(fileNameBuffer.toString(), object.startTime);
        for (RouteElement point: pointsWorkout){
            gpx.addPoint(point);
        }
        if(gpx.save() == true)
            Toast.makeText(getBaseContext(), getBaseContext().getString(R.string.SaveTrueInfo) + " " + fileNameBuffer.toString(), Toast.LENGTH_LONG).show();
        else
            Toast.makeText(getBaseContext(), getBaseContext().getString(R.string.SaveFalseInfo), Toast.LENGTH_LONG).show();

    }
}