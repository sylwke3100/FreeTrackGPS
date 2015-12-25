package com.sylwke3100.freetrackgps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class MainActivityReceiver extends BroadcastReceiver {
    public static class COMMANDS{
        public static String GPS_ON = "gpsOn";
        public static String GPS_OFF ="gpsOff";
        public static String GPS_POS = "gpsPos";
        public static String WORKOUT_ACTIVE = "workoutActive";
        public static String WORKOUT_INACTIVE = "workoutInactive";
        public static String WORKOUT_PAUSE = "workoutPause";
        public static String WORKOUT_DISTANCE = "workoutDistance";
        public static String WORKOUT_STATUS = "workoutStatus";
    }
    public MainActivityGuiManager globalGuiOperations;
    public MainActivity.StatusWorkout statusRoute;

    public MainActivityReceiver(MainActivityGuiManager operations,
        MainActivity.StatusWorkout status) {
        globalGuiOperations = operations;
        statusRoute = status;
    }

    public void onReceive(Context context, Intent intent) {
        String command = intent.getStringExtra("command");
        Log.i("GPSRunnerService", "action: " + intent.getAction());
        Log.i("GPSRunnerService", "command: " + command);
        if (command.equals(COMMANDS.GPS_ON))
            globalGuiOperations.setOnGPS();
        if (command.equals(COMMANDS.GPS_OFF))
            globalGuiOperations.setOffGPS();
        if (command.equals(COMMANDS.WORKOUT_ACTIVE))
            globalGuiOperations.setWorkoutActive();
        if (command.equals(COMMANDS.WORKOUT_PAUSE))
            globalGuiOperations.setWorkoutPause();
        if (command.equals(COMMANDS.WORKOUT_INACTIVE))
            globalGuiOperations.setWorkoutInactive();
        if (command.equals(COMMANDS.GPS_POS))
            globalGuiOperations
                .setGpsPosition(intent.getDoubleExtra("lat", 0), intent.getDoubleExtra("lon", 0));
        if (command.equals(COMMANDS.WORKOUT_DISTANCE))
            globalGuiOperations.setWorkoutDistance(intent.getDoubleExtra("dist", 0));
        if (command.equals(COMMANDS.WORKOUT_STATUS)) {
            Log.i("GPSRunnerService", "workoutStatus: " + intent.getIntExtra("status", 0));
            switch (intent.getIntExtra("status", 0)) {
                case 0:
                    this.statusRoute.status = DefaultValues.routeStatus.stop;
                    break;
                case 1:
                    this.statusRoute.status = DefaultValues.routeStatus.start;
                    break;
                case 2:
                    this.statusRoute.status = DefaultValues.routeStatus.pause;
                    break;
            }
        }
    }
}
