package com.sylwke3100.freetrackgps;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import com.sylwke3100.freetrackgps.LocalPagerAdapter;

public class WorkoutInfoActivity extends FragmentActivity {
    FragmentPagerAdapter adapterViewPager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_info_tabs);
        ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
        adapterViewPager =
            new LocalPagerAdapter(getSupportFragmentManager(), savedInstanceState.getInt("routeId"),
                savedInstanceState.getDouble("distanceInfo"), savedInstanceState.getLong("startTimeInfo"), savedInstanceState.getString("routeName"));
        vpPager.setAdapter(adapterViewPager);
    }

}
