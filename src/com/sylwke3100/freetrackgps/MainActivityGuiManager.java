package com.sylwke3100.freetrackgps;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class MainActivityGuiManager {
    Context localContext;
    private TextView gpsWorkStatus, gpsPosition, workoutStatus, workoutDistance;
    private Button startButton, pauseButton;
    private boolean pauseButtonStatus = false;

    public MainActivityGuiManager(Context mainContext, List<TextView> textViewElements,
                                  List<Button> buttonsList) {
        gpsWorkStatus = textViewElements.get(0);
        gpsPosition = textViewElements.get(1);
        workoutDistance = textViewElements.get(2);
        workoutStatus = textViewElements.get(3);
        startButton = buttonsList.get(0);
        pauseButton = buttonsList.get(1);
        localContext = mainContext;
    }

    public void setOnGPS() {
        this.gpsWorkStatus.setText(this.localContext.getString(R.string.onLabel));
    }

    public void setOffGPS() {
        this.gpsWorkStatus.setText(this.localContext.getString(R.string.offLabel));
    }

    public void setGpsPosition(double latitude, double longitude) {
        String message = String.format(" %1$s %2$s", String.format("%.2f", longitude),
            String.format("%.2f", latitude));
        this.gpsPosition.setText(message);
    }

    public void setWorkoutDistance(double distance) {
        this.workoutDistance.setText(String.format("%.2f km", distance));
    }

    public void setWorkoutActive() {
        this.workoutStatus.setText(this.localContext.getString(R.string.activeLabel));
        this.startButton.setText(this.localContext.getString(R.string.stopLabel));
        this.pauseButton.setText(this.localContext.getString(R.string.pauseLabel));
        this.pauseButton.setVisibility(View.VISIBLE);
    }

    public void setWorkoutPause() {
        this.workoutStatus.setText(this.localContext.getString(R.string.pauseLabel));
        this.pauseButton.setText(this.localContext.getString(R.string.unPauseLabel));
    }

    public void setWorkoutInactive() {
        this.workoutStatus.setText("--");
        this.startButton.setText(this.localContext.getString(R.string.startLabel));
        this.setWorkoutDistance(0);
        this.pauseButton.setVisibility(View.INVISIBLE);
    }
}
