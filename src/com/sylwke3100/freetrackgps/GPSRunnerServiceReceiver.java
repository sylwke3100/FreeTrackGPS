package com.sylwke3100.freetrackgps;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class GPSRunnerServiceReceiver extends BroadcastReceiver {
    private RouteManager currentRoute;
    private Context currentContext;

    public GPSRunnerServiceReceiver(RouteManager route, Context context) {
        currentRoute = route;
        currentContext = context;
    }

    public void onReceive(Context context, Intent intent) {
        Integer command = intent.getIntExtra("command", 0);
        Log.i("GPSRunnerService", Integer.toString(command));
        switch (command) {
            case GPSRunnerService.SERVICE_ACTION.WORKOUT_START:
                currentRoute.start();
                break;
            case GPSRunnerService.SERVICE_ACTION.WORKOUT_PAUSE:
                currentRoute.pause();
                break;
            case GPSRunnerService.SERVICE_ACTION.WORKOUT_STOP:
                currentRoute.stop();
                break;
            case GPSRunnerService.SERVICE_ACTION.WORKOUT_UNPAUSE:
                currentRoute.unPause();
                break;
            case GPSRunnerService.SERVICE_ACTION.WORKOUT_STATUS:
                sendRouteStatus();
                break;
            default:
                currentRoute.stop();
                break;
        }
        sendRouteStatus();
    }

    public void sendRouteStatus() {
        Intent message = new Intent();
        message.putExtra("command", MainActivityReceiver.COMMANDS.WORKOUT_STATUS);
        if (currentRoute.getStatus() == DefaultValues.routeStatus.pause)
            message.putExtra("status", 2);
        if (currentRoute.getStatus() == DefaultValues.routeStatus.stop)
            message.putExtra("status", 0);
        if (currentRoute.getStatus() == DefaultValues.routeStatus.start)
            message.putExtra("status", 1);
        message.setAction(MainActivity.MAINACTIVITY_ACTION);
        currentContext.sendBroadcast(message);
    }
}
