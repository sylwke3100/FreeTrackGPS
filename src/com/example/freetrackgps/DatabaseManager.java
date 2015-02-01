package com.example.freetrackgps;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.LinkedList;
import java.util.List;

public class DatabaseManager extends SQLiteOpenHelper {
    private static final int databaseVersion = 1;

    public DatabaseManager(Context context){
        super(context, DefaultValues.defaultDatabaseName, null, databaseVersion);
    }

    public void onCreate(SQLiteDatabase db){
        String query = "CREATE TABLE workoutsList (id INTEGER PRIMARY KEY AUTOINCREMENT , timeStart INTEGER, distance NUMBER)";
        db.execSQL(query);
        query = "CREATE TABLE workoutPoint (id INTEGER, timestamp INTEGER, distance NUMBER, latitude NUMBER, longitude NUMBER, altitude NUMBER)";
        db.execSQL(query);
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
        String filter = "id="+workoutId;
        writableDatabase.update("workoutsList", updatedValues, filter, null);
        writableDatabase.close();
    }

    public List<RouteListElement> getRoutesList() {
        List<RouteListElement> currentList = new LinkedList<RouteListElement>();
        SQLiteDatabase readableDatabase = this.getReadableDatabase();
        Cursor currentCursor = readableDatabase.rawQuery("SELECT * FROM workoutsList ORDER BY timeStart DESC", null);
        if (currentCursor.moveToFirst()) {
            do {
                currentList.add(new RouteListElement(currentCursor.getInt(0), currentCursor.getLong(1), currentCursor.getDouble(2)));
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

    public List<RouteElement> getPointsInRoute(long id){
        List<RouteElement> currentList = new LinkedList<RouteElement>();
        SQLiteDatabase readableDatabase = this.getReadableDatabase();
        Cursor currentCursor = readableDatabase.rawQuery("SELECT * FROM workoutPoint WHERE id="+Long.toString(id), null);
        if (currentCursor.moveToFirst()) {
            do {
                currentList.add(new RouteElement(currentCursor.getDouble(3), currentCursor.getDouble(4),currentCursor.getDouble(5), currentCursor.getLong(1)));
            }
            while (currentCursor.moveToNext());
        }
        readableDatabase.close();
        return  currentList;
    }

    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion,
                          int newVersion){
        onCreate(db);
    }
}
