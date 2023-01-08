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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NotificationListener extends NotificationListenerService {
    String checkTitle = "";
    String checkContent = "";
    String content = "";
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
            content = "";
            if(contentCs != null) content = contentCs.toString();
            long postTime = sbn.getPostTime();
            String uniqueKey = sbn.getKey();

            /*JSONObject data = new JSONObject();
            data.put("appName", appName);
            data.put("title", title);
            data.put("content",  ""+content);
            data.put("postTime", postTime);
            data.put("key", uniqueKey);*/
            if (checkTitle.equals(title) && checkContent.equals(content)){
                Log.e("TAG", "Same notification: " );
            }else {
                checkTitle = title;
                checkContent = content;
                Noti noti = new Noti();
                noti.setAppName(appName);
                noti.setTitle(title);
                noti.setContent(content);
                String postDateTime = new SimpleDateFormat("dd MMM yyyy hh:mm a", Locale.ENGLISH).format(new Date(postTime));
                noti.setPostTime(postDateTime);
                noti.setKey(uniqueKey);
                new AddNotification(this, noti).start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
