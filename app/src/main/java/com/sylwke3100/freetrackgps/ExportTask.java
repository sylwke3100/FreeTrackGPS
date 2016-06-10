package com.sylwke3100.freetrackgps;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.List;


public class ExportTask extends AsyncTask<Integer, Void, Boolean> {
    private Activity progressFromActivity;
    private WorkoutsListManager localInstanceWorkoutList;
    private String nameWorkout;

    public ExportTask(Activity activity, WorkoutsListManager instanceWorkoutList){
        progressFromActivity = activity;
        localInstanceWorkoutList = instanceWorkoutList;
    }

    protected void onPreExecute() {
        progressFromActivity.showDialog(WorkoutsListActivity.EXPORT_DIALOG);
    }

    protected Boolean doInBackground(Integer... id) {
        RouteListElement object = localInstanceWorkoutList.getUpdatedWorkoutsRawList().get(id[0].intValue());
        List<RouteElement> pointsWorkout = localInstanceWorkoutList.getPointsInRoute(object.id);
        GPXWriter gpx = new GPXWriter(object.startTime, object.name);
        nameWorkout = gpx.getFilename();
        for (RouteElement point : pointsWorkout) {
            gpx.addPoint(point);
        }
        return gpx.save();
    }

    protected void onPostExecute(Boolean result) {
        progressFromActivity.removeDialog(WorkoutsListActivity.EXPORT_DIALOG);
        Context localContext = progressFromActivity.getBaseContext();
        if (result)
                Toast.makeText(localContext,
                        localContext.getString(R.string.SaveTrueInfo) + " " + nameWorkout,
                        Toast.LENGTH_LONG).show();
            else
                Toast.makeText(localContext,
                        localContext.getString(R.string.SaveFalseInfo) + " " + nameWorkout,
                        Toast.LENGTH_LONG).show();

    }

}
