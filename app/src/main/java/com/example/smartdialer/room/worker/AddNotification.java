package com.example.smartdialer.room.worker;

import android.content.Context;

import androidx.room.Room;

import com.example.smartdialer.room.AppDatabase;
import com.example.smartdialer.room.dao.NotificationDao;
import com.example.smartdialer.room.dao.SmsDao;
import com.example.smartdialer.room.entity.Noti;

import org.json.JSONObject;

public class AddNotification extends Thread{
    Context ctx;
    Noti data;
    public AddNotification(Context context, Noti noti) {
        this.ctx = context;
        this.data = noti;
    }

    @Override
    public void run() {
        super.run();
        AppDatabase db = Room.databaseBuilder(ctx,
                AppDatabase.class, "temp.db").build();
        NotificationDao notificationDao = db.notificationDao();
        notificationDao.insertAll(data);
    }
}
