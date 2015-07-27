package com.sylwke3100.freetrackgps;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.sylwke3100.freetrackgps.DatabaseManager;

import java.util.HashMap;

public class IgnorePointsActivity extends Activity {
    private  ListView ignorePonitsList;
    private DatabaseManager localInstanceDatabase;
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ignorepoints);
        ignorePonitsList = (ListView) findViewById(R.id.listIgnorePointsView);
        localInstanceDatabase = new DatabaseManager(getBaseContext());
        onUpdateIgnoreList();
    }
    public void onUpdateIgnoreList(){
        ArrayAdapter<String> adapterList = new ArrayAdapter<String>(this, R.layout.textview_ignore_points, R.id.LineTextView);
        for(HashMap<String, Double> element: localInstanceDatabase.getIgnorePointsList()){
            adapterList.add(Double.toString(element.get("lat")) + "-" + Double.toString(element.get("lon")));
        }
        ignorePonitsList.setAdapter(adapterList);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_ignorepoints, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_ignorepoints_add:
                onAddIgnorePoints();
                break;
        }
        return true;
    }

    public void onShowAlert(){
        Toast.makeText(getBaseContext(), getBaseContext().getString(R.string.errorValueInfo),Toast.LENGTH_LONG).show();
    }

    public void onAddIgnorePoints(){
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.prompt_ignorepoints_add, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView);
        final EditText inputLat = (EditText) promptView.findViewById(R.id.latEdit);
        final EditText inputLon = (EditText) promptView.findViewById(R.id.lonEdit);
        alertDialogBuilder
            .setCancelable(false)
            .setPositiveButton(R.string.okLabel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (!inputLat.getText().toString().isEmpty()&& !inputLon.getText().toString().isEmpty()) {
                        Double varA = Double.parseDouble(inputLat.getText().toString());
                        Double varB = Double.parseDouble(inputLon.getText().toString());
                        if (varA != 0 && varB != 0)
                            localInstanceDatabase.addIgnorePoint(varA, varB);
                            onUpdateIgnoreList();
                    }else
                        onShowAlert();
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
    }
