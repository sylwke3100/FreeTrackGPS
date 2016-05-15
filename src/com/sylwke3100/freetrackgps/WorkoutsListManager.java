package com.sylwke3100.freetrackgps;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class WorkoutsListManager {
    private SharedPreferences sharePrefs;
    private WorkoutsListDatabaseController workoutsListController;
    private List<RouteListElement> rawWorkoutsList;
    private Context localContext;
    private WorkoutDatabaseController workoutController;
    private DatabaseTimeFilter timeFilter;
    private DatabaseNameFilter nameFilter;

    WorkoutsListManager(Context context) {
        workoutsListController = new WorkoutsListDatabaseController(context);
        localContext = context;
        sharePrefs = context.getSharedPreferences(DefaultValues.prefs, Activity.MODE_PRIVATE);
        timeFilter = new DatabaseTimeFilter(context);
        nameFilter = new DatabaseNameFilter(context);
        workoutController = new WorkoutDatabaseController(context);
    }

    public ArrayList<HashMap<String, String>> getUpdatedWorkoutsList() {
        List<DatabaseFilter> filtersList = new LinkedList<DatabaseFilter>();
        filtersList.add(timeFilter);
        filtersList.add(nameFilter);
        rawWorkoutsList =  workoutsListController.getRoutes(filtersList);
        ArrayList<HashMap<String, String>> workoutsList = new ArrayList<HashMap<String, String>>();
        for (RouteListElement workout : rawWorkoutsList) {
            workoutsList.add(workout.getPreparedHashMapToView());
        }
        return workoutsList;
    }

    public List<RouteListElement> getUpdatedWorkoutsRawList() {
        return rawWorkoutsList;
    }

    public void deleteWorkout(int id) {
        workoutsListController.delete(rawWorkoutsList.get(id).id);
    }

    public List<RouteElement> getPointsInRoute(Integer id) {
        return workoutController.getPoints(id);
    }

    public void setTimeOneFilter(long time) {
        timeFilter.setViewFilter(time);
    }

    public void setNameFilter(String name) {
        nameFilter.setViewFilter(name);
    }

    public boolean getStatusTimeFilter() {
        if (timeFilter.isActive())
            return true;
        else
            return false;
    }

    public void clearAllFilters(){
        timeFilter.clearFilters();
        nameFilter.clearFilters();
    }

    public boolean getStatusNameFilter() {
        if (nameFilter.isActive())
            return true;
        else
            return false;
    }

    public String getFilterName() {
        return sharePrefs.getString("filterName", "");
    }

    public String getWorkoutName(int id) {
        RouteListElement object = rawWorkoutsList.get(id);
        return object.name;
    }

    public void updateWorkoutName(int id, String name) {
        RouteListElement object = rawWorkoutsList.get(id);
        workoutController.updateName(object.id, name);
    }
}
