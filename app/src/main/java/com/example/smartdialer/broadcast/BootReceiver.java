package com.example.smartdialer.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import androidx.core.content.ContextCompat;

import com.example.smartdialer.services.MainService;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, MainService.class);
        ContextCompat.startForegroundService(context, serviceIntent);
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.e("TAG", "Boot Completed: " );

            CallReceiver callReceiver = new CallReceiver();
            IntentFilter filter = new IntentFilter("android.intent.action.PHONE_STATE");
            filter.setPriority(Integer.MAX_VALUE);
            context.registerReceiver(callReceiver, filter);
            SmsReceiver smsReceiver = new SmsReceiver();
            IntentFilter filter2 = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
            filter.setPriority(Integer.MAX_VALUE);
            context.registerReceiver(smsReceiver, filter2);
        }
    }
}
