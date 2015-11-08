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
            Intent gpsIntent = new Intent();
            gpsIntent.putExtra("command", "gpsOn");
            sendMessageToUi(gpsIntent);
            Location lastLocation =
                (Location) service.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            gpsCurrentStatus = true;
            if (lastLocation != null) {
                if (currentRoute.getStatus() != DefaultValues.routeStatus.stop) {
                    Intent gpsPosIntent = new Intent();
                    gpsPosIntent.putExtra("command", "gpsPos");
                    gpsPosIntent.putExtra("lat", lastLocation.getLatitude());
                    gpsPosIntent.putExtra("lon", lastLocation.getLongitude());
                    sendMessageToUi(gpsPosIntent);
                }
            }
        } else {
            Intent gpsIntent = new Intent();
            gpsIntent.putExtra("command", "gpsOff");
            sendMessageToUi(gpsIntent);
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

    public void sendMessageToUi(Intent messageIntent) {
        messageIntent.setAction(MainActivity.MAINACTIVITY_ACTION);
        sendBroadcast(messageIntent);
    }

    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    public static class SERVICE_ACTION {
        public static final int START_ACTION = 1;
        public static final int STOP_ACTION = 0;
        public static final int PAUSE_ACTION = 2;
        public static final int STATUS_ACTION = 3;
        public static final int UNPAUSE_ACTION = 4;
    }


    public class LocalBinder extends Binder {
        GPSRunnerService getService() {
            return GPSRunnerService.this;
        }
    }

}
