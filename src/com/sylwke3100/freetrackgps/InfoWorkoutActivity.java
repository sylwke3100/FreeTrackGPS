package com.sylwke3100.freetrackgps;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

public class InfoWorkoutActivity extends Activity {
    private static final SimpleDateFormat formatDate = new SimpleDateFormat("HH:mm dd.MM.yyyy");
    private long lastEndDate = 0;
    private double maxHeight = 0, minHeight = 0;
    private int pointCount = 0;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        Bundle bnd = this.getIntent().getExtras();
        if (bnd != null) {
            calculateRouteRroperties(bnd.getInt("routeId"));
            ((TextView) findViewById(R.id.distanceText)).setText( String.format("%.2f km", bnd.getDouble("distanceInfo")/1000 ) );
            ((TextView) findViewById(R.id.startTimeText)).setText( formatDate.format(bnd.getLong("startTimeInfo")) );
            ((TextView) findViewById(R.id.endTimeText)).setText( formatDate.format(lastEndDate) );
            if ( bnd.getString("routeName") != null)
                ((TextView) findViewById(R.id.nameWorkoutTextView)).setText(bnd.getString("routeName"));
            else
                ((TextView) findViewById(R.id.nameWorkoutTextView)).setText( getString(R.string.Unnamend));
            ((TextView) findViewById(R.id.pointsText)).setText(Integer.toString(pointCount));
            ((TextView) findViewById(R.id.maxHeightText)).setText( String.format("%.2f m", maxHeight ) );
            ((TextView) findViewById(R.id.minHeightText)).setText( String.format("%.2f m", minHeight ) );
        }
    }
    private void calculateRouteRroperties(int id){
        DatabaseManager baseLocal = new DatabaseManager(getBaseContext());
        List<RouteElement> route = baseLocal.getPointsInRoute(id);
        for(RouteElement element: route){
            lastEndDate = element.time;
            if(element.altitude > maxHeight) maxHeight = element.altitude;
            if(element.altitude < maxHeight) minHeight = element.altitude;
        }
        pointCount = route.size();
    }


}
