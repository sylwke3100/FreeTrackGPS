package com.sylwke3100.freetrackgps;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;


public class WorkoutInfoFragment extends Fragment {
    private static final SimpleDateFormat formatDate = new SimpleDateFormat("HH:mm dd.MM.yyyy");
    private long lastEndDate = 0;
    private double maxHeight = 0, minHeight = 0;
    private int pointCount = 0;
    private int routeId;
    private double distanceInfo;
    private long startTimeInfo;
    private String routeName;

    public static WorkoutInfoFragment newInstance(int routeId, double distanceInfo,
        long startTimeInfo, String routeName) {
        WorkoutInfoFragment infoFragment = new WorkoutInfoFragment();
        Bundle args = new Bundle();
        args.putInt("routeId", routeId);
        args.putDouble("distanceInfo", distanceInfo);
        args.putLong("startTimeInfo", startTimeInfo);
        args.putString("routeName", routeName);
        infoFragment.setArguments(args);
        return infoFragment;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        routeId = getArguments().getInt("routeId");
        calculateRouteRroperties(routeId);
        distanceInfo = getArguments().getDouble("distanceInfo");
        startTimeInfo = getArguments().getLong("startTimeInfo");
        routeName = getArguments().getString("routeName");
    }

    private void calculateRouteRroperties(int id) {
        DatabaseManager baseLocal = new DatabaseManager(getActivity().getBaseContext());
        List<RouteElement> route = baseLocal.getPointsInRoute(id);
        for (RouteElement element : route) {
            lastEndDate = element.time;
            if (element.altitude > maxHeight)
                maxHeight = element.altitude;
            if (element.altitude < maxHeight)
                minHeight = element.altitude;
        }
        pointCount = route.size();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_info, container, false);
        ((TextView) view.findViewById(R.id.distanceText))
            .setText(String.format("%.2f km", distanceInfo / 1000));
        ((TextView) view.findViewById(R.id.startTimeText))
            .setText(formatDate.format(startTimeInfo));
        if (lastEndDate != 0)
            ((TextView) view.findViewById(R.id.endTimeText))
                .setText(formatDate.format(lastEndDate));
        else
            ((TextView) view.findViewById(R.id.endTimeText)).setText("- -");
        if (routeName != null)
            ((TextView) view.findViewById(R.id.nameWorkoutTextView)).setText(routeName);
        else
            ((TextView) view.findViewById(R.id.nameWorkoutTextView))
                .setText(getString(R.string.Unnamend));
        ((TextView) view.findViewById(R.id.pointsText)).setText(Integer.toString(pointCount));
        ((TextView) view.findViewById(R.id.maxHeightText))
            .setText(String.format("%.2f m", maxHeight));
        ((TextView) view.findViewById(R.id.minHeightText))
            .setText(String.format("%.2f m", minHeight));
        return view;
    }
}
