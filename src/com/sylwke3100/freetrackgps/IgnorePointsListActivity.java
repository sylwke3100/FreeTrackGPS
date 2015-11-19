package com.sylwke3100.freetrackgps;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IgnorePointsListActivity extends Activity {
    List<IgnorePointsListElement> localListIgnore;
    private ListView ignorePonitsList;
    private SimpleAdapter simpleAdapter;
    private DatabaseManager localInstanceDatabase;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ignorepoints_list);
        ignorePonitsList = (ListView) findViewById(R.id.listIgnorePointsView);
        registerForContextMenu(ignorePonitsList);
        localInstanceDatabase = new DatabaseManager(getBaseContext());
        onUpdateIgnoreList();
    }

    public void onUpdateIgnoreList() {
        ArrayList<HashMap<String, String>> baseList = new ArrayList<HashMap<String, String>>();
        localListIgnore = localInstanceDatabase.getIgnorePointsList();
        for (IgnorePointsListElement element : localListIgnore) {
            baseList.add(element.getPreparedHashMapToView());
        }
        simpleAdapter = new SimpleAdapter(this, baseList, R.layout.textview_ignore_points,
            new String[] {"name", "points"}, new int[] {R.id.NameTextView, R.id.PointsTextView});
        ignorePonitsList.setAdapter(simpleAdapter);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_ignorepoints, menu);
        return true;
    }

    public void onCreateContextMenu(ContextMenu menu, View v,
        ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_ignorepoints, menu);
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info =
            (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.action_ignoreponts_list_delete:
                onDeleteIgnorePoints(info.position);
                onUpdateIgnoreList();
                break;
        }
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_ignorepoints_add_manually:
                onAddIgnorePointsAlertDialogShow();
                break;
            case R.id.action_ignorepoints_add_from_map:
                startActivity(new Intent(this, IgnorePointsAddFromMapActivity.class));
                break;
        }
        return true;
    }

    public void onResume(){
        super.onResume();
        onUpdateIgnoreList();
    }

    public void onEmptyErrorAlertShow() {
        Toast.makeText(getBaseContext(), getBaseContext().getString(R.string.errorValueInfo),
            Toast.LENGTH_LONG).show();
    }

    public void onDeleteIgnorePoints(long position) {
        IgnorePointsListElement element = localListIgnore.get((int) position);
        this.localInstanceDatabase.deleteIgnorePoint(element.latitude, element.longitude);
    }

    public void onAddIgnorePointsAlertDialogShow() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.prompt_ignorepoints_add, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView);
        final EditText inputName = (EditText) promptView.findViewById(R.id.nameEdit);
        final EditText inputLat = (EditText) promptView.findViewById(R.id.latEdit);
        final EditText inputLon = (EditText) promptView.findViewById(R.id.lonEdit);
        alertDialogBuilder.setCancelable(false)
            .setPositiveButton(R.string.okLabel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    addIgnorePointsFromAlertDialog(inputLat, inputLon, inputName);
                }
            }).setNegativeButton(R.string.cancelLabel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();
    }

    public void addIgnorePointsFromAlertDialog(EditText inputLat, EditText inputLon,
        EditText inputName) {
        if (!inputLat.getText().toString().isEmpty() && !inputLon.getText().toString().isEmpty()) {
            Double latitude = Double.parseDouble(inputLat.getText().toString());
            Double longitude = Double.parseDouble(inputLon.getText().toString());
            String name = inputName.getText().toString();
            if (latitude != 0 && longitude != 0 && !name.isEmpty())
                if (localInstanceDatabase.addIgnorePoint(latitude, longitude, name) == false)
                    Toast.makeText(getBaseContext(), R.string.ignorePointsExists, Toast.LENGTH_LONG)
                        .show();
            onUpdateIgnoreList();
        } else
            onEmptyErrorAlertShow();
    }
}
