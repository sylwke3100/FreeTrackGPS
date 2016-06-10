package com.sylwke3100.freetrackgps;

import android.app.ActionBar;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

public class WorkoutInfoActivity extends Activity {

    SharedPreferences sharedPreferences;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(DefaultValues.prefs, MODE_PRIVATE);
        ActionBar actionBar = this.getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(true);
        ActionBar.Tab detailsTab = actionBar.newTab().setText(R.string.workoutInfoDetailsLabel)
            .setTabListener(new TabListener<WorkoutInfoDetailsFragment>(this, "info",
                WorkoutInfoDetailsFragment.class, this.getIntent().getExtras()));
        actionBar.addTab(detailsTab);
        if (!sharedPreferences.getBoolean("disableMap", false)) {
            ActionBar.Tab mapTab = actionBar.newTab().setText(R.string.workoutInfoMapLabel)
                    .setTabListener(
                            new TabListener<WorkoutInfoMapFragment>(this, "map", WorkoutInfoMapFragment.class,
                                    this.getIntent().getExtras()));
            actionBar.addTab(mapTab);
        }

    }

}
