package com.sylwke3100.freetrackgps;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.TextView;
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

public class WorkoutMapViewActivity extends Activity {
    private static final SimpleDateFormat formatDate = new SimpleDateFormat("HH:mm dd.MM.yyyy");
    private MapView mMapView;
    private DatabaseManager workoutDatabase;
    private SharedPreferences sharePrefs;
    private long currentWorkoutId = -1;

    public void onCreate(Bundle savedInstanceState) {
        sharePrefs = getSharedPreferences(DefaultValues.prefs, Activity.MODE_PRIVATE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_mapview);
        IntentFilter filter = new IntentFilter();
        filter.addAction(MainActivity.MAINACTIVITY_ACTION);
        android.os.Handler updateView = new android.os.Handler(new android.os.Handler.Callback() {
            public boolean handleMessage(Message message) {
                currentWorkoutId = sharePrefs.getLong("currentWorkoutId", -1);
                ((TextView) findViewById(R.id.distanceValue)).setText(String.format("%.2f km", message.getData().getDouble("dist", 0)));
                if (message.getData().getInt("updateCounter", 0) == 3) {
                    onUpdateMap();
                    onReDrawMap();
                }
                return true;
            }
        });
        registerReceiver(new WorkoutMapViewReciver(getBaseContext(), updateView), filter);
        GPSRunnerServiceMessageController controller = new GPSRunnerServiceMessageController(this);
        controller.sendMessageToService(GPSRunnerService.SERVICE_ACTION.WORKOUT_ID);
        workoutDatabase = new DatabaseManager(this);
        mMapView = (MapView) findViewById(R.id.currentmap);
        currentWorkoutId = sharePrefs.getLong("currentWorkoutId", -1);
        mMapView.setTileSource(TileSourceFactory.MAPNIK);
        mMapView.setBuiltInZoomControls(true);
        mMapView.setMultiTouchControls(true);
        mMapView.getOverlays().add(getRoutePath());
        mMapView.getOverlays().add(getStartEndMarkers(this));
    }

    public void onUpdateMap() {
        mMapView.getOverlays().clear();
        mMapView.getOverlays().add(getRoutePath());
        mMapView.getOverlays().add(getStartEndMarkers(this));
    }

    public void onReDrawMap() {
        mMapView.setTilesScaledToDpi(true);
        if (mMapView.getScreenRect(null).height() > 0)
            mMapView.zoomToBoundingBox(getLimitedAreaPath());
        else {
            ViewTreeObserver observer = mMapView.getViewTreeObserver();
            observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                public void onGlobalLayout() {
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
    }

    public void onResume() {
        currentWorkoutId = sharePrefs.getLong("currentWorkoutId", -1);
        onReDrawMap();
        super.onResume();
    }

    public void onBackPressed() {
        finish();
    }

    private PathOverlay getRoutePath() {
        Log.d("GRStatus", Long.toString(currentWorkoutId));
        List<RouteElement> pointsList = workoutDatabase.getPointsInRoute(currentWorkoutId);
        PathOverlay routeMapPath = new PathOverlay(Color.BLUE, this);
        for (RouteElement routePoint : pointsList) {
            GeoPoint point = new GeoPoint(routePoint.latitude, routePoint.longitude);
            routeMapPath.addPoint(point);
        }
        return routeMapPath;
    }

    private ItemizedIconOverlay<OverlayItem> getStartEndMarkers(final Context inflanterContext) {
        List<RouteElement> pointsList = workoutDatabase.getPointsInRoute(currentWorkoutId);
        final List<OverlayItem> routeMarkersArray = new ArrayList<OverlayItem>();
        if (pointsList.size() >= 2) {
            RouteElement startPoint = pointsList.get(0);
            RouteElement endPoint = pointsList.get(pointsList.size() - 1);
            Resources res = getResources();
            OverlayItem startPointMarker =
                    new OverlayItem(this.getString(R.string.startPointLabel),
                            formatDate.format(startPoint.time),
                            new GeoPoint(startPoint.latitude, startPoint.longitude));
            OverlayItem endPointMarker =
                    new OverlayItem(this.getString(R.string.endPointLabel),
                            formatDate.format(endPoint.time),
                            new GeoPoint(endPoint.latitude, endPoint.longitude));
            startPointMarker.setMarker(res.getDrawable(R.drawable.startpointpcon));
            endPointMarker.setMarker(res.getDrawable(R.drawable.endpointicon));
            routeMarkersArray.add(startPointMarker);
            routeMarkersArray.add(endPointMarker);
        }
        return new ItemizedIconOverlay<OverlayItem>(this, routeMarkersArray,
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
        List<RouteElement> pointsList = workoutDatabase.getPointsInRoute(currentWorkoutId);
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
