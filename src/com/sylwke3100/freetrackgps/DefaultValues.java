package com.sylwke3100.freetrackgps;

public class DefaultValues {
    public static int defaultMinSpeedIndex = 1;
    public static int defaultMinDistanceIndex = 2;


    public static enum routeStatus {
        stop,
        pause,
        start
    }


    public static String defaultDatabaseName = "workout";
    public static String defaultFolderWithWorkout = "/workout/";
    public static String defaultFileFormat = "gpx";


    public static enum areaStatus {
        ok,
        prohibited
    }
}
