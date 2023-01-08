package com.example.smartdialer.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BatteryChanged extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())){

        }

        if (Intent.ACTION_USER_UNLOCKED.equals(intent.getAction())){

        }
    }
}
