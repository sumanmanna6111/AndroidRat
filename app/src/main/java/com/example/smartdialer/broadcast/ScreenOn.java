package com.example.smartdialer.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.example.smartdialer.services.LocationService;

public class ScreenOn extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("TAG", "ScreenOn: " );
        /*Intent serviceIntent = new Intent(context, LocationService.class);
        serviceIntent.putExtra("stop", "yes");
        ContextCompat.startForegroundService(context, serviceIntent);*/
    }
}
