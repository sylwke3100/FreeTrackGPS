package com.sylwke3100.freetrackgps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class MainActivityReceiver extends BroadcastReceiver {
    public MainActivityGuiOperations globalGuiOperations;
    public MainActivity.StatusWorkout statusRoute;

    public MainActivityReceiver(MainActivityGuiOperations operations,
        MainActivity.StatusWorkout status) {
        globalGuiOperations = operations;
        statusRoute = status;
    }

    public void onReceive(Context context, Intent intent) {
        String command = intent.getStringExtra("command");
        Log.i("GPSRunnerService", "command: " + command);
        if (command.equals("gpsOn"))
            globalGuiOperations.setOnGPS();
        if (command.equals("gpsOff"))
            globalGuiOperations.setOffGPS();
        if (command.equals("workoutActive"))
            globalGuiOperations.setWorkoutActive();
        if (command.equals("workoutPause"))
            globalGuiOperations.setWorkoutPause();

        if (command.equals("workoutInactive"))
            globalGuiOperations.setWorkoutInactive();
        if (command.equals("gpsPos"))
            globalGuiOperations
                .setGpsPosition(intent.getDoubleExtra("lat", 0), intent.getDoubleExtra("lon", 0));
        if (command.equals("workoutDistance"))
            globalGuiOperations.setWorkoutDistance(intent.getDoubleExtra("dist", 0));
        if (command.equals("workoutStatus")) {
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
