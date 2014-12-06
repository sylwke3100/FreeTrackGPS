package com.example.freetrackgps;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;
import java.util.List;

import android.content.SharedPreferences;

public class Settings extends Activity {

    private Spinner timeSetting, distanceSetting;
    private ToggleButton viewWorkouStatusSetting;
    private SharedPreferences sharePrefs;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        sharePrefs = getSharedPreferences("Pref", Activity.MODE_PRIVATE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        timeSetting =(Spinner) this.findViewById(R.id.spinner);
        distanceSetting = (Spinner) this.findViewById(R.id.spinner2);
        viewWorkouStatusSetting = (ToggleButton) this.findViewById(R.id.toggleButton);
        List<String> speedTypes = new ArrayList<String>();
        speedTypes.add("Fast");
        speedTypes.add("Normal");
        speedTypes.add("Slow");
        ArrayAdapter<String> timeAdapter = new ArrayAdapter<String> (this, android.R.layout.simple_spinner_dropdown_item, speedTypes);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSetting.setAdapter(timeAdapter);
        timeSetting.setSelection(sharePrefs.getInt("time", 1));
        List<String> distanceTypes = new ArrayList<String>();
        distanceTypes.add("Near");
        distanceTypes.add("Normal");
        distanceTypes.add("Far");
        ArrayAdapter<String> distanceAdapter = new ArrayAdapter<String> (this, android.R.layout.simple_spinner_dropdown_item, distanceTypes);
        distanceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        distanceSetting.setAdapter(distanceAdapter);
        distanceSetting.setSelection(sharePrefs.getInt("distance", 1));
        viewWorkouStatusSetting.setChecked(sharePrefs.getBoolean("showWorkoutInfo", false));
        timeSetting.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor preferencesEditor = sharePrefs.edit();
                preferencesEditor.putInt("time", position);
                preferencesEditor.commit();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                timeSetting.setSelection(sharePrefs.getInt("time", 1));
            }
        });
        distanceSetting.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor preferencesEditor = sharePrefs.edit();
                preferencesEditor.putInt("distance", position);
                preferencesEditor.commit();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                distanceSetting.setSelection(sharePrefs.getInt("distance", 1));
            }
        });
        viewWorkouStatusSetting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    SharedPreferences.Editor preferencesEditor = sharePrefs.edit();
                    preferencesEditor.putBoolean("showWorkoutInfo",isChecked);
                    preferencesEditor.commit();
            }
        });
    }

    @Override
    protected void onDestroy() {
        Toast.makeText(getBaseContext(), "Restart application for apply changes",Toast.LENGTH_LONG).show();
        super.onDestroy();
    }
}