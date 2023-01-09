package com.example.smartdialer.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.smartdialer.services.LocationService;


public class ScreenOff extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //Log.e("TAG", "ScreenOFF: " );
        /*Intent serviceIntent = new Intent(context, LocationService.class);
        serviceIntent.putExtra("stop", "no");
        ContextCompat.startForegroundService(context, serviceIntent);*/
        LocationService.CallBack(false);
    }
}
