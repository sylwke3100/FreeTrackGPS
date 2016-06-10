package com.sylwke3100.freetrackgps;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.LinkedList;
import java.util.List;

public class WorkoutDatabaseController {
    private DatabaseManager databaseManager;

    public WorkoutDatabaseController(Context context) {
        databaseManager = new DatabaseManager(context);
    }

    public long start(long timeStartWorkout) {
        ContentValues workoutValues = new ContentValues();
        workoutValues.put("timeStart", timeStartWorkout);
        workoutValues.put("distance", 0);
        long workoutId = databaseManager.insertValues("workoutsList", workoutValues);
        return workoutId;
    }

    public void addPoint(long workoutId, RouteElement point, double distance) {
        ContentValues workoutPointValues = new ContentValues();
        ContentValues workoutPointUpdatedValues = new ContentValues();
        workoutPointValues.put("id", workoutId);
        workoutPointValues.put("timestamp", point.time);
        workoutPointValues.put("distance", distance);
        workoutPointValues.put("latitude", point.latitude);
        workoutPointValues.put("longitude", point.longitude);
        workoutPointValues.put("altitude", point.altitude);
        databaseManager.insertValues("workoutPoint", workoutPointValues);
        workoutPointUpdatedValues.put("distance", distance);
        databaseManager.updateValues("workoutsList", workoutPointUpdatedValues, "id=" + workoutId);
    }

    public RouteListElement getInfo(long id) {
        Cursor currentCursor = databaseManager.getCursor("workoutsList", "id=" + Long.toString(id), null);
        currentCursor.moveToFirst();
        return new RouteListElement(currentCursor.getInt(0), currentCursor.getLong(1), currentCursor.getDouble(2), currentCursor.getString(3), currentCursor.getDouble(4), currentCursor.getDouble(5), currentCursor.getLong(6), currentCursor.getInt(7));
    }

    public void updateProperties(long id, double minHeight, double maxHeight, long endTime, int pointCount) {
        ContentValues workoutProperties = new ContentValues();
        workoutProperties.put("minHeight", minHeight);
        workoutProperties.put("maxHeight", maxHeight);
        workoutProperties.put("timeEnd", endTime);
        workoutProperties.put("pointCount", pointCount);
        databaseManager.updateValues("workoutsList", workoutProperties, "id=" + id);
    }

    public void updateName(long idWorkout, String name) {
        ContentValues workoutValues = new ContentValues();
        workoutValues.put("name", name);
        databaseManager.updateValues("workoutsList", workoutValues, "id=" + Long.toString(idWorkout));
    }

    public List<RouteElement> getPoints(long id) {
        List<RouteElement> currentPointsList = new LinkedList<RouteElement>();
        Cursor currentCursor =
                databaseManager.getCursor("workoutPoint", "id=" + Long.toString(id), null);
        if (currentCursor.moveToFirst()) {
            do {
                currentPointsList.add(
                        new RouteElement(currentCursor.getDouble(3), currentCursor.getDouble(4),
                                currentCursor.getDouble(5), currentCursor.getLong(1)));
            } while (currentCursor.moveToNext());
        }
        return currentPointsList;
    }

}
