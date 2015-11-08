package com.sylwke3100.freetrackgps;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

public class SettingsActivity extends Activity {

    private Spinner timeSetting, distanceSetting;
    private ToggleButton viewWorkoutStatusSetting, showNotificationSetting,
        vibrateNotificationSetting, exitAlertSetting;
    private SharedPreferences sharePrefs;

    @Override public void onCreate(Bundle savedInstanceState) {
        sharePrefs = getSharedPreferences("Pref", Activity.MODE_PRIVATE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        timeSetting = (Spinner) this.findViewById(R.id.spinner);
        distanceSetting = (Spinner) this.findViewById(R.id.spinner2);
        viewWorkoutStatusSetting = (ToggleButton) this.findViewById(R.id.toggleButton);
        showNotificationSetting = (ToggleButton) this.findViewById(R.id.toggleButton2);
        vibrateNotificationSetting = (ToggleButton) this.findViewById(R.id.vibraTogglebutton);
        exitAlertSetting = (ToggleButton) this.findViewById(R.id.toggleButton3);
        timeSetting.setSelection(sharePrefs.getInt("time", DefaultValues.defaultMinSpeedIndex));
        distanceSetting
            .setSelection(sharePrefs.getInt("distance", DefaultValues.defaultMinDistanceIndex));
        viewWorkoutStatusSetting.setChecked(sharePrefs.getBoolean("showWorkoutInfo", false));
        showNotificationSetting.setChecked(sharePrefs.getBoolean("showNotificationWorkout", true));
        vibrateNotificationSetting.setChecked(sharePrefs.getBoolean("vibrateNotification", false));
        exitAlertSetting.setChecked(sharePrefs.getBoolean("exitAlert", true));
        timeSetting.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor preferencesEditor = sharePrefs.edit();
                preferencesEditor.putInt("time", position);
                preferencesEditor.commit();
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {
                timeSetting
                    .setSelection(sharePrefs.getInt("time", DefaultValues.defaultMinSpeedIndex));
            }
        });
        distanceSetting.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences.Editor preferencesEditor = sharePrefs.edit();
                preferencesEditor.putInt("distance", position);
                preferencesEditor.commit();
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {
                distanceSetting.setSelection(
                    sharePrefs.getInt("distance", DefaultValues.defaultMinDistanceIndex));
            }
        });
        viewWorkoutStatusSetting
            .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        SharedPreferences.Editor preferencesEditor = sharePrefs.edit();
                        preferencesEditor.putBoolean("showWorkoutInfo", isChecked);
                        preferencesEditor.commit();
                    }
                });
        showNotificationSetting
            .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    SharedPreferences.Editor preferencesEditor = sharePrefs.edit();
                    preferencesEditor.putBoolean("showNotificationWorkout", isChecked);
                    preferencesEditor.commit();
                }
            });
        vibrateNotificationSetting
            .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    SharedPreferences.Editor preferencesEditor = sharePrefs.edit();
                    preferencesEditor.putBoolean("vibrateNotification", isChecked);
                    preferencesEditor.commit();
                }
            });
        exitAlertSetting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                SharedPreferences.Editor preferencesEditor = sharePrefs.edit();
                preferencesEditor.putBoolean("exitAlert", isChecked);
                preferencesEditor.commit();
            }
        });
    }

    @Override protected void onDestroy() {
        Toast.makeText(getBaseContext(), getBaseContext().getString(R.string.closeSettingsInfo),
            Toast.LENGTH_LONG).show();
        super.onDestroy();
    }
}
