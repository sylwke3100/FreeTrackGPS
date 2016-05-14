package com.sylwke3100.freetrackgps;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;


public class WorkoutInfoDetailsFragment extends Fragment {
    private static final SimpleDateFormat formatDate = new SimpleDateFormat("HH:mm dd.MM.yyyy");
    private long lastEndDate = 0;
    private double maxHeight = 0, minHeight = 0;
    private int pointCount = 0;
    private Context context;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myInflate = inflater.inflate(R.layout.activity_workout_info, container, false);
        context = myInflate.getContext();
        Bundle localBundle = getArguments();
        if (localBundle != null) {
            calculateRouteProperties(localBundle.getInt("routeId"));
            RouteListElement currentRoute = getRouteInfo(localBundle.getInt("routeId"));
            ((TextView) myInflate.findViewById(R.id.distanceText))
                    .setText(String.format("%.2f km", currentRoute.distance / 1000));
            ((TextView) myInflate.findViewById(R.id.startTimeText))
                    .setText(formatDate.format(currentRoute.startTime));
            if (lastEndDate != 0)
                ((TextView) myInflate.findViewById(R.id.endTimeText))
                        .setText(formatDate.format(lastEndDate));
            else
                ((TextView) myInflate.findViewById(R.id.endTimeText)).setText("- -");
            if (currentRoute.name != null)
                ((TextView) myInflate.findViewById(R.id.nameWorkoutTextView))
                        .setText(currentRoute.name);
            else
                ((TextView) myInflate.findViewById(R.id.nameWorkoutTextView))
                        .setText(getString(R.string.Unnamend));
            ((TextView) myInflate.findViewById(R.id.pointsText))
                    .setText(Integer.toString(pointCount));
            ((TextView) myInflate.findViewById(R.id.maxHeightText))
                    .setText(String.format("%.2f m", maxHeight));
            ((TextView) myInflate.findViewById(R.id.minHeightText))
                    .setText(String.format("%.2f m", minHeight));
        }
        return myInflate;
    }

    private void calculateRouteProperties(int id) {
        DatabaseManager baseLocal = new DatabaseManager(context);
        RouteListElement currentWorkout = baseLocal.getRouteInfo(id);
        if (currentWorkout.minHeight != -1 && currentWorkout.maxHeight != -1) {
            minHeight = currentWorkout.minHeight;
            maxHeight = currentWorkout.maxHeight;
            lastEndDate = currentWorkout.endTime;
            pointCount = currentWorkout.pointCount;
        } else {
            List<RouteElement> route = baseLocal.getPointsInRoute(id);
            for (RouteElement element : route) {
                lastEndDate = element.time;
                if (element.altitude > maxHeight)
                    maxHeight = element.altitude;
                if (element.altitude < maxHeight)
                    minHeight = element.altitude;
            }
            pointCount = route.size();
            baseLocal.updateRouteProperties(id, minHeight, maxHeight, lastEndDate, pointCount);
        }
    }

    private RouteListElement getRouteInfo(int id) {
        DatabaseManager localManager = new DatabaseManager(context);
        RouteListElement route = localManager.getRouteInfo(id);
        if (route != null)
            return route;
        return new RouteListElement(0, 0, 0, "");
    }
}

