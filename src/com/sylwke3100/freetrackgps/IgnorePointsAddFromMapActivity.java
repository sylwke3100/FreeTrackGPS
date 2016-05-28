package com.sylwke3100.freetrackgps;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import org.osmdroid.bonuspack.overlays.MapEventsOverlay;
import org.osmdroid.bonuspack.overlays.MapEventsReceiver;
import org.osmdroid.bonuspack.overlays.Polygon;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.List;

public class IgnorePointsAddFromMapActivity extends Activity {
    private MapView mMapView;
    private LocationSharing sharedLocation;
    private GeoPoint ignorePoint;
    private IgnorePointsManager ignorePointsManager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedLocation = new LocationSharing(this);
        setContentView(R.layout.activity_ignorepoints_add);
        mMapView = (MapView) findViewById(R.id.ignorePointsMap);
        mMapView.setTileSource(TileSourceFactory.MAPNIK);
        mMapView.setBuiltInZoomControls(true);
        mMapView.setMultiTouchControls(true);
        mMapView.setTilesScaledToDpi(true);
        mMapView.getController().setZoom(1);
        MapEventsOverlay eventsOverlay =
                new MapEventsOverlay(mMapView.getContext(), new MapEventsReceiver() {
                    public boolean singleTapConfirmedHelper(GeoPoint geoPoint) {
                        if (mMapView.getOverlays().size() > 2)
                            mMapView.getOverlays().remove(2);
                        if (mMapView.getOverlays().size() > 1)
                            mMapView.getOverlays().remove(1);
                        mMapView.getOverlays().add(addCircleWithLimitDistance(geoPoint));
                        mMapView.getOverlays()
                                .add(createOverlayFromGeoPoint(mMapView.getContext(), geoPoint));
                        mMapView.invalidate();
                        return false;
                    }

                    public boolean longPressHelper(GeoPoint geoPoint) {
                        return false;
                    }
                });
        mMapView.getOverlays().add(0, eventsOverlay);
        ignorePointsManager = new IgnorePointsManager(getApplicationContext());
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_ignorepoints_add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_ignorepoints_add_centermap:
                LocationSharing.LocationSharingResult location =
                        sharedLocation.getCurrentLocation();
                mMapView.getController()
                        .setCenter(new GeoPoint(location.latitude, location.longitude));
                mMapView.getController().setZoom(DefaultValues.defaultZoomLevel);
                mMapView.invalidate();
                break;
            case R.id.action_ignorepoints_add_done:
                if (ignorePoint != null) {
                    onSetIgnorePointsNameAlert();
                }
                break;
        }
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        if (ignorePoint == null)
            menu.findItem(R.id.action_ignorepoints_add_done).setEnabled(false);
        else
            menu.findItem(R.id.action_ignorepoints_add_done).setEnabled(true);
        if (sharedLocation.getCurrentLocation().status == 1)
            menu.findItem(R.id.action_ignorepoints_add_centermap).setEnabled(true);
        else
            menu.findItem(R.id.action_ignorepoints_add_centermap).setEnabled(false);
        return super.onPrepareOptionsMenu(menu);
    }

    private Polygon addCircleWithLimitDistance(GeoPoint point) {
        Polygon polygon = new Polygon(this);
        final double distance = 100;
        ArrayList<GeoPoint> circlePoints = new ArrayList<GeoPoint>();
        for (float degrees = 0; degrees < 360; degrees += 1) {
            circlePoints.add(new GeoPoint(point.getLatitude(), point.getLongitude()).destinationPoint(distance, degrees));
        }
        polygon.setPoints(circlePoints);
        return polygon;
    }


    private ItemizedIconOverlay<OverlayItem> createOverlayFromGeoPoint(Context mapContext,
                                                                       GeoPoint point) {
        OverlayItem overlayItem =
                new OverlayItem("Ignored Point", "This point has been ignored", point);
        ignorePoint = point;
        final List<OverlayItem> items = new ArrayList<OverlayItem>();
        items.add(overlayItem);
        return new ItemizedIconOverlay<OverlayItem>(mapContext, items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    public boolean onItemSingleTapUp(int i, OverlayItem overlayItem) {
                        return false;
                    }

                    public boolean onItemLongPress(int i, OverlayItem overlayItem) {
                        return false;
                    }
                });
    }

    public void onAddIgnorePointsFromLocation(String ignorePointName) {
        if (ignorePoint != null) {
            if (!ignorePointsManager
                    .addIgnorePoint(ignorePoint.getLatitude(), ignorePoint.getLongitude(),
                            ignorePointName))
                Toast.makeText(getBaseContext(), R.string.ignorePointsExists, Toast.LENGTH_LONG)
                        .show();
        }
    }

    public void onSetIgnorePointsNameAlert() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate
                (R.layout.prompt_ignore_points_from_location, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptView);
        final EditText inputName = (EditText) promptView.findViewById(R.id.nameEdit);
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton(R.string.okLabel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        onAddIgnorePointsFromLocation(inputName.getText().toString());
                        finish();
                    }
                }).setNegativeButton(R.string.cancelLabel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
                finish();
            }
        });
        AlertDialog alertD = alertDialogBuilder.create();
        alertD.show();
    }

}
