package com.sylwke3100.freetrackgps;


import android.content.Context;
import android.database.Cursor;

import java.util.LinkedList;
import java.util.List;

public class WorkoutsListDatabaseController {
    private DatabaseManager databaseManager;

    public WorkoutsListDatabaseController(Context context){
        databaseManager = new DatabaseManager(context);
    }

    private String getPreparedStringFilters(List<DatabaseFilter> filters) {
        boolean isActiveAnyFilter = false;
        String databaseFilterString = new String();
        for (DatabaseFilter filter : filters) {
            if (filter.isActive()) {
                isActiveAnyFilter = true;
                databaseFilterString += filter.getGeneratedFilterString() + " AND ";
            }
        }
        if (!(filters.size() == 0) && isActiveAnyFilter == true)
            databaseFilterString =
                    databaseFilterString.substring(0, databaseFilterString.length() - 5);
        else
            databaseFilterString = "";
        return databaseFilterString;
    }

    public List<RouteListElement> getRoutes(List<DatabaseFilter> filters) {
        List<RouteListElement> currentRoutesList = new LinkedList<RouteListElement>();
        Cursor currentCursor = databaseManager.getCursor(
                "workoutsList", getPreparedStringFilters(filters), null, "timeStart DESC");
        if (currentCursor.moveToFirst()) {
            do {
                currentRoutesList.add(
                        new RouteListElement(currentCursor.getInt(0), currentCursor.getLong(1),
                                currentCursor.getDouble(2), currentCursor.getString(3)));
            } while (currentCursor.moveToNext());
        }
        return currentRoutesList;
    }

    public void delete(long id) {
        databaseManager.deleteValues("workoutsList", "id=" + Long.toString(id), null);
        databaseManager.deleteValues("workoutPoint", "id=" + Long.toString(id), null);
    }
}
