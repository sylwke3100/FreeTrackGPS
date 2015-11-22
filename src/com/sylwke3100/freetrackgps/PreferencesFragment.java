package com.sylwke3100.freetrackgps;

import android.os.Bundle;
import android.preference.PreferenceFragment;


public class PreferencesFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.activity_preferences);
        }

}
