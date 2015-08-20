package com.sylwke3100.freetrackgps;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.sylwke3100.freetrackgps.DatabaseManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IgnorePointsActivity extends Activity {
    private  ListView ignorePonitsList;
    private DatabaseManager localInstanceDatabase;
    List<HashMap<String, Double>> localListIgnore;
    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ignorepoints);
        ignorePonitsList = (ListView) findViewById(R.id.listIgnorePointsView);
        registerForContextMenu(ignorePonitsList);
        localInstanceDatabase = new DatabaseManager(getBaseContext());
        onUpdateIgnoreList();
    }
    public void onUpdateIgnoreList(){
        ArrayAdapter<String> adapterList = new ArrayAdapter<String>(this, R.layout.textview_ignore_points, R.id.LineTextView);
        localListIgnore = localInstanceDatabase.getIgnorePointsList();
        for(HashMap<String, Double> element: localListIgnore){
            adapterList.add(Double.toString(element.get("lat")) + "-" + Double.toString(element.get("lon")));
        }
        ignorePonitsList.setAdapter(adapterList);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_ignorepoints, menu);
        return true;
    }

    public void onCreateContextMenu(ContextMenu menu,
        View v,
        ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_ignorepoints, menu);
    }

    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()){
            case R.id.action_ignoreponts_delete:
                onDeleteIgnorePoints(info.position);
                onUpdateIgnoreList();
                break;
        }
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
    public void onDeleteIgnorePoints(long position){
        HashMap<String, Double> element = localListIgnore.get((int) position);
        this.localInstanceDatabase.deleteIgnorePoint(element.get("lat"), element.get("lon"));
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
                            if(localInstanceDatabase.addIgnorePoint(varA, varB) == false)
                                Toast.makeText(getBaseContext(),R.string.ignorePointsExists, Toast.LENGTH_LONG).show();
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
