package com.sylwke3100.freetrackgps;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity {
    public static final String MAINACTIVITY_ACTION = "MainActivityAction";
    StatusWorkout workoutStatus;
    private LocationManager service;
    private Button pauseButton, startButton;
    private SharedPreferences sharedPrefs;
    private MainActivityGuiOperations mainOperations;
    private LocationSharing currentLocation;

    protected void onCreate(Bundle savedInstanceState) {
        workoutStatus = new StatusWorkout();
        sharedPrefs = getSharedPreferences("Pref", Activity.MODE_PRIVATE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        List<TextView> textViewElements = Arrays
            .asList((TextView) this.findViewById(R.id.textGPSStatus),
                (TextView) this.findViewById(R.id.textPosition),
                (TextView) this.findViewById(R.id.textWorkoutDistance),
                (TextView) this.findViewById(R.id.textWorkoutStatus));
        pauseButton = (Button) this.findViewById(R.id.pauseButton);
        startButton = (Button) this.findViewById(R.id.startButton);
        List<Button> buttonsList = Arrays.asList(startButton, pauseButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View V) {
                onStartRoute();
            }
        });
        pauseButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View V) {
                onPauseRoute();
            }
        });
        mainOperations =
            new MainActivityGuiOperations(getBaseContext(), textViewElements, buttonsList);
        currentLocation = new LocationSharing(getBaseContext());
        currentLocation.clearCurrentLocation();
        IntentFilter mainFiler = new IntentFilter();
        mainFiler.addAction(MAINACTIVITY_ACTION);
        registerReceiver(new MainActivityReceiver(mainOperations, workoutStatus), mainFiler);
        startService(new Intent(this, GPSRunnerService.class));
        checkWorkoutStatus();
        this.mainOperations.setWorkoutInactive();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        checkWorkoutStatus();
        if (workoutStatus.status == DefaultValues.routeStatus.start
            || workoutStatus.status == DefaultValues.routeStatus.pause) {
            menu.findItem(R.id.action_workouts_list).setEnabled(false);
            menu.findItem(R.id.action_ignorepoints_list).setEnabled(false);
            menu.findItem(R.id.action_settings).setEnabled(false);
        } else {
            menu.findItem(R.id.action_workouts_list).setEnabled(true);
            menu.findItem(R.id.action_ignorepoints_list).setEnabled(true);
            menu.findItem(R.id.action_settings).setEnabled(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_ignorepoints_list:
                startActivity(new Intent(this, IgnorePointsListActivity.class));
                break;
            case R.id.action_workouts_list:
                startActivity(new Intent(this, WorkoutsListActivity.class));
                break;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.action_gpssettings:
                Intent locationSettingsIntent =
                    new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                locationSettingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(locationSettingsIntent);
                break;
            case R.id.action_about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
        }
        return true;
    }

    public void setPreviewStatus(int status) {
        if (!sharedPrefs.getBoolean("showWorkoutInfo", false)) {
            (this.findViewById(R.id.textWorkoutDistance)).setVisibility(status);
            (this.findViewById(R.id.textWorkoutStatus)).setVisibility(status);
            (this.findViewById(R.id.textDistanceLabel)).setVisibility(status);
            (this.findViewById(R.id.textWorkoutStatusLabel)).setVisibility(status);
        }
    }

    public void checkWorkoutStatus() {
        Intent message = new Intent();
        message.putExtra("command", GPSRunnerService.SERVICE_ACTION.WORKOUT_STATUS);
        message.setAction(GPSRunnerService.ACTION);
        sendBroadcast(message);
    }

    public void sendMessageToService(Integer messageAction) {
        Intent message = new Intent();
        message.putExtra("command", messageAction);
        message.setAction(GPSRunnerService.ACTION);
        sendBroadcast(message);
    }

    public void onStartRoute() {
        checkWorkoutStatus();
        if (workoutStatus.status == DefaultValues.routeStatus.stop) {
            mainOperations.setWorkoutActive();
            setPreviewStatus(View.VISIBLE);
            sendMessageToService(GPSRunnerService.SERVICE_ACTION.WORKOUT_START);
        } else if (workoutStatus.status != DefaultValues.routeStatus.stop) {
            sendMessageToService(GPSRunnerService.SERVICE_ACTION.WORKOUT_STOP);
            mainOperations.setWorkoutInactive();
            setPreviewStatus(View.INVISIBLE);
        }
    }

    public void onPauseRoute() {
        checkWorkoutStatus();
        if (workoutStatus.status == DefaultValues.routeStatus.start) {
            sendMessageToService(GPSRunnerService.SERVICE_ACTION.WORKOUT_PAUSE);
            mainOperations.setWorkoutPause();
        } else if (workoutStatus.status == DefaultValues.routeStatus.pause) {
            sendMessageToService(GPSRunnerService.SERVICE_ACTION.WORKOUT_UNPAUSE);
            mainOperations.setWorkoutActive();
        }
    }

    public void onBackPressed() {
        checkWorkoutStatus();
        if (sharedPrefs.getBoolean("exitAlert", true))
            new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.finishApp))
                .setMessage(getString(R.string.finishAppInfo))
                .setPositiveButton(this.getString(R.string.yesLabel),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (workoutStatus.status != DefaultValues.routeStatus.stop) {
                                sendMessageToService(GPSRunnerService.SERVICE_ACTION.WORKOUT_STOP);
                            }
                            finish();
                            System.exit(0);
                        }
                    }).setNegativeButton(this.getString(R.string.noLabel), null).show();
        else {
            finish();
            System.exit(0);
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        checkWorkoutStatus();
        if (workoutStatus.status != DefaultValues.routeStatus.stop) {
            sendMessageToService(GPSRunnerService.SERVICE_ACTION.WORKOUT_STOP);
        }
    }


    public class StatusWorkout {
        public DefaultValues.routeStatus status;
    }
}
