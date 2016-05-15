package com.sylwke3100.freetrackgps;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBoxE6;
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
    private WorkoutDatabaseController workoutController;
    private Context globalContext;
    private List<RouteElement> pointsList;

    public void onCreate(Bundle savedInstanceState) {
        globalContext = getActivity().getBaseContext();
        workoutController = new WorkoutDatabaseController(globalContext);
        Bundle localBundle = getArguments();
        Integer routeId = localBundle.getInt("routeId");
        pointsList = workoutController.getPoints(routeId);
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        mMapView = new MapView(globalContext, 256);
        mMapView.setTileSource(TileSourceFactory.MAPNIK);
        mMapView.setBuiltInZoomControls(true);
        mMapView.setMultiTouchControls(true);
        mMapView.getOverlays().add(getRoutePath());
        mMapView.getOverlays().add(getStartEndMarkers(inflater.getContext()));
        return mMapView;
    }

    public void onResume() {
        mMapView.setTilesScaledToDpi(true);
        if (mMapView.getScreenRect(null).height() > 0)
            mMapView.zoomToBoundingBox(getLimitedAreaPath());
        else {
            ViewTreeObserver observer = mMapView.getViewTreeObserver();
            observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override public void onGlobalLayout() {
                    mMapView.zoomToBoundingBox(getLimitedAreaPath());
                    if (mMapView.getZoomLevel() <= 12)
                        mMapView.getController().setZoom(mMapView.getZoomLevel() + 4);
                    ViewTreeObserver copyLayoutObserver = mMapView.getViewTreeObserver();
                    if (Build.VERSION.SDK_INT < 16)
                        copyLayoutObserver.removeGlobalOnLayoutListener(this);
                    else
                        copyLayoutObserver.removeOnGlobalLayoutListener(this);
                }
            });
        }
        super.onResume();
    }

    private PathOverlay getRoutePath() {
        PathOverlay routeMapPath = new PathOverlay(Color.BLUE, globalContext);
        for (RouteElement routePoint : pointsList) {
            GeoPoint point = new GeoPoint(routePoint.latitude, routePoint.longitude);
            routeMapPath.addPoint(point);
        }
        return routeMapPath;
    }

    private ItemizedIconOverlay<OverlayItem> getStartEndMarkers(final Context inflanterContext) {
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
                 public boolean onItemSingleTapUp(int index, OverlayItem item) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(inflanterContext);
                    dialog.setTitle(item.getTitle());
                    dialog.setMessage(item.getSnippet());
                    if (index == 0)
                        dialog.setIcon(R.drawable.startpointpcon);
                    else
                        dialog.setIcon(R.drawable.endpointicon);
                    dialog.show();
                    return true;
                }

                 public boolean onItemLongPress(int index, OverlayItem item) {
                    return true;
                }
            });
    }

    private BoundingBoxE6 getLimitedAreaPath() {
        double minLatitude = Double.MAX_VALUE, maxLatitude = Double.MIN_VALUE, minLongitude =
            Double.MAX_VALUE, maxLongitude = Double.MIN_VALUE;
        if (pointsList.size() > 0) {
            minLatitude = pointsList.get(0).latitude;
            minLongitude = pointsList.get(0).longitude;
        }
        for (RouteElement point : pointsList) {
            if (point.latitude > maxLatitude)
                maxLatitude = point.latitude;
            if (point.latitude < minLatitude)
                minLatitude = point.latitude;
            if (point.longitude > maxLongitude)
                maxLongitude = point.longitude;
            if (point.longitude < minLongitude)
                minLongitude = point.longitude;
        }
        return new BoundingBoxE6(minLatitude, maxLongitude, maxLatitude, minLongitude);
    }

}
