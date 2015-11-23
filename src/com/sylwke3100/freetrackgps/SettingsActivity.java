package com.sylwke3100.freetrackgps;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class SettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle localBundle = getIntent().getExtras();
        try {
            if (localBundle.getBoolean("gpsWorkoutSettings", false))
                getFragmentManager().beginTransaction()
                        .replace(android.R.id.content, new GPSPreferencesFragment()).commit();
            else
                getFragmentManager().beginTransaction()
                        .replace(android.R.id.content, new PreferencesFragment()).commit();
        }
        catch (Exception e){
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content, new PreferencesFragment()).commit();
        }
    }

    @Override
    protected void onDestroy() {
        Toast.makeText(getBaseContext(), getBaseContext().getString(R.string.closeSettingsInfo),
                Toast.LENGTH_LONG).show();
        super.onDestroy();
    }
}
