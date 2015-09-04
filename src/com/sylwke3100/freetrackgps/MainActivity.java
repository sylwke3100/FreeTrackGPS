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
    private TextView gpsStatus, gpsPosition, workoutStatus, workoutDistance;
    private String provider;
    private List<TextView> textViewElements;
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
        gpsStatus = (TextView) this.findViewById(R.id.textGPSStatus);
        gpsPosition = (TextView) this.findViewById(R.id.textPosition);
        workoutStatus = (TextView) this.findViewById(R.id.textWorkoutStatus);
        workoutDistance = (TextView) this.findViewById(R.id.textWorkOut);
        ;
        textViewElements = Arrays.asList(gpsStatus, gpsPosition, workoutDistance);
        pauseButton = (Button) this.findViewById(R.id.pauseButton);
        startButton = (Button) this.findViewById(R.id.startButton);
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
        mainOperations = new MainActivityGuiOperations(getBaseContext(), textViewElements);
        gpsConnect.onCreateConnection(mainOperations, service, currentRoute);
        if (currentRoute.getStatus() != DefaultValues.routeStatus.start)
            setPreviewStatus(View.INVISIBLE);
        else
            setPreviewStatus(View.VISIBLE);
        currentLocation = new LocationSharing(getBaseContext());
        currentLocation.clearCurrentLocation();
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onPrepareOptionsMenu(Menu menu) {

        if (currentRoute.getStatus() == DefaultValues.routeStatus.start) {
            menu.findItem(R.id.action_workout).setEnabled(false);
            menu.findItem(R.id.action_ignorePoints).setEnabled(false);
            menu.findItem(R.id.action_settings).setEnabled(false);
        } else {
            menu.findItem(R.id.action_workout).setEnabled(true);
            menu.findItem(R.id.action_ignorePoints).setEnabled(true);
            menu.findItem(R.id.action_settings).setEnabled(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_ignorePoints:
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
            case R.id.gpsSetting:
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
        if (sharedPrefs.getBoolean("showWorkoutInfo", false) == false) {
            workoutDistance.setVisibility(status);
            workoutStatus.setVisibility(status);
            ((TextView) this.findViewById(R.id.textDistanceLabel)).setVisibility(status);
            ((TextView) this.findViewById(R.id.textWorkoutStatusLabel)).setVisibility(status);
        }
    }

    public void onStartRoute() {
        if (currentRoute.getStatus() == DefaultValues.routeStatus.stop
            && gpsConnect.getStatus() == true) {
            currentRoute.start();
            workoutStatus.setText(getString(R.string.activeLabel));
            startButton.setText(getString(R.string.stopLabel));
            setPreviewStatus(View.VISIBLE);
        } else {
            if (currentRoute.getStatus() != DefaultValues.routeStatus.stop) {
                currentRoute.stop();
                workoutStatus.setText("--");
                startButton.setText(getString(R.string.startLabel));
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
            workoutStatus.setText(getString(R.string.pauseLabel));
            pauseButton.setText(getString(R.string.unPauseLabel));
        } else if (currentRoute.getStatus() == DefaultValues.routeStatus.pause) {
            currentRoute.unPause();
            workoutStatus.setText(getString(R.string.activeLabel));
            pauseButton.setText(getString(R.string.pauseLabel));
        }
    }

    public void onBackPressed() {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle(getString(R.string.finishApp)).setMessage(getString(R.string.finishAppInfo))
            .setPositiveButton(this.getString(R.string.yesLabel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (currentRoute.getStatus() != DefaultValues.routeStatus.stop)
                            currentRoute.stop();
                        finish();
                        System.exit(0);
                    }
                }).setNegativeButton(this.getString(R.string.noLabel), null).show();
    }

    protected void onDestroy() {
        super.onDestroy();
        if (currentRoute.getStatus() != DefaultValues.routeStatus.stop)
            currentRoute.stop();
    }
}
