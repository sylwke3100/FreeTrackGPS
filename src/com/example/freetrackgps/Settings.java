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
        timeSetting.setSelection(sharePrefs.getInt("time", 1));
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