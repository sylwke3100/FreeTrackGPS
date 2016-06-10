package com.sylwke3100.freetrackgps;

import android.os.Bundle;
import android.preference.PreferenceFragment;


public class GPSPreferencesFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.activity_gps_preferences);
        }

}
