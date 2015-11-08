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
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity {
    private LocationManager service;
    private Button pauseButton, startButton;
    private SharedPreferences sharedPrefs;
    private MainActivityGuiOperations mainOperations;


    public class StatusWorkout {
        public DefaultValues.routeStatus status;
    }


    StatusWorkout workoutStatus;
    private LocationSharing currentLocation;
    public static final String MAINACTIVITY_ACTION = "MainActivityAction";

    protected void onCreate(Bundle savedInstanceState) {
        workoutStatus = new StatusWorkout();
        sharedPrefs = getSharedPreferences("Pref", Activity.MODE_PRIVATE);
        //workoutStatus = DefaultValues.routeStatus.stop;
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
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        checkWorkoutStatus();
        if (workoutStatus.status == DefaultValues.routeStatus.start
            || workoutStatus.status == DefaultValues.routeStatus.pause) {
            menu.findItem(R.id.action_workout).setEnabled(false);
            menu.findItem(R.id.action_ignorepoints).setEnabled(false);
            menu.findItem(R.id.action_settings).setEnabled(false);
        } else {
            menu.findItem(R.id.action_workout).setEnabled(true);
            menu.findItem(R.id.action_ignorepoints).setEnabled(true);
            menu.findItem(R.id.action_settings).setEnabled(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_ignorepoints:
                Intent ignorePointsIntent = new Intent(this, IgnorePointsActivity.class);
                startActivity(ignorePointsIntent);
                break;
            case R.id.action_workout:
                Intent workoutPreviewActivityIntent = new Intent(this, WorkoutsListActivity.class);
                startActivity(workoutPreviewActivityIntent);
                break;
            case R.id.action_settings:
                Intent settingActivityIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingActivityIntent);
                break;
            case R.id.action_gpssettings:
                Intent locationSettingsIntent =
                    new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                locationSettingsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(locationSettingsIntent);
                break;
            case R.id.action_about:
                Intent AboutActivityIntent = new Intent(this, AboutActivity.class);
                startActivity(AboutActivityIntent);
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
        message.putExtra("command", GPSRunnerService.SERVICE_ACTION.STATUS_ACTION);
        message.setAction(GPSRunnerService.ACTION);
        sendBroadcast(message);
    }

    public void sendActiontoService(Integer action) {
        Intent message = new Intent();
        message.putExtra("command", action);
        message.setAction(GPSRunnerService.ACTION);
        sendBroadcast(message);
    }

    public void onStartRoute() {
        checkWorkoutStatus();
        if (workoutStatus.status == DefaultValues.routeStatus.stop) {
            mainOperations.setWorkoutActive();
            setPreviewStatus(View.VISIBLE);
            sendActiontoService(GPSRunnerService.SERVICE_ACTION.START_ACTION);
        } else {
            if (workoutStatus.status != DefaultValues.routeStatus.stop) {
                sendActiontoService(GPSRunnerService.SERVICE_ACTION.STOP_ACTION);
                mainOperations.setWorkoutInactive();
                setPreviewStatus(View.INVISIBLE);
            } else {
                Toast.makeText(getBaseContext(), getString(R.string.errorGPSConnectionInfo),
                    Toast.LENGTH_LONG).show();
            }
        }
    }

    public void onPauseRoute() {
        checkWorkoutStatus();
        if (workoutStatus.status == DefaultValues.routeStatus.start) {
            sendActiontoService(GPSRunnerService.SERVICE_ACTION.PAUSE_ACTION);
            mainOperations.setWorkoutPause();
        } else if (workoutStatus.status == DefaultValues.routeStatus.pause) {
            sendActiontoService(GPSRunnerService.SERVICE_ACTION.UNPAUSE_ACTION);
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
                                sendActiontoService(GPSRunnerService.SERVICE_ACTION.STOP_ACTION);
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
            sendActiontoService(GPSRunnerService.SERVICE_ACTION.STOP_ACTION);
        }
    }
}
