package com.example.freetrackgps;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseManager extends SQLiteOpenHelper {
    private static String databaseName = "workout";
    private static final int databaseVersion = 1;

    public DatabaseManager(Context context){
        super(context, databaseName, null, databaseVersion);
    }

    public void onCreate(SQLiteDatabase db){
        String query = "CREATE TABLE workoutsList (id INTEGER PRIMARY KEY AUTOINCREMENT , timeStart INTEGER, distance NUMBER)";
        db.execSQL(query);
        query = "CREATE TABLE workoutPoint (id INTEGER, timestamp INTEGER, distance NUMBER, latitude NUMBER, longitude NUMBER, altitude NUMBER)";
        db.execSQL(query);
    }

    public long startWorkout(long timeStartWorkout){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("timeStart", timeStartWorkout);
        values.put("distance", 0);
        long id = db.insert("workoutsList",null, values );
        db.close();
        return id;

    }

    public void addPoint(long workoutId, RouteElement point, double distance){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        ContentValues updatedValues = new ContentValues();
        values.put("id", workoutId);
        values.put("timestamp", point.time);
        values.put("distance", distance);
        values.put("latitude", point.lat);
        values.put("longitude", point.lon);
        values.put("altitude", point.alt);
        db.insert("workoutPoint", null, values);
        updatedValues.put("distance", distance);
        String filter = "id="+workoutId;
        db.update("workoutsList", updatedValues, filter, null);
        db.close();

    }


    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        onCreate(db);
    }
}
