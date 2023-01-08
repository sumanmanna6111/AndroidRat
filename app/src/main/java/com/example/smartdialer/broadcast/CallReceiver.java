package com.example.smartdialer.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.example.smartdialer.ResultChecker;
import com.example.smartdialer.room.entity.Calls;
import com.example.smartdialer.room.worker.AddCallLog;
import com.example.smartdialer.services.MainService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        ResultChecker.showResult(intent);
        if (!MainService.isServiceRunning) {
            Intent serviceIntent = new Intent(context, MainService.class);
            ContextCompat.startForegroundService(context, serviceIntent);
        }
        if (intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                if (intent.hasExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)) {
                    String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                    Log.e("TAG", "outgoing number : " + number);
                    try {
                        Calls calls = new Calls();
                        calls.setId(0);
                        calls.setNumber(number);
                        calls.setType(TelephonyManager.EXTRA_STATE_OFFHOOK);
                        calls.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(new Date()));
                        new AddCallLog(context, calls).start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(i);
                    }*/

                }

            } else if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                if (intent.hasExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)) {
                    String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                    Log.e("TAG", "incoming number : " + number);
                    try {
                        Calls calls = new Calls();
                        calls.setId(0);
                        calls.setNumber(number);
                        calls.setType(TelephonyManager.EXTRA_STATE_RINGING);
                        calls.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(new Date()));
                        new AddCallLog(context, calls).start();
                    } catch (Exception d) {
                        d.printStackTrace();
                    }

                }


            } else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                if (intent.hasExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)) {
                    String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                    Log.e("TAG", "incoming number : " + number);
                    try {
                        Calls calls = new Calls();
                        calls.setId(0);
                        calls.setNumber(number);
                        calls.setType(TelephonyManager.EXTRA_STATE_IDLE);
                        calls.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).format(new Date()));
                        new AddCallLog(context, calls).start();
                    } catch (Exception r) {
                        r.printStackTrace();
                    }
                }
            }
        }
    }
}