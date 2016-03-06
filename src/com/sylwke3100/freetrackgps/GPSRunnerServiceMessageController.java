package com.sylwke3100.freetrackgps;


import android.content.Context;
import android.content.Intent;

public class GPSRunnerServiceMessageController {
    private Context mContext;

    public GPSRunnerServiceMessageController(Context context) {
        mContext = context;
    }

    public void sendMessageToService(Integer messageAction) {
        Intent message = new Intent();
        message.putExtra("command", messageAction);
        message.setAction(GPSRunnerService.ACTION);
        mContext.sendBroadcast(message);
    }

    public void sendMessageToGUI(String command, Intent intent) {
        intent.putExtra("command", command);
        intent.setAction(MainActivity.MAINACTIVITY_ACTION);
        mContext.sendBroadcast(intent);
    }

    public void sendMessageToGUI(String command) {
        sendMessageToGUI(command, new Intent());
    }

}
