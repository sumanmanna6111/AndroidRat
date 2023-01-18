package com.example.smartdialer.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.smartdialer.room.entity.Calls;
import com.example.smartdialer.room.entity.ScreenTime;
import com.example.smartdialer.room.worker.AddScreenLog;
import com.example.smartdialer.room.worker.AddSmsLog;
import com.example.smartdialer.services.LocationService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ScreenOn extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("TAG", "ScreenOn: " );
        /*Intent serviceIntent = new Intent(context, LocationService.class);
        serviceIntent.putExtra("stop", "yes");
        ContextCompat.startForegroundService(context, serviceIntent);*/
        LocationService.CallBack(true);

        try {
            ScreenTime screenTime = new ScreenTime();
            screenTime.setId(0);
            screenTime.setStatus("ON");
            screenTime.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(new Date()));
            new AddScreenLog(context, screenTime).start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
