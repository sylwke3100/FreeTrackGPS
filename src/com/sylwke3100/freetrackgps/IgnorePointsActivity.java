package com.sylwke3100.freetrackgps;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IgnorePointsActivity extends Activity {
    private ListView ignorePonitsList;
    private SimpleAdapter simpleAdapter;
    private DatabaseManager localInstanceDatabase;
    private LocationSharing currentLocationSharing;
    List<IgnorePointsListElement> localListIgnore;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ignorepoints);
        ignorePonitsList = (ListView) findViewById(R.id.listIgnorePointsView);
        registerForContextMenu(ignorePonitsList);
        localInstanceDatabase = new DatabaseManager(getBaseContext());
        currentLocationSharing = new LocationSharing(getBaseContext());
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
            case R.id.action_ignoreponts_delete:
                onDeleteIgnorePoints(info.position);
                onUpdateIgnoreList();
                break;
        }
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        LocationSharing.LocationSharingResult result = currentLocationSharing.getCurrentLocation();
        if (result.status == 1)
            menu.findItem(R.id.action_ignorepoints_add_from_location).setEnabled(true);
        else
            menu.findItem(R.id.action_ignorepoints_add_from_location).setEnabled(false);
        return super.onPrepareOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_ignorepoints_add:
                onAddIgnorePointsAlertDialogShow();
                break;
            case R.id.action_ignorepoints_add_from_location:
                onSetIgnorePointsNameAlert();
                break;
        }
        return true;
    }

    public void onAddIgnorePointsFromLocation(String ignorePointName) {
        LocationSharing.LocationSharingResult result = currentLocationSharing.getCurrentLocation();
        if (result.status == 1) {
            if (!localInstanceDatabase
                .addIgnorePoint(result.latitude, result.longitude, ignorePointName))
                Toast.makeText(getBaseContext(), R.string.ignorePointsExists, Toast.LENGTH_LONG)
                    .show();
            onUpdateIgnoreList();
        }
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

    public void onSetIgnorePointsNameAlert() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.prompt_ignore_points_from_location, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView);
        final EditText inputName = (EditText) promptView.findViewById(R.id.nameEdit);
        alertDialogBuilder.setCancelable(false)
            .setPositiveButton(R.string.okLabel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    onAddIgnorePointsFromLocation(inputName.getText().toString());
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
            Double varA = Double.parseDouble(inputLat.getText().toString());
            Double varB = Double.parseDouble(inputLon.getText().toString());
            String name = inputName.getText().toString();
            if (varA != 0 && varB != 0 && !name.isEmpty())
                if (localInstanceDatabase.addIgnorePoint(varA, varB, name) == false)
                    Toast.makeText(getBaseContext(), R.string.ignorePointsExists, Toast.LENGTH_LONG)
                        .show();
            onUpdateIgnoreList();
        } else
            onEmptyErrorAlertShow();
    }
}
