package com.sylwke3100.freetrackgps;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WorkoutsListActivity extends Activity {
    private SimpleAdapter simpleAdapter;
    private WorkoutsListOperations workoutsListOperations;
    private ListView workoutList;
    private Menu optionsMenu;
    private ArrayList<HashMap<String, String>> routesList;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workouts_list);
        routesList = new ArrayList<HashMap<String, String>>();
        workoutList = (ListView) this.findViewById(R.id.listWorkout);
        registerForContextMenu(workoutList);
        workoutsListOperations = new WorkoutsListOperations(getBaseContext());
        onUpdateWorkoutsList();

        workoutList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            List<RouteListElement> objects = workoutsListOperations.getUpdatedWorkoutsRawList();

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(WorkoutsListActivity.this, WorkoutInfoActivity.class);
                intent.putExtra("distanceInfo", objects.get(i).distance);
                intent.putExtra("startTimeInfo", objects.get(i).startTime);
                intent.putExtra("routeId", objects.get(i).id);
                intent.putExtra("routeName", objects.get(i).name);
                startActivity(intent);
            }
        });
    }

    private void onUpdateWorkoutsList() {
        routesList = workoutsListOperations.getUpdatedWorkoutsList();
        simpleAdapter = new SimpleAdapter(this, routesList, R.layout.textview_row_lines,
            new String[] {"time", "distance"}, new int[] {R.id.line_time, R.id.line_distance});
        workoutList.setAdapter(simpleAdapter);
    }

    public void onCreateContextMenu(ContextMenu menu, View v,
        ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_workoutspreview, menu);
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info =
            (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.action_workout_delete:
                onDeleteWorkoutAlert(info.position);
                return true;
            case R.id.action_workout_export:
                workoutsListOperations.exportWorkout(info.position);
                return true;
            case R.id.action_workout_change:
                onUpdateNameWorkout(info.position);
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void onDeleteWorkoutAlert(final int id){
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle(getString(R.string.deleteWorkoutTitleAlert))
            .setMessage(getString(R.string.deleteWorkoutTextAlert))
            .setPositiveButton(this.getString(R.string.yesLabel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        workoutsListOperations.deleteWorkout(id);
                        onUpdateWorkoutsList();
                    }
                }).setNegativeButton(this.getString(R.string.noLabel), null).show();

    }

    public void updateIconOptionMenu() {
        Integer dateFilterIcon, nameFilterIcon;
        if (workoutsListOperations.getStatusTimeFilter())
            dateFilterIcon = R.drawable.tick;
        else
            dateFilterIcon = R.drawable.emptytick;
        if (workoutsListOperations.getStatusNameFilter())
           nameFilterIcon = R.drawable.tick;
        else
            nameFilterIcon = R.drawable.emptytick;
        optionsMenu.findItem(R.id.action_overflow).getSubMenu().findItem(R.id.action_filter_by_date).setIcon(dateFilterIcon);
        optionsMenu.findItem(R.id.action_overflow).getSubMenu().findItem(R.id.action_filter_by_name).setIcon(nameFilterIcon);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_workoutpreview, menu);
        this.optionsMenu = menu;
        updateIconOptionMenu();
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        updateIconOptionMenu();
        return super.onPrepareOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        updateIconOptionMenu();
        switch (item.getItemId()) {
            case R.id.action_filter_by_date:
                Intent intent = new Intent(this, DateFilterActivity.class);
                startActivity(intent);
                break;
            case R.id.action_filter_by_name:
                onUpdateNameFilter();
                break;
        }
        return true;
    }

    public void onUpdateNameFilter() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.prompt_workout_name_filer, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView);
        final EditText input = (EditText) promptView.findViewById(R.id.nameFilter);
        input.setText(workoutsListOperations.getFilterName());
        alertDialogBuilder.setCancelable(false)
            .setPositiveButton(R.string.okLabel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    workoutsListOperations.setNameFilter(input.getText().toString());
                    onUpdateWorkoutsList();
                }
            }).setNegativeButton(R.string.cancelLabel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();
    }

    public void onUpdateNameWorkout(final int idWorkout) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.prompt_workout_name_edit, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView);
        final EditText input = (EditText) promptView.findViewById(R.id.nameWorkout);
        input.setText(workoutsListOperations.getWorkoutName(idWorkout));
        alertDialogBuilder.setCancelable(false)
            .setPositiveButton(R.string.okLabel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    workoutsListOperations.updateWorkoutName(idWorkout, input.getText().toString());
                    onUpdateWorkoutsList();
                }
            }).setNegativeButton(R.string.cancelLabel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();
    }

    public void onResume() {
        super.onResume();
        onUpdateWorkoutsList();
    }
}
