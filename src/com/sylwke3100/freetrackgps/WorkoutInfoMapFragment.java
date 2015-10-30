package com.sylwke3100.freetrackgps;



import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.ResourceProxyImpl;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.PathOverlay;

import java.util.List;

public class WorkoutInfoMapFragment extends Fragment {
    private MapView mMapView;
    private ResourceProxyImpl mResourceProxy;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        DatabaseManager workoutDatabase = new DatabaseManager(inflater.getContext());
        Bundle localBundle = getArguments();
        mResourceProxy = new ResourceProxyImpl(inflater.getContext().getApplicationContext());
        mMapView = new MapView(inflater.getContext(), 256);
        List<RouteElement> pointsList =
            workoutDatabase.getPointsInRoute(localBundle.getInt("routeId"));
        GeoPoint startPoint = new GeoPoint(0, 0);
        int counter = 0;
        PathOverlay routeMapPath = new PathOverlay(Color.RED, inflater.getContext());
        for (RouteElement routePoint : pointsList) {
            counter++;
            if (counter == (pointsList.size() / 2))
                startPoint = new GeoPoint(routePoint.latitude, routePoint.longitude);
            GeoPoint point = new GeoPoint(routePoint.latitude, routePoint.longitude);
            routeMapPath.addPoint(point);
        }
        mMapView.getOverlays().add(routeMapPath);
        mMapView.setTileSource(TileSourceFactory.MAPNIK);
        mMapView.setBuiltInZoomControls(true);
        mMapView.setMultiTouchControls(true);
        IMapController mapController = mMapView.getController();
        mapController.setZoom(10);
        mapController.setCenter(startPoint);
        return mMapView;
    }
}
