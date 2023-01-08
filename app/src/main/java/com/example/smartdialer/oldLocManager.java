package com.example.smartdialer;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;

public class oldLocManager implements LocationUpdate{
    long lastLocationUpdate = 0L;
    double lat, lng;

    //TODO -- This work only when activity are running
    @Override
    public void onLocationChanged(Location location) {
        Log.e("TAG", "onLocationChanged: " + location.getLatitude() + location.getLongitude());
        if (lat != location.getLatitude() && lng != location.getLongitude()) {
            lat = location.getLatitude();
            lng = location.getLongitude();
            long lastUpdate = lastLocationUpdate - System.currentTimeMillis();
            if (lastUpdate > 120000) {
                lastLocationUpdate = System.currentTimeMillis();
                Log.e("TAG", "onLocationChanged: " + location.getLatitude() + location.getLongitude());
            }
        }
    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onGpsStatusChanged(int event) {

    }
}
