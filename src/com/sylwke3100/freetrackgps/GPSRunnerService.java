package com.sylwke3100.freetrackgps;


import android.app.Activity;
import android.app.Service;
import android.content.*;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class GPSRunnerService extends Service {
    public static final String ACTION = "GPSRunnerService";
    private final IBinder mBinder = new LocalBinder();
    private BroadcastReceiver gPSRunnerServiceReceiver;
    private RouteManager currentRoute;
    private SharedPreferences sharedPrefs;
    private boolean gpsCurrentStatus = false;
    private LocationManager service;

    private void onCreateGPSConnection() {
        int[] timeSettingArray = this.getResources().getIntArray(R.array.timeArray);
        int[] distanceSettingArray = this.getResources().getIntArray(R.array.distanceArray);
        if (service != null) {
            service.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                timeSettingArray[(sharedPrefs.getInt("time", DefaultValues.defaultMinSpeedIndex))],
                distanceSettingArray[(sharedPrefs.getInt("distance", 1))],
                new GPSListener(currentRoute, this));
            sendMessageToUi("gpsOn", new Intent());
            Location lastLocation =
                (Location) service.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            gpsCurrentStatus = true;
            if (lastLocation != null) {
                if (currentRoute.getStatus() != DefaultValues.routeStatus.stop) {
                    Intent gpsPosIntent = new Intent();
                    gpsPosIntent.putExtra("lat", lastLocation.getLatitude());
                    gpsPosIntent.putExtra("lon", lastLocation.getLongitude());
                    sendMessageToUi("gpsPos", gpsPosIntent);
                }
            }
        } else {
            sendMessageToUi("gpsOff", new Intent());
            service.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                timeSettingArray[(sharedPrefs.getInt("time", DefaultValues.defaultMinSpeedIndex))],
                distanceSettingArray[(sharedPrefs.getInt("distance", 1))],
                new GPSListener(currentRoute, this));
        }
    }

    public void onCreate() {
        currentRoute = new RouteManager(this);
        service = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        sharedPrefs = this.getSharedPreferences("Pref", Activity.MODE_PRIVATE);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION);
        registerReceiver(new GPSRunnerServiceReceiver(currentRoute, this), intentFilter);
        onCreateGPSConnection();
        super.onCreate();
    }



    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("GPSRunnerService", "Received start id " + startId + ": " + intent);
        return START_STICKY_COMPATIBILITY;
    }

    public void onDestroy() {
        Toast.makeText(this, "Service Stop", Toast.LENGTH_SHORT).show();
        unregisterReceiver(gPSRunnerServiceReceiver);
        if (currentRoute.getStatus() != DefaultValues.routeStatus.stop)
            currentRoute.stop();
        super.onDestroy();
    }

    private void sendMessageToUi(String command, Intent intent) {
        intent.putExtra("command", command);
        intent.setAction(MainActivity.MAINACTIVITY_ACTION);
        sendBroadcast(intent);
    }

    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    public static class SERVICE_ACTION {
        public static final int WORKOUT_STOP = 0;
        public static final int WORKOUT_START = 1;
        public static final int WORKOUT_PAUSE = 2;
        public static final int WORKOUT_STATUS = 3;
        public static final int WORKOUT_UNPAUSE = 4;
    }


    public class LocalBinder extends Binder {
        GPSRunnerService getService() {
            return GPSRunnerService.this;
        }
    }

}