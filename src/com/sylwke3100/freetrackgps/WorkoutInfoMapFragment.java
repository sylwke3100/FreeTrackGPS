package com.sylwke3100.freetrackgps;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.PathOverlay;

import java.util.List;

public class WorkoutInfoMapFragment extends Fragment {
    private MapView mMapView;
    private DatabaseManager workoutDatabase;
    private GeoPoint centerRoutePoint = new GeoPoint(0, 0);

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        workoutDatabase = new DatabaseManager(inflater.getContext());
        Bundle localBundle = getArguments();
        mMapView = new MapView(inflater.getContext(), 256);
        mMapView.setTileSource(TileSourceFactory.MAPNIK);
        mMapView.setBuiltInZoomControls(true);
        mMapView.setMultiTouchControls(true);
        IMapController mapController = mMapView.getController();
        mMapView.getOverlays()
            .add(getRoutePath(localBundle.getInt("routeId"), inflater.getContext()));
        mapController.setZoom(13);
        mapController.setCenter(centerRoutePoint);
        return mMapView;
    }

    private PathOverlay getRoutePath(Integer routeId, Context localContext) {
        List<RouteElement> pointsList = workoutDatabase.getPointsInRoute(routeId);
        int pointCounter = 0;
        PathOverlay routeMapPath = new PathOverlay(Color.BLUE, localContext);
        for (RouteElement routePoint : pointsList) {
            pointCounter++;
            if (pointCounter == (pointsList.size() / 2))
                centerRoutePoint = new GeoPoint(routePoint.latitude, routePoint.longitude);
            GeoPoint point = new GeoPoint(routePoint.latitude, routePoint.longitude);
            routeMapPath.addPoint(point);
        }
        return routeMapPath;
    }
}
