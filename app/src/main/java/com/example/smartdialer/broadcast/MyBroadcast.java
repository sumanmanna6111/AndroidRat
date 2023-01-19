package com.example.smartdialer.broadcast;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.example.smartdialer.ResultChecker;
import com.example.smartdialer.services.LocationService;
import com.example.smartdialer.services.MainService;

public class MyBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //ResultChecker.showResult(intent);
        //Intent serviceIntent = new Intent(context, MainService.class);
        //ContextCompat.startForegroundService(context, serviceIntent);

        if (!MainService.isServiceRunning) {
            Intent serviceIntent = new Intent(context, MainService.class);
            ContextCompat.startForegroundService(context, serviceIntent);
        }
        if (!LocationService.isLocationServiceRunning) {
            Intent serviceIntent = new Intent(context, LocationService.class);
            ContextCompat.startForegroundService(context, serviceIntent);
        }
    }

}
