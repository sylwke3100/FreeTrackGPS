package com.sylwke3100.freetrackgps;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class WorkoutsPreviewActivity extends Activity {
    private SimpleAdapter simpleAdapter;
    private WorkoutsPreviewOperations workoutsPreviewOperations;
    private ListView listWorkout;
    private Menu optionsMenu;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_views);
        listWorkout = (ListView) this.findViewById(R.id.listWorkout);
        registerForContextMenu(listWorkout);
        workoutsPreviewOperations = new WorkoutsPreviewOperations(getBaseContext());
        onUpdateWorkoutsList();
    }

    private void onUpdateWorkoutsList() {
        simpleAdapter = new SimpleAdapter(this, workoutsPreviewOperations.getUpdatedWorkoutsList(), R.layout.textview_row_lines, new String[]{"time", "distance"}, new int[]{R.id.line_time, R.id.line_distance});
        listWorkout.setAdapter(simpleAdapter);
    }

    public void onCreateContextMenu(ContextMenu menu,
                                    View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_workoutspreview, menu);
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
            case R.id.action_workout_change:
                onUpdateNameWorkout(info.position);
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void updateIconOptionMenu(){
        if (workoutsPreviewOperations.getStatusTimeFilter())
            optionsMenu.findItem(R.id.action_overflow).getSubMenu().findItem(R.id.action_filter_by_date).setIcon(R.drawable.tick);
        else
            optionsMenu.findItem(R.id.action_overflow).getSubMenu().findItem(R.id.action_filter_by_date).setIcon((R.drawable.emptytick));
        if (workoutsPreviewOperations.getStatusNameFilter())
            optionsMenu.findItem(R.id.action_overflow).getSubMenu().findItem(R.id.action_filter_by_name).setIcon(R.drawable.tick);
        else
            optionsMenu.findItem(R.id.action_overflow).getSubMenu().findItem(R.id.action_filter_by_name).setIcon((R.drawable.emptytick));

    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_workoutpreview, menu);
        this.optionsMenu = menu;
        updateIconOptionMenu();
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onPrepareOptionsMenu(Menu menu){
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

    public void onUpdateNameFilter(){
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.prompt_workout_name_filer, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView);
        final EditText input = (EditText) promptView.findViewById(R.id.nameFilter);
        input.setText(workoutsPreviewOperations.getFilterName());
        alertDialogBuilder
            .setCancelable(false)
            .setPositiveButton(R.string.okLabel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    workoutsPreviewOperations.setNameFilter(input.getText().toString());
                    onUpdateWorkoutsList();
                }
            })
            .setNegativeButton(R.string.cancelLabel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
        AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();
    }

    public void onUpdateNameWorkout(final int idWorkout){
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.prompt_workout_name_edit, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView);
        final EditText input = (EditText) promptView.findViewById(R.id.nameWorkout);
        input.setText(workoutsPreviewOperations.getWorkoutName(idWorkout));
        alertDialogBuilder
            .setCancelable(false)
            .setPositiveButton(R.string.okLabel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    workoutsPreviewOperations.updateWorkoutName(idWorkout, input.getText().toString());
                    onUpdateWorkoutsList();
                }
            })
            .setNegativeButton(R.string.cancelLabel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();
    }

    public void onResume(){
        super.onResume();
        onUpdateWorkoutsList();
    }
}
