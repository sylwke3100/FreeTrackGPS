package com.sylwke3100.freetrackgps;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.LinkedList;
import java.util.List;


public class DatabaseManager extends SQLiteOpenHelper {
    private static final int databaseVersion = 4;

    public DatabaseManager(Context context) {
        super(context, DefaultValues.defaultDatabaseName, null, databaseVersion);
        SQLiteDatabase writableDatabase = this.getWritableDatabase();
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
            "CREATE TABLE workoutsList (id INTEGER PRIMARY KEY AUTOINCREMENT , timeStart INTEGER, distance NUMBER, name String)");
        db.execSQL(
            "CREATE TABLE workoutPoint (id INTEGER, timestamp INTEGER, distance NUMBER, latitude NUMBER, longitude NUMBER, altitude NUMBER)");
        db.execSQL(
            "CREATE TABLE ignorePointsList (id INTEGER PRIMARY KEY AUTOINCREMENT, latitude NUMBER, longitude NUMBER, name TEXT)");
    }

    public long startWorkout(long timeStartWorkout) {
        SQLiteDatabase writableDatabase = this.getWritableDatabase();
        ContentValues workoutValues = new ContentValues();
        workoutValues.put("timeStart", timeStartWorkout);
        workoutValues.put("distance", 0);
        long workoutId = writableDatabase.insert("workoutsList", null, workoutValues);
        writableDatabase.close();
        return workoutId;
    }

    public void addPoint(long workoutId, RouteElement point, double distance) {
        SQLiteDatabase writableDatabase = this.getWritableDatabase();
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
        writableDatabase.close();
    }

    public boolean addIgnorePoint(double latitude, double longitude, String name) {
        SQLiteDatabase readableDatabase = this.getReadableDatabase();
        Cursor currentCursor = readableDatabase.rawQuery(
            "SELECT * FROM ignorePointsList WHERE latitude=" + Double.toString(latitude)
                + " AND longitude=" + Double.toString(longitude), null);
        if (currentCursor.getCount() == 0) {
            SQLiteDatabase writableDatabase = this.getWritableDatabase();
            ContentValues ignorePointValues = new ContentValues();
            ignorePointValues.put("latitude", latitude);
            ignorePointValues.put("longitude", longitude);
            ignorePointValues.put("name", name);
            writableDatabase.insert("ignorePointsList", null, ignorePointValues);
            writableDatabase.close();
            return true;
        } else
            return false;
    }

    private String getPreparedStringFilters(List<DatabaseFilter> filters) {
        boolean isActiveAnyFilter = false;
        String databaseFilterString = new String();
        databaseFilterString = " WHERE ";
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
        SQLiteDatabase readableDatabase = this.getReadableDatabase();
        Cursor currentCursor = readableDatabase.rawQuery(
            "SELECT * FROM workoutsList" + getPreparedStringFilters(filters)
                + " ORDER BY timeStart DESC", null);
        if (currentCursor.moveToFirst()) {
            do {
                currentRoutesList.add(
                    new RouteListElement(currentCursor.getInt(0), currentCursor.getLong(1),
                        currentCursor.getDouble(2), currentCursor.getString(3)));
            } while (currentCursor.moveToNext());
        }
        readableDatabase.close();
        return currentRoutesList;
    }

    public void deleteRoute(long id) {
        SQLiteDatabase writableDatabase = this.getWritableDatabase();
        writableDatabase.delete("workoutsList", "id=" + Long.toString(id), null);
        writableDatabase.delete("workoutPoint", "id=" + Long.toString(id), null);
        writableDatabase.close();
    }

    public void deleteIgnorePoint(double lat, double lon) {
        SQLiteDatabase writableDatabase = this.getWritableDatabase();
        writableDatabase.delete("ignorePointsList", "latitude=" + Double.toString(lat), null);
        writableDatabase.delete("ignorePointsList", "longitude=" + Double.toString(lon), null);
        writableDatabase.close();
    }

    public List<IgnorePointsListElement> getIgnorePointsList() {
        List<IgnorePointsListElement> currentIgnorePointsList =
            new LinkedList<IgnorePointsListElement>();
        SQLiteDatabase readableDatabase = this.getReadableDatabase();
        Cursor currentCursor = readableDatabase.rawQuery("SELECT * FROM ignorePointsList", null);
        if (currentCursor.moveToFirst()) {
            do {
                IgnorePointsListElement element =
                    new IgnorePointsListElement(currentCursor.getDouble(1),
                        currentCursor.getDouble(2), currentCursor.getString(3));
                currentIgnorePointsList.add(element);
            } while (currentCursor.moveToNext());
        }
        readableDatabase.close();
        return currentIgnorePointsList;
    }

    public List<RouteElement> getPointsInRoute(long id) {
        List<RouteElement> currentPointsList = new LinkedList<RouteElement>();
        SQLiteDatabase readableDatabase = this.getReadableDatabase();
        Cursor currentCursor = readableDatabase
            .rawQuery("SELECT * FROM workoutPoint WHERE id=" + Long.toString(id), null);
        if (currentCursor.moveToFirst()) {
            do {
                currentPointsList.add(
                    new RouteElement(currentCursor.getDouble(3), currentCursor.getDouble(4),
                        currentCursor.getDouble(5), currentCursor.getLong(1)));
            } while (currentCursor.moveToNext());
        }
        readableDatabase.close();
        return currentPointsList;
    }

    public void updateNameWorkout(long idWorkout, String name) {
        SQLiteDatabase writableDatabase = this.getWritableDatabase();
        ContentValues workoutValues = new ContentValues();
        workoutValues.put("name", name);
        writableDatabase
            .update("workoutsList", workoutValues, "id=" + Long.toString(idWorkout), null);
        writableDatabase.close();
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
            }
        else
            onCreate(db);
    }
}
