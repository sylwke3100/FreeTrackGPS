package com.sylwke3100.freetrackgps;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
    private Button pauseButton, startButton;
    private SharedPreferences sharedPrefs;
    private MainActivityGuiManager mainOperations;
    private LocationSharing currentLocation;
    private GPSRunnerServiceMessageController messageController;
    private Intent serviceIntent;

    protected void onCreate(Bundle savedInstanceState) {
        serviceIntent = new Intent(this, GPSRunnerService.class);
        messageController = new GPSRunnerServiceMessageController(getBaseContext());
        workoutStatus = new StatusWorkout();
        sharedPrefs = getSharedPreferences("com.sylwke3100.freetrackgps_preferences", Activity.MODE_PRIVATE);
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
                new MainActivityGuiManager(getBaseContext(), textViewElements, buttonsList);
        currentLocation = new LocationSharing(getBaseContext());
        currentLocation.clearCurrentLocation();
        IntentFilter mainFiler = new IntentFilter();
        mainFiler.addAction(MAINACTIVITY_ACTION);
        registerReceiver(new MainActivityReceiver(mainOperations, workoutStatus), mainFiler);
        startService(serviceIntent);
        updateWorkoutStatus();
        setPreviewStatus(View.INVISIBLE);
        this.mainOperations.setWorkoutInactive();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        updateWorkoutStatus();
        if (workoutStatus.status == DefaultValues.routeStatus.start
                || workoutStatus.status == DefaultValues.routeStatus.pause) {
            menu.findItem(R.id.action_workouts_list).setEnabled(false);
            menu.findItem(R.id.action_ignorepoints_list).setEnabled(false);
            menu.findItem(R.id.action_settings).setEnabled(false);
            if (!sharedPrefs.getBoolean("disableMap", false))
                menu.findItem(R.id.action_workout_mapview).setEnabled(true);
            else
                menu.findItem(R.id.action_workout_mapview).setEnabled(false);


        } else {
            menu.findItem(R.id.action_workouts_list).setEnabled(true);
            menu.findItem(R.id.action_ignorepoints_list).setEnabled(true);
            menu.findItem(R.id.action_settings).setEnabled(true);
            menu.findItem(R.id.action_workout_mapview).setEnabled(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intentToStart = new Intent();
        switch (item.getItemId()) {
            case R.id.action_workout_mapview:
                intentToStart = new Intent(this, WorkoutMapViewActivity.class);
                break;
            case R.id.action_ignorepoints_list:
                intentToStart = new Intent(this, IgnorePointsListActivity.class);
                break;
            case R.id.action_workouts_list:
                intentToStart = new Intent(this, WorkoutsListActivity.class);
                break;
            case R.id.action_settings:
                intentToStart = new Intent(this, SettingsActivity.class);
                break;
            case R.id.action_gpssettings:
                intentToStart =
                        new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intentToStart.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                break;
            case R.id.action_about:
                intentToStart = new Intent(this, AboutActivity.class);
                break;
        }
        startActivity(intentToStart);
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

    public void updateWorkoutStatus() {
        messageController.sendMessageToService(GPSRunnerService.SERVICE_ACTION.WORKOUT_STATUS);
    }

    public void onStartRoute() {
        updateWorkoutStatus();
        if (workoutStatus.status == DefaultValues.routeStatus.stop) {
            mainOperations.setWorkoutActive();
            setPreviewStatus(View.VISIBLE);
            messageController.sendMessageToService(GPSRunnerService.SERVICE_ACTION.WORKOUT_START);
        } else if (workoutStatus.status != DefaultValues.routeStatus.stop) {
            messageController.sendMessageToService(GPSRunnerService.SERVICE_ACTION.WORKOUT_STOP);
            mainOperations.setWorkoutInactive();
            setPreviewStatus(View.INVISIBLE);
        }
    }

    public void onPauseRoute() {
        updateWorkoutStatus();
        if (workoutStatus.status == DefaultValues.routeStatus.start) {
            messageController.sendMessageToService(GPSRunnerService.SERVICE_ACTION.WORKOUT_PAUSE);
            mainOperations.setWorkoutPause();
        } else if (workoutStatus.status == DefaultValues.routeStatus.pause) {
            messageController.sendMessageToService(GPSRunnerService.SERVICE_ACTION.WORKOUT_UNPAUSE);
            mainOperations.setWorkoutActive();
        }
    }

    public void onBackPressed() {
        updateWorkoutStatus();
        if (sharedPrefs.getBoolean("exitAlert", true))
            new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(getString(R.string.finishApp))
                    .setMessage(getString(R.string.finishAppInfo))
                    .setPositiveButton(this.getString(R.string.yesLabel),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if (workoutStatus.status != DefaultValues.routeStatus.stop) {
                                        messageController.sendMessageToService(GPSRunnerService.SERVICE_ACTION.WORKOUT_STOP);
                                    }
                                    finish();
                                    stopService(serviceIntent);
                                    System.exit(0);
                                }
                            }).setNegativeButton(this.getString(R.string.noLabel), null).show();
        else {
            finish();
            System.exit(0);
        }
    }

    protected void onResume() {
        updateWorkoutStatus();
        if (workoutStatus.status == DefaultValues.routeStatus.stop) {
            mainOperations.setWorkoutInactive();
            setPreviewStatus(View.INVISIBLE);
        }
        if (workoutStatus.status == DefaultValues.routeStatus.start) {
            mainOperations.setWorkoutActive();
            setPreviewStatus(View.VISIBLE);
        }
        if (workoutStatus.status == DefaultValues.routeStatus.pause) {
            mainOperations.setWorkoutPause();
        }

        super.onResume();
    }

    protected void onDestroy() {
        super.onDestroy();
        updateWorkoutStatus();
        if (workoutStatus.status != DefaultValues.routeStatus.stop) {
            messageController.sendMessageToService(GPSRunnerService.SERVICE_ACTION.WORKOUT_STOP);
        }
        stopService(serviceIntent);
    }

    public class StatusWorkout {
        public DefaultValues.routeStatus status;
    }
}
