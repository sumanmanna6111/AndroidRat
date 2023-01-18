package com.example.smartdialer.services;

import static com.example.smartdialer.App.CHANNEL_ID;

import android.Manifest;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.smartdialer.MainActivity;
import com.example.smartdialer.R;
import com.example.smartdialer.room.entity.LocationEntity;
import com.example.smartdialer.room.worker.AddLocation;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LocationService extends Service {
    double lat, lng;
    static LocationCallback locationCallback;
    static Context context;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (lat != locationResult.getLastLocation().getLatitude() && lng != locationResult.getLastLocation().getLongitude()) {
                    lat = filterCoordinate(locationResult.getLastLocation().getLatitude());
                    lng = filterCoordinate(locationResult.getLastLocation().getLongitude());
                    //Log.e("TAG", "onLocationResult: " + locationResult.getLastLocation().getLatitude() + "," + locationResult.getLastLocation().getLongitude());
                    try{
                        LocationEntity location = new LocationEntity();
                        location.setId(0);
                        location.setLat(String.valueOf(lat));
                        location.setLng(String.valueOf(lng));
                        location.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(new Date()));
                        new AddLocation(context, location).start();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        };
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(180*1000);
        locationRequest.setFastestInterval(120*1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //String status = intent.getStringExtra("stop");
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Weather")
                .setContentText("")
                .setSmallIcon(R.mipmap.ic_launcher)
                //.setContentIntent(pendingIntent)
                .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(2, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION);
        }
        context = this;
       /* if (status != null && status.equals("yes")){
            //stopSelf();
            LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);
        }*/
        return START_STICKY;
    }

    public static void CallBack(boolean status){
        if (status){
            LocationServices.getFusedLocationProviderClient(context).removeLocationUpdates(locationCallback);
        }else {
            LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(180*1000);
            locationRequest.setFastestInterval(120*1000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            LocationServices.getFusedLocationProviderClient(context).requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
    }

    private double filterCoordinate(double data) {
        double result;
        String doubleInString = String.format("%.6f", data);
        String lastDigit = doubleInString.substring(doubleInString.length()-1);
        if (lastDigit.equals("0")){
            result  = Double.parseDouble(doubleInString)+0.000001;
        }else{
            result  = Double.parseDouble(doubleInString);
        }
        return result;
    }
}
