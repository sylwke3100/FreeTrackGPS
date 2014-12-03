package com.example.freetrackgps;

import android.location.*;
import android.widget.TextView;

import java.util.List;

import android.os.Bundle;


public class GPSListner implements LocationListener  {
	private TextView T, E, C, U;
	private RouteManager localRoute;
	public GPSListner(List<TextView> E, RouteManager Rt){
		this.T = E.get(0);
		this.E = E.get(1);
		this.C = E.get(2);
		this.U = E.get(3);
		localRoute = Rt;
	}
    public void onLocationChanged(Location location) {
        String message = String.format( " %1$s %2$s %3$s",String.format( "%.2f", location.getLongitude()), String.format( "%.2f",location.getLatitude()), String.format( "%.2f",location.getAltitude()));
        if (localRoute != null && location != null && location.hasAltitude() == true ){
        	localRoute.addPoint(location);
        	this.U.setText( String.format( "%.2f",localRoute.getDistance()) + " m" );
        }
        this.T.setText(message);
    }
    public void onStatusChanged(String s, int i, Bundle b) {
    	switch(i){
    	case LocationProvider.AVAILABLE:
    		this.E.setText("On");
    		if (localRoute.getStatus() == 1)
    			localRoute.unpause();
    		break;
    	case LocationProvider.OUT_OF_SERVICE:
    		this.E.setText("Off");
    		if (localRoute.getStatus() == 2)
    			localRoute.pause();
    		break;
    	case LocationProvider.TEMPORARILY_UNAVAILABLE:
    		this.E.setText("Off");
    		if (localRoute.getStatus() == 2)
    			localRoute.pause();
    		break;
    	}
    }
    public void onProviderDisabled(String s) {
    	this.E.setText("Off");
    	if (localRoute.getStatus() == 2)
    		localRoute.pause();
    }
    public void onProviderEnabled(String s) {
    	this.E.setText("On");
    	if (localRoute.getStatus() == 1)
    		localRoute.unpause();
    }		
}
    
    
    

