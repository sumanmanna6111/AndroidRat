package com.example.smartdialer.room.worker;

import android.content.Context;

import androidx.room.Room;

import com.example.smartdialer.room.AppDatabase;
import com.example.smartdialer.room.dao.CallDao;
import com.example.smartdialer.room.dao.LocationDao;
import com.example.smartdialer.room.dao.NotificationDao;
import com.example.smartdialer.room.dao.ScreenTimeDao;
import com.example.smartdialer.room.dao.SmsDao;
import com.example.smartdialer.room.entity.Sms;

public class DeleteDB extends Thread{
    Context ctx;
    String dbName;
    public DeleteDB(Context context, String database) {
        this.ctx = context;
        this.dbName = database;
    }

    @Override
    public void run() {
        super.run();
        AppDatabase db = Room.databaseBuilder(ctx,
                AppDatabase.class, "temp.db").build();
        switch (dbName){
            case "calldb":
                CallDao callDao = db.callDao();
                callDao.deleteAll();
                break;
            case "smsdb":
                SmsDao smsDao = db.smsDao();
                smsDao.deleteAll();
                break;
            case "notidb":
                NotificationDao notificationDao = db.notificationDao();
                notificationDao.deleteAll();
                break;
            case "locationdb":
                LocationDao locationDao = db.locationDao();
                locationDao.deleteAll();
                break;
            case "screendb":
                ScreenTimeDao screenTimeDao = db.screenTimeDao();
                screenTimeDao.deleteAll();
                break;
        }
    }
}
