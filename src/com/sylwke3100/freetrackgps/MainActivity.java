package com.sylwke3100.freetrackgps;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
    private RouteManager currentRoute;
    private MainActivityGuiOperations mainOperations;
    private GPSConnectionManager gpsConnect;
    private LocationSharing currentLocation;

    protected void onCreate(Bundle savedInstanceState) {
        sharedPrefs = getSharedPreferences("Pref", Activity.MODE_PRIVATE);
        super.onCreate(savedInstanceState);
        gpsConnect = new GPSConnectionManager(this);
        setContentView(R.layout.activity_main);
        service = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        List<TextView> textViewElements = Arrays
            .asList((TextView) this.findViewById(R.id.textGPSStatus),
                (TextView) this.findViewById(R.id.textPosition),
                (TextView) this.findViewById(R.id.textWorkoutDistance),
                (TextView) this.findViewById(R.id.textWorkoutStatus));
        pauseButton = (Button) this.findViewById(R.id.pauseButton);
        startButton = (Button) this.findViewById(R.id.startButton);
        List<Button> buttonsList = Arrays.asList(startButton, pauseButton);
        currentRoute = new RouteManager(this);
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
        gpsConnect.onCreateConnection(mainOperations, service, currentRoute);
        currentLocation = new LocationSharing(getBaseContext());
        currentLocation.clearCurrentLocation();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onPrepareOptionsMenu(Menu menu) {

        if (currentRoute.getStatus() == DefaultValues.routeStatus.start
            || currentRoute.getStatus() == DefaultValues.routeStatus.pause) {
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
                Intent workoutPreviewActivityIntent =
                    new Intent(this, WorkoutsPreviewActivity.class);
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

    public void onStartRoute() {
        if (currentRoute.getStatus() == DefaultValues.routeStatus.stop
            && gpsConnect.getStatus() == true) {
            currentRoute.start();
            mainOperations.setWorkoutActive();
            setPreviewStatus(View.VISIBLE);
        } else {
            if (currentRoute.getStatus() != DefaultValues.routeStatus.stop) {
                currentRoute.stop();
                mainOperations.setWorkoutInactive();
                setPreviewStatus(View.INVISIBLE);
            } else {
                Toast.makeText(getBaseContext(), getString(R.string.errorGPSConnectionInfo),
                    Toast.LENGTH_LONG).show();
            }
        }
    }

    public void onPauseRoute() {
        if (currentRoute.getStatus() == DefaultValues.routeStatus.start) {
            currentRoute.pause();
            mainOperations.setWorkoutPause();
        } else if (currentRoute.getStatus() == DefaultValues.routeStatus.pause) {
            currentRoute.unPause();
            mainOperations.setWorkoutActive();
        }
    }

    public void onBackPressed() {
        if (sharedPrefs.getBoolean("exitAlert", true))
            new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getString(R.string.finishApp))
                .setMessage(getString(R.string.finishAppInfo))
                .setPositiveButton(this.getString(R.string.yesLabel),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (currentRoute.getStatus() != DefaultValues.routeStatus.stop)
                                currentRoute.stop();
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
        if (currentRoute.getStatus() != DefaultValues.routeStatus.stop)
            currentRoute.stop();
    }
}
