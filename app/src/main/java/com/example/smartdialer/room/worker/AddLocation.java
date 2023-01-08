package com.example.smartdialer.room.worker;

import android.app.Activity;
import android.content.Context;

import androidx.room.Room;

import com.example.smartdialer.room.AppDatabase;
import com.example.smartdialer.room.dao.CallDao;
import com.example.smartdialer.room.dao.LocationDao;
import com.example.smartdialer.room.entity.Calls;
import com.example.smartdialer.room.entity.LocationEntity;

public class AddLocation extends Thread{
    Context context;
    LocationEntity locationEntity;
    public AddLocation(Context ctx, LocationEntity entity) {
        this.context = ctx;
        this.locationEntity = entity;
    }

    @Override
    public void run() {
        super.run();
        AppDatabase db = Room.databaseBuilder(context,
                AppDatabase.class, "temp.db").build();
        LocationDao locationDao = db.locationDao();
        locationDao.insertAll(locationEntity);
    }
}
