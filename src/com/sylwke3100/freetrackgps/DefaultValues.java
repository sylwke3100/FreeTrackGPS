package com.sylwke3100.freetrackgps;

public final class DefaultValues {
    public static String defaultMinSpeedIndex = "1";
    public static String defaultMinDistanceIndex = "2";
    public static String defaultDatabaseName = "workout";
    public static String defaultFolderWithWorkout = "/workout/";
    public static String defaultFileFormat = "gpx";
    public static enum routeStatus {
        stop,
        pause,
        start
    }


    public static enum areaStatus {
        ok,
        prohibited
    }
    public static String prefs= "com.sylwke3100.freetrackgps_preferences";
}
