package com.sylwke3100.freetrackgps;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.LinkedList;
import java.util.List;


public class DatabaseManager extends SQLiteOpenHelper {
    private static final int databaseVersion = 5;
    private SQLiteDatabase writableDatabase, readableDatabase;

    public DatabaseManager(Context context) {
        super(context, DefaultValues.defaultDatabaseName, null, databaseVersion);
        writableDatabase = this.getWritableDatabase();
        readableDatabase = this.getReadableDatabase();
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE workoutsList (id INTEGER PRIMARY KEY AUTOINCREMENT , timeStart INTEGER, distance NUMBER, name String, minHeight NUMBER DEFAULT -1, maxHeight NUMBER DEFAULT -1, timeEnd INTEGER, pointCount INTEGER, endTime)");
        db.execSQL(
                "CREATE TABLE workoutPoint (id INTEGER, timestamp INTEGER, distance NUMBER, latitude NUMBER, longitude NUMBER, altitude NUMBER)");
        db.execSQL(
                "CREATE TABLE ignorePointsList (id INTEGER PRIMARY KEY AUTOINCREMENT, latitude NUMBER, longitude NUMBER, name TEXT)");
    }

    public long startWorkout(long timeStartWorkout) {
        ContentValues workoutValues = new ContentValues();
        workoutValues.put("timeStart", timeStartWorkout);
        workoutValues.put("distance", 0);
        long workoutId = this.writableDatabase.insert("workoutsList", null, workoutValues);
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
        writableDatabase.insert("workoutPoint", null, workoutPointValues);
        workoutPointUpdatedValues.put("distance", distance);
        writableDatabase.update("workoutsList", workoutPointUpdatedValues, "id=" + workoutId, null);
    }

    public boolean addIgnorePoint(double latitude, double longitude, String name) {
        Cursor currentCursor = readableDatabase
                .query("ignorePointsList", null, "latitude=? AND longitude=?",
                        new String[]{Double.toString(latitude), Double.toString(longitude)}, null, null,
                        null);
        if (currentCursor.getCount() == 0) {
            ContentValues ignorePointValues = new ContentValues();
            ignorePointValues.put("latitude", latitude);
            ignorePointValues.put("longitude", longitude);
            ignorePointValues.put("name", name);
            writableDatabase.insert("ignorePointsList", null, ignorePointValues);
            return true;
        } else
            return false;
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

    public List<RouteListElement> getRoutesList(List<DatabaseFilter> filters) {
        List<RouteListElement> currentRoutesList = new LinkedList<RouteListElement>();
        Cursor currentCursor = readableDatabase
                .query("workoutsList", null, getPreparedStringFilters(filters), null, null, null,
                        "timeStart DESC");
        if (currentCursor.moveToFirst()) {
            do {
                currentRoutesList.add(
                        new RouteListElement(currentCursor.getInt(0), currentCursor.getLong(1),
                                currentCursor.getDouble(2), currentCursor.getString(3)));
            } while (currentCursor.moveToNext());
        }
        return currentRoutesList;
    }

    public void deleteRoute(long id) {
        writableDatabase.delete("workoutsList", "id=" + Long.toString(id), null);
        writableDatabase.delete("workoutPoint", "id=" + Long.toString(id), null);
    }

    public void deleteIgnorePoint(double lat, double lon) {
        writableDatabase.delete("ignorePointsList", " latitude =? AND longitude =?",
                new String[]{Double.toString(lat), Double.toString(lon)});
    }

    public List<IgnorePointsListElement> getIgnorePointsList() {
        List<IgnorePointsListElement> currentIgnorePointsList =
                new LinkedList<IgnorePointsListElement>();
        Cursor currentCursor =
                readableDatabase.query("ignorePointsList", null, null, null, null, null, null, null);
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

    public List<RouteElement> getPointsInRoute(long id) {
        List<RouteElement> currentPointsList = new LinkedList<RouteElement>();
        Cursor currentCursor = readableDatabase
                .query("workoutPoint", null, "id=" + Long.toString(id), null, null, null, null, null);
        if (currentCursor.moveToFirst()) {
            do {
                currentPointsList.add(
                        new RouteElement(currentCursor.getDouble(3), currentCursor.getDouble(4),
                                currentCursor.getDouble(5), currentCursor.getLong(1)));
            } while (currentCursor.moveToNext());
        }
        return currentPointsList;
    }

    public RouteListElement getRouteInfo(long id) {
        Cursor currentCursor = readableDatabase.query("workoutsList", null, "id=" + Long.toString(id), null, null, null, null, null);
        currentCursor.moveToFirst();
        return new RouteListElement(currentCursor.getInt(0), currentCursor.getLong(1), currentCursor.getDouble(2), currentCursor.getString(3), currentCursor.getDouble(4), currentCursor.getDouble(5), currentCursor.getLong(6), currentCursor.getInt(7));
    }

    public void updateRouteProperties(long id, double minHeight, double maxHeight, long endTime, int pointCount){
        ContentValues workoutProperties = new ContentValues();
        workoutProperties.put("minHeight", minHeight);
        workoutProperties.put("maxHeight", maxHeight);
        workoutProperties.put("timeEnd", endTime);
        workoutProperties.put("pointCount", pointCount);
        writableDatabase.update("workoutsList", workoutProperties, "id=" + id, null);
    }

    public void updateNameWorkout(long idWorkout, String name) {
        ContentValues workoutValues = new ContentValues();
        workoutValues.put("name", name);
        writableDatabase
                .update("workoutsList", workoutValues, "id=" + Long.toString(idWorkout), null);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion)
            while (newVersion > oldVersion) {
                oldVersion++;
                if (oldVersion == 2)
                    db.execSQL("ALTER TABLE workoutsList ADD COLUMN name TEXT");
                if (oldVersion == 3)
                    db.execSQL(
                            "CREATE TABLE ignorePointsList (id INTEGER PRIMARY KEY AUTOINCREMENT, latitude NUMBER, longitude NUMBER)");
                if (oldVersion == 4)
                    db.execSQL("ALTER TABLE ignorePointsList ADD COLUMN name TEXT");
                if (oldVersion == 5) {
                    db.execSQL("ALTER TABLE workoutsList ADD COLUMN minHeight NUMBER DEFAULT -1");
                    db.execSQL("ALTER TABLE workoutsList ADD COLUMN maxHeight NUMBER DEFAULT -1");
                    db.execSQL("ALTER TABLE workoutsList ADD COLUMN timeEnd INTEGER");
                    db.execSQL("ALTER TABLE workoutsList ADD COLUMN pointCount INTEGER");
                }
            }
        else
            onCreate(db);
    }
}
