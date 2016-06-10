package com.sylwke3100.freetrackgps;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

public class WorkoutMapViewReciver extends BroadcastReceiver {
    private Handler mhandler;
    private int updateCounter;

    public WorkoutMapViewReciver(Context context, Handler handler) {
        mhandler = handler;
        updateCounter = 0;
    }

    public void onReceive(Context context, Intent intent) {
        String command = intent.getStringExtra("command");
        if (command.equals(MainActivityReceiver.COMMANDS.WORKOUT_ID)) {
            intent.putExtra("currentWorkoutId", intent.getLongExtra("workoutId", -1));
            Message localMessage = new Message();
            localMessage.setData(intent.getExtras());
            mhandler.dispatchMessage(localMessage);
            updateCounter = 0;
        }
        if (command.equals(MainActivityReceiver.COMMANDS.WORKOUT_DISTANCE)){
            updateCounter++;
            Message localMessage = new Message();
            intent.putExtra("updateCounter", updateCounter);
            localMessage.setData(intent.getExtras());
            mhandler.dispatchMessage(localMessage);
            if (updateCounter == 3)
                updateCounter = 0;
        }

    }
}
