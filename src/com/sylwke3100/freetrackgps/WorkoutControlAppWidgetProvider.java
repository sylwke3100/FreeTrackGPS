package com.sylwke3100.freetrackgps;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;


public class WorkoutControlAppWidgetProvider extends AppWidgetProvider {

    private static DefaultValues.routeStatus currentStatus = DefaultValues.routeStatus.stop;
    private Double distance = 0.0;
    private GPSRunnerServiceMessageController messageController;
    private String BUTTON_WIDGET_ACTION = "com.sylwke3100.freetrackgps.BUTTON_WIDGET_ACTION";

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.i("UpdateWidget", "Start");
        messageController = new GPSRunnerServiceMessageController(context);
        sendRequestStatus(context);
        for (int i = 0; i < appWidgetIds.length; i++) {
            RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.appwidget_workout_control);
            Log.i("UpdateWidget", Double.toString(distance) + " km");
            view.setTextViewText(R.id.distanceWidgetTextView, Double.toString(distance) + " km");
            Intent buttonIntent = new Intent(BUTTON_WIDGET_ACTION);
            Intent mainAcivity = new Intent(context, MainActivity.class);
            PendingIntent mainAcivityPending = PendingIntent.getActivity(context, 0, mainAcivity, PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            view.setOnClickPendingIntent(R.id.startStopButton, pendingIntent);
            view.setOnClickPendingIntent(R.id.widgetAcitivty, mainAcivityPending);
            appWidgetManager.updateAppWidget(appWidgetIds[i], view);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    public void onReceive(Context context, Intent intent) {
        messageController = new GPSRunnerServiceMessageController(context);
        super.onReceive(context, intent);
        Log.i("ReciveWidget ", intent.getAction());
        if (intent.getStringExtra("command") != null) {
            Log.i("ReciveWidget ", intent.getStringExtra("command"));
            String command = intent.getStringExtra("command");
            Log.i("ReciveWidget ", "command " + intent.getStringExtra("command"));
            if (command.equals(MainActivityReceiver.COMMANDS.WORKOUT_STATUS)) {
                switch (intent.getIntExtra("status", 0)) {
                    case 0:
                        currentStatus = DefaultValues.routeStatus.stop;
                        break;
                    case 1:
                        currentStatus = DefaultValues.routeStatus.start;
                        break;
                    case 2:
                        currentStatus = DefaultValues.routeStatus.pause;
                        break;
                }
                Log.i("ReciveWidgetStatus", "workoutStatus " + Integer.toString(intent.getIntExtra("status", 0)));
                onChangeButtonStatus(context);
            }
            if (command.equals(MainActivityReceiver.COMMANDS.WORKOUT_DISTANCE)) {
                distance = intent.getDoubleExtra("dist", 0.0);
                Log.i("ReciveWidget", "Distance: " + Double.toString(distance) + " km");
                RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.appwidget_workout_control);
                remoteViews.setTextViewText(R.id.distanceWidgetTextView, String
                        .format("%.2f km ", distance));
                ComponentName componentName = new ComponentName(context, WorkoutControlAppWidgetProvider.class);
                AppWidgetManager.getInstance(context).updateAppWidget(componentName, remoteViews);
            }
        }
        if (intent.getAction().equals(BUTTON_WIDGET_ACTION)) {
            Log.i("ReciveWidget", "Button up");
            if (currentStatus == DefaultValues.routeStatus.start)
                messageController.sendMessageToService(GPSRunnerService.SERVICE_ACTION.WORKOUT_PAUSE);
            if (currentStatus == DefaultValues.routeStatus.pause)
                messageController.sendMessageToService(GPSRunnerService.SERVICE_ACTION.WORKOUT_UNPAUSE);
            if (currentStatus == DefaultValues.routeStatus.stop)
                messageController.sendMessageToService(GPSRunnerService.SERVICE_ACTION.WORKOUT_START);
            Log.i("ReciveWidgetStatus", currentStatus.toString());
        }
    }

    public void onChangeButtonStatus(Context context) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.appwidget_workout_control);
        if (currentStatus == DefaultValues.routeStatus.pause || currentStatus == DefaultValues.routeStatus.stop)
            remoteViews.setImageViewResource(R.id.startStopButton, android.R.drawable.ic_media_play);
        else
            remoteViews.setImageViewResource(R.id.startStopButton, android.R.drawable.ic_media_pause);
        ComponentName componentName = new ComponentName(context, WorkoutControlAppWidgetProvider.class);
        AppWidgetManager.getInstance(context).updateAppWidget(componentName, remoteViews);
    }

    public void sendRequestStatus(Context context) {
        messageController = new GPSRunnerServiceMessageController(context);
        messageController.sendMessageToService(GPSRunnerService.SERVICE_ACTION.WORKOUT_STATUS);
    }
}
