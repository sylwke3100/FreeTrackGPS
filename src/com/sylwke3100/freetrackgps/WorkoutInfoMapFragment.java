package com.sylwke3100.freetrackgps;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.PathOverlay;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class WorkoutInfoMapFragment extends Fragment {
    private static final SimpleDateFormat formatDate = new SimpleDateFormat("HH:mm dd.MM.yyyy");
    private MapView mMapView;
    private DatabaseManager workoutDatabase;
    private GeoPoint centerRoutePoint = new GeoPoint(0, 0);
    private Context globalContext;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        workoutDatabase = new DatabaseManager(inflater.getContext());
        Bundle localBundle = getArguments();
        globalContext = inflater.getContext();
        mMapView = new MapView(globalContext, 256);
        mMapView.setTileSource(TileSourceFactory.MAPNIK);
        mMapView.setBuiltInZoomControls(true);
        mMapView.setMultiTouchControls(true);
        IMapController mapController = mMapView.getController();
        mMapView.getOverlays().add(getRoutePath(localBundle.getInt("routeId")));
        mMapView.getOverlays().add(getStartEndMarkers(localBundle.getInt("routeId")));
        mapController.setZoom(13);
        mapController.setCenter(centerRoutePoint);
        mMapView.invalidate();
        return mMapView;
    }

    private PathOverlay getRoutePath(Integer routeId) {
        List<RouteElement> pointsList = workoutDatabase.getPointsInRoute(routeId);
        int pointCounter = 0;
        PathOverlay routeMapPath = new PathOverlay(Color.BLUE, globalContext);
        for (RouteElement routePoint : pointsList) {
            pointCounter++;
            if (pointCounter == (pointsList.size() / 2))
                centerRoutePoint = new GeoPoint(routePoint.latitude, routePoint.longitude);
            GeoPoint point = new GeoPoint(routePoint.latitude, routePoint.longitude);
            routeMapPath.addPoint(point);
        }
        return routeMapPath;
    }

    private ItemizedIconOverlay<OverlayItem> getStartEndMarkers(Integer routeId) {
        List<RouteElement> pointsList = workoutDatabase.getPointsInRoute(routeId);
        final List<OverlayItem> routeMarkersArray = new ArrayList<OverlayItem>();
        if (pointsList.size() >= 2) {
            RouteElement startPoint = pointsList.get(0);
            RouteElement endPoint = pointsList.get(pointsList.size() - 1);
            Resources res = getResources();
            OverlayItem startPointMarker =
                new OverlayItem(globalContext.getString(R.string.startPointLabel),
                    formatDate.format(startPoint.time),
                    new GeoPoint(startPoint.latitude, startPoint.longitude));
            OverlayItem endPointMarker =
                new OverlayItem(globalContext.getString(R.string.endPointLabel),
                    formatDate.format(endPoint.time),
                    new GeoPoint(endPoint.latitude, endPoint.longitude));
            startPointMarker.setMarker(res.getDrawable(R.drawable.startpointpcon));
            endPointMarker.setMarker(res.getDrawable(R.drawable.endpointicon));
            routeMarkersArray.add(startPointMarker);
            routeMarkersArray.add(endPointMarker);
        }
        return new ItemizedIconOverlay<OverlayItem>(globalContext, routeMarkersArray,
            new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                @Override public boolean onItemSingleTapUp(int index, OverlayItem item) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(globalContext);
                    dialog.setTitle(item.getTitle());
                    dialog.setMessage(item.getSnippet());
                    if (index == 0)
                        dialog.setIcon(R.drawable.startpointpcon);
                    else
                        dialog.setIcon(R.drawable.endpointicon);
                    dialog.show();
                    return true;
                }

                @Override public boolean onItemLongPress(int index, OverlayItem item) {
                    return true;
                }
            });
    }

}
