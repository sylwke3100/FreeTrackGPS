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

    public void updateValues(String table, ContentValues values, String conditions){
        writableDatabase.update(table, values, conditions, null);
    }

    public Cursor getCursor(String table, String selection, String[] selectionArg){
        Cursor currentCursor = readableDatabase.query(table, null, selection, selectionArg, null, null, null );
        return currentCursor;
    }

    public Cursor getCursor(String table, String selection, String[] selectionArg, String order){
        Cursor currentCursor = readableDatabase.query(table, null, selection, selectionArg, null, null, order );
        return currentCursor;
    }

    public long insertValues(String table, ContentValues values){
        return writableDatabase.insert(table, null, values);
    }

    public void deleteValues(String table, String conditions, String[] conditionsArgs){
        writableDatabase.delete(table, conditions, conditionsArgs);
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
