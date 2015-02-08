package com.example.freetrackgps;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;


public class WorkoutsPreviewActivity extends Activity {
    private SimpleAdapter simpleAdapter;
    private WorkoutsPreviewOperations workoutsPreviewOperations;
    private ListView listWorkout;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workout_views_activity);
        listWorkout = (ListView) this.findViewById(R.id.listWorkout);
        registerForContextMenu(listWorkout);
        workoutsPreviewOperations = new WorkoutsPreviewOperations(getBaseContext());
        onUpdateWorkoutsList();
    }

    private void onUpdateWorkoutsList() {
        simpleAdapter = new SimpleAdapter(this, workoutsPreviewOperations.getUpdatedWorkoutsList(), R.layout.lines_layout, new String[]{"time", "distance"}, new int[]{R.id.line_time, R.id.line_distance});
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
                workoutsPreviewOperations.deleteWorkout(info.position);
                this.onUpdateWorkoutsList();
                return true;
            case R.id.action_workout_export:
                workoutsPreviewOperations.exportWorkout(info.position);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.normal_menu_workouts_view, menu);
        return  true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter_by_date:
                Intent intent = new Intent(this, DateFilterActivity.class);
                startActivity(intent);
                break;
        } return true;
    }

    public void onResume(){
        super.onResume();
        onUpdateWorkoutsList();
    }
}
