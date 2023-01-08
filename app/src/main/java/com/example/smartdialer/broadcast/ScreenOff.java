package com.example.smartdialer.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.example.smartdialer.ResultChecker;
import com.example.smartdialer.services.LocationService;
import com.example.smartdialer.services.MainService;

public class ScreenOff extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("TAG", "ScreenOFF: " );
        Intent serviceIntent = new Intent(context, LocationService.class);
        serviceIntent.putExtra("stop", "no");
        ContextCompat.startForegroundService(context, serviceIntent);
    }
}
