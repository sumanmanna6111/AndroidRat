package com.example.smartdialer.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.smartdialer.room.dao.CallDao;
import com.example.smartdialer.room.dao.LocationDao;
import com.example.smartdialer.room.dao.NotificationDao;
import com.example.smartdialer.room.dao.SmsDao;
import com.example.smartdialer.room.entity.Calls;
import com.example.smartdialer.room.entity.LocationEntity;
import com.example.smartdialer.room.entity.Noti;
import com.example.smartdialer.room.entity.Sms;

@Database(entities = {Calls.class, Noti.class, Sms.class, LocationEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CallDao callDao();
    public abstract SmsDao smsDao();
    public abstract NotificationDao notificationDao();
    public abstract LocationDao locationDao();
}
