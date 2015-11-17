package com.sylwke3100.freetrackgps;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import org.osmdroid.bonuspack.overlays.MapEventsOverlay;
import org.osmdroid.bonuspack.overlays.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.List;

public class IgnorePointsAddActivity extends Activity {
    private MapView mMapView;
    private LocationSharing sharedLocation;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedLocation = new LocationSharing(this);
        setContentView(R.layout.activity_ignorepoints_add);
        mMapView = (MapView) findViewById(R.id.ignorePointsMap);
        mMapView.setTileSource(TileSourceFactory.MAPNIK);
        mMapView.setBuiltInZoomControls(true);
        mMapView.setMultiTouchControls(true);
        mMapView.setTilesScaledToDpi(true);
        MapEventsOverlay eventsOverlay =
            new MapEventsOverlay(mMapView.getContext(), new MapEventsReceiver() {
                public boolean singleTapConfirmedHelper(GeoPoint geoPoint) {
                    if (mMapView.getOverlays().size() > 1)
                        mMapView.getOverlays().remove(1);
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
                mMapView.invalidate();
                break;
        }
        return true;
    }

    private ItemizedIconOverlay<OverlayItem> createOverlayFromGeoPoint(Context mapContext,
        GeoPoint point) {
        OverlayItem overlayItem =
            new OverlayItem("Ignored Point", "This point has been ignored", point);
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
}
