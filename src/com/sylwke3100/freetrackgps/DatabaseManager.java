package com.sylwke3100.freetrackgps;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DatabaseManager extends SQLiteOpenHelper{
    private static final int databaseVersion = 3;

    public DatabaseManager(Context context){
        super(context, DefaultValues.defaultDatabaseName, null, databaseVersion);
    }

    public void onCreate(SQLiteDatabase db){
        String query = "CREATE TABLE workoutsList (id INTEGER PRIMARY KEY AUTOINCREMENT , timeStart INTEGER, distance NUMBER, name String)";
        db.execSQL(query);
        query = "CREATE TABLE workoutPoint (id INTEGER, timestamp INTEGER, distance NUMBER, latitude NUMBER, longitude NUMBER, altitude NUMBER)";
        db.execSQL(query);
        db.execSQL(
            "CREATE TABLE ignorePointsList (id INTEGER PRIMARY KEY AUTOINCREMENT, latitude NUMBER, longitude NUMBER)");
    }

    public long startWorkout(long timeStartWorkout){
        SQLiteDatabase writableDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("timeStart", timeStartWorkout);
        values.put("distance", 0);
        long id = writableDatabase.insert("workoutsList",null, values );
        writableDatabase.close();
        return id;
    }

    public void addPoint(long workoutId,
                         RouteElement point,
                         double distance){
        SQLiteDatabase writableDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        ContentValues updatedValues = new ContentValues();
        values.put("id", workoutId);
        values.put("timestamp", point.time);
        values.put("distance", distance);
        values.put("latitude", point.latitude);
        values.put("longitude", point.longitude);
        values.put("altitude", point.altitude);
        writableDatabase.insert("workoutPoint", null, values);
        updatedValues.put("distance", distance);
        writableDatabase.update("workoutsList", updatedValues, "id=" + workoutId, null);
        writableDatabase.close();
    }

    public void addIgnorePoint(double latitude, double longitude){
        SQLiteDatabase writableDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        ContentValues updatedValues = new ContentValues();
        values.put("latitude", latitude);
        values.put("longitude", longitude);
        writableDatabase.insert("ignorePointsList", null, values);
        writableDatabase.close();
    }

    private String getPreparedStringFilters(List<DatabaseFilter> filters){
        boolean isActiveAnyFilter = false;
        String databaseFilterString = new String();
        databaseFilterString = " WHERE ";
        for (DatabaseFilter filter:filters){
            if (filter.isActive()) {
                isActiveAnyFilter = true;
                databaseFilterString += filter.getGeneratedFilterString() + " AND ";
            }
        }
        if (!(filters.size() == 0) && isActiveAnyFilter == true)
            databaseFilterString = databaseFilterString.substring(0, databaseFilterString.length() - 5);
        else
            databaseFilterString = "";
        return databaseFilterString;
    }

    public List<RouteListElement> getRoutesList(List<DatabaseFilter> filters) {
        List<RouteListElement> currentList = new LinkedList<RouteListElement>();
        SQLiteDatabase readableDatabase = this.getReadableDatabase();
        Cursor currentCursor = readableDatabase.rawQuery("SELECT * FROM workoutsList" + getPreparedStringFilters(filters) + " ORDER BY timeStart DESC", null);
        if (currentCursor.moveToFirst()) {
            do {
                currentList.add(new RouteListElement(currentCursor.getInt(0), currentCursor.getLong(1), currentCursor.getDouble(2), currentCursor.getString(3)));
            }
            while (currentCursor.moveToNext());
        }
        readableDatabase.close();
        return currentList;
    }

    public void deleteRoute(long id){
        SQLiteDatabase writableDatabase = this.getWritableDatabase();
        writableDatabase.delete("workoutsList","id="+Long.toString(id), null);
        writableDatabase.delete("workoutPoint","id="+Long.toString(id), null);
        writableDatabase.close();
    }

    public List<HashMap<String, Double>> getIgnorePointsList(){
        List<HashMap<String, Double>> currentList = new LinkedList<HashMap<String, Double>>();
        SQLiteDatabase readableDatabase = this.getReadableDatabase();
        Cursor currentCursor = readableDatabase.rawQuery("SELECT * FROM ignorePointsList", null);
        if (currentCursor.moveToFirst()) {
            do {
                HashMap<String, Double> localMap = new HashMap<String, Double>();
                localMap.put("lat", currentCursor.getDouble(1));
                localMap.put("lon", currentCursor.getDouble(2));
                currentList.add(localMap);
            }
            while (currentCursor.moveToNext());
        }
        readableDatabase.close();
        return currentList;
    }
    public List<RouteElement> getPointsInRoute(long id){
        List<RouteElement> currentList = new LinkedList<RouteElement>();
        SQLiteDatabase readableDatabase = this.getReadableDatabase();
        Cursor currentCursor = readableDatabase.rawQuery("SELECT * FROM workoutPoint WHERE id=" + Long.toString(id), null);
        if (currentCursor.moveToFirst()) {
            do {
                currentList.add(new RouteElement(currentCursor.getDouble(3), currentCursor.getDouble(4),currentCursor.getDouble(5), currentCursor.getLong(1)));
            }
            while (currentCursor.moveToNext());
        }
        readableDatabase.close();
        return  currentList;
    }

    public void updateNameWorkout(long idWorkout,
                                  String name){
        SQLiteDatabase writableDatabase = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        writableDatabase.update("workoutsList", values, "id=" + Long.toString(idWorkout), null );
        writableDatabase.close();
    }

    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion,
                          int newVersion){
        if (oldVersion !=newVersion && newVersion == 2)
            db.execSQL("ALTER TABLE workoutsList ADD COLUMN name TEXT");
        else
            if (oldVersion !=newVersion && newVersion == 3)
                db.execSQL("CREATE TABLE ignorePointsList (id INTEGER PRIMARY KEY AUTOINCREMENT, latitude NUMBER, longitude NUMBER)");
             else
                onCreate(db);
    }
}
