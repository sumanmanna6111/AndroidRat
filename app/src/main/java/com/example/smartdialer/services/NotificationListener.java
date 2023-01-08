package com.example.smartdialer.services;

import android.app.Notification;
import android.content.Intent;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.example.smartdialer.room.entity.Noti;
import com.example.smartdialer.room.worker.AddNotification;

import org.json.JSONException;
import org.json.JSONObject;

public class NotificationListener extends NotificationListenerService {


    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn){
        try {
            String appName = sbn.getPackageName();
            String title = sbn.getNotification().extras.getString(Notification.EXTRA_TITLE);//SpannableString cannot be cast to java.lang.String
            CharSequence contentCs = sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TEXT);
            String content = "";
            if(contentCs != null) content = contentCs.toString();
            long postTime = sbn.getPostTime();
            String uniqueKey = sbn.getKey();

            /*JSONObject data = new JSONObject();
            data.put("appName", appName);
            data.put("title", title);
            data.put("content",  ""+content);
            data.put("postTime", postTime);
            data.put("key", uniqueKey);*/

            Noti noti = new Noti();
            noti.setAppName(appName);
            noti.setTitle(title);
            noti.setContent(""+content);
            noti.setPostTime(postTime);
            noti.setKey(uniqueKey);
            new AddNotification(this, noti).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
