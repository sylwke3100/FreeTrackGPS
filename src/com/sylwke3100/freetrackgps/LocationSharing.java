package com.sylwke3100.freetrackgps;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class LocationSharing {
    private SharedPreferences sharedPrefs;


    public LocationSharing(Context activityContext) {
        sharedPrefs = activityContext.getSharedPreferences("Pref", Activity.MODE_PRIVATE);
    }

    public void clearCurrentLocation() {
        SharedPreferences.Editor preferencesEditor = sharedPrefs.edit();
        preferencesEditor.putInt("currentLocationStatus", 0);
        preferencesEditor.putLong("currentLocationLat", 0);
        preferencesEditor.putLong("currentLocationLon", 0);
        preferencesEditor.commit();
    }

    public LocationSharingResult getCurrentLocation() {
        LocationSharingResult result = new LocationSharingResult();
        result.status = sharedPrefs.getInt("currentLocationStatus", 0);
        result.latitude = Double.longBitsToDouble(sharedPrefs.getLong("currentLocationLat", 0));
        result.longitude = Double.longBitsToDouble(sharedPrefs.getLong("currentLocationLon", 0));
        return result;
    }

    public void setCurrentLocation(Double latitude, Double longitude) {
        SharedPreferences.Editor preferencesEditor = sharedPrefs.edit();
        preferencesEditor.putInt("currentLocationStatus", 1);
        preferencesEditor.putLong("currentLocationLat", Double.doubleToRawLongBits(latitude));
        preferencesEditor.putLong("currentLocationLon", Double.doubleToRawLongBits(longitude));
        preferencesEditor.commit();
    }


    public class LocationSharingResult {
        public int status = 0;
        public double latitude;
        public double longitude;
    }

}
