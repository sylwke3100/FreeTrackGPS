package com.sylwke3100.freetrackgps;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.LinkedList;
import java.util.List;

public class IgnorePointsDatabaseController {
    private DatabaseManager databaseManager;

    public IgnorePointsDatabaseController(Context context) {
        databaseManager = new DatabaseManager(context);
    }

    public boolean addPoint(double latitude, double longitude, String name) {
        Cursor currentCursor = databaseManager.getCursor("ignorePointsList", "latitude=? AND longitude=?",
                new String[]{Double.toString(latitude), Double.toString(longitude)});
        if (currentCursor.getCount() == 0) {
            ContentValues ignorePointValues = new ContentValues();
            ignorePointValues.put("latitude", latitude);
            ignorePointValues.put("longitude", longitude);
            ignorePointValues.put("name", name);
            databaseManager.insertValues("ignorePointsList", ignorePointValues);
            return true;
        } else
            return false;
    }

    public List<IgnorePointsListElement> getList() {
        List<IgnorePointsListElement> currentIgnorePointsList =
                new LinkedList<IgnorePointsListElement>();
        Cursor currentCursor =
                databaseManager.getCursor("ignorePointsList", null, null);
        if (currentCursor.moveToFirst()) {
            do {
                IgnorePointsListElement element =
                        new IgnorePointsListElement(currentCursor.getDouble(1),
                                currentCursor.getDouble(2), currentCursor.getString(3));
                currentIgnorePointsList.add(element);
            } while (currentCursor.moveToNext());
        }
        return currentIgnorePointsList;
    }

    public void deletePoint(double lat, double lon) {
       databaseManager.deleteValues("ignorePointsList", " latitude =? AND longitude =?",
                new String[]{Double.toString(lat), Double.toString(lon)});
    }


}
