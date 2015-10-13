package com.sylwke3100.freetrackgps;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public  class LocalPagerAdapter extends FragmentPagerAdapter {

    private int routeId;
    private double distanceInfo;
    private long startTimeInfo;
    private String routeName;
    static final int numberItems = 1;

    public LocalPagerAdapter(FragmentManager fragmentManager, int routeId, double distanceInfo,
        long startTimeInfo, String routeName) {
        super(fragmentManager);
        this.routeId = routeId;
        this.distanceInfo = distanceInfo;
        this.startTimeInfo = startTimeInfo;
        this.routeName = routeName;
    }

    public int getCount() {
        return numberItems;
    }

    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return WorkoutInfoFragment
                    .newInstance(routeId, distanceInfo, startTimeInfo, routeName);
            default:
                return null;
        }

    }
}
