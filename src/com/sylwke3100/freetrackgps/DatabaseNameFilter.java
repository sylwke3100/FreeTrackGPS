package com.sylwke3100.freetrackgps;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import com.sylwke3100.freetrackgps.DatabaseFilter;

public class DatabaseNameFilter implements DatabaseFilter {
    private String name = new String();
    private SharedPreferences prefs;
    public DatabaseNameFilter(Context local){
        name = new String();
        prefs = local.getSharedPreferences("Pref", Activity.MODE_PRIVATE);
        name = prefs.getString("filterName","");
    }
    public void setViewFilter(String name){
        this.name = name;
        SharedPreferences.Editor preferencesEditor = prefs.edit();
        preferencesEditor.putString("filterName", name);
        preferencesEditor.commit();
    }

    public void clearFilters(){
        this.name = "";
        setViewFilter(name);
    }

    public boolean isActive(){
        if (!name.isEmpty())
            return true;
        else
            return false;
    }

    public String getGeneratedFilterString(){
        if (isActive())
            return " name LIKE '%"+name+"%'";
        else
            return "";
    }
}
