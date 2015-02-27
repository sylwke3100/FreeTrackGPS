package com.sylwke3100.freetrackgps;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class DatabaseTimeFilter implements DatabaseFilter {
    private long startTimeFilter, endTimeFilter;
    private SharedPreferences sharePrefs;

    public DatabaseTimeFilter(Context context){
        sharePrefs = context.getSharedPreferences("Pref", Activity.MODE_PRIVATE);
        startTimeFilter = sharePrefs.getLong("filterOneTime", -1);
        endTimeFilter = 0;
    }
    public void setViewFilter(long startTime){
        startTimeFilter = startTime;
        SharedPreferences.Editor preferencesEditor = sharePrefs.edit();
        preferencesEditor.putLong("filterOneTime", startTime);
        preferencesEditor.commit();
    }

    private void updateStatus(){
        startTimeFilter = sharePrefs.getLong("filterOneTime", -1);
    }

    public void setViewFilter(long startTime,
                              long endTime){
        startTimeFilter = startTime;
        endTimeFilter = endTime;
    }

    public void clearFilters(){
        startTimeFilter = 0;
        endTimeFilter = 0;
    }

    public String getGeneratedFilterString(){
        updateStatus();
        if (startTimeFilter > 0 && endTimeFilter == 0)
            return "timeStart>= " + Long.toString(startTimeFilter);
        else{
            if (startTimeFilter > 0 && endTimeFilter > 0)
                return "timeStart>= " + Long.toString(startTimeFilter) + "AND timeStart<= " + Long.toString(endTimeFilter);
            else
                return "";
        }
    }

    public boolean isActive(){
        updateStatus();
        if ((startTimeFilter > 0 && endTimeFilter == 0) || (startTimeFilter > 0 && endTimeFilter > 0))
            return true;
        else
            return false;
    }
}
