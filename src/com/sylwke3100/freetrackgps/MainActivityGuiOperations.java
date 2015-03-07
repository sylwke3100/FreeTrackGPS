package com.sylwke3100.freetrackgps;

import android.content.Context;
import android.widget.TextView;

import java.util.List;

public class MainActivityGuiOperations {
    private TextView gpsWorkStatus, gpsPosition, workoutSpeed, workoutDistance;
    Context localContext;
    public MainActivityGuiOperations(Context mainContext,
                                     List<TextView> textViewElements){
        gpsWorkStatus = textViewElements.get(0);
        gpsPosition = textViewElements.get(1);
        workoutSpeed = textViewElements.get(2);
        workoutDistance = textViewElements.get(3);
        localContext = mainContext;
    }

    public void setOnGPS(){
        this.gpsWorkStatus.setText(this.localContext.getString(R.string.onLabel));
    }

    public void setOffGPS(){
        this.gpsWorkStatus.setText(this.localContext.getString(R.string.offLabel));
    }

    public void setGpsPosition(double latitude,
                               double longitude ){
        String message = String.format( " %1$s %2$s",String.format( "%.2f", longitude), String.format( "%.2f", latitude));
        this.gpsPosition.setText(message);
    }

    public void setWorkoutSpeed(double speed){
        this.workoutSpeed.setText(String.format("%d km/h", (int) speed));
    }

    public void setWorkoutDistance(double distance){
        this.workoutDistance.setText(String.format("%.2f km", distance));
    }
}
