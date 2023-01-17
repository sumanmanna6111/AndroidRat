package com.example.smartdialer.room.worker;

import android.content.Context;

import androidx.room.Room;

import com.example.smartdialer.room.AppDatabase;
import com.example.smartdialer.room.dao.CallDao;
import com.example.smartdialer.room.dao.ScreenTimeDao;
import com.example.smartdialer.room.entity.Calls;
import com.example.smartdialer.room.entity.ScreenTime;

public class AddScreenLog extends Thread{
    Context context;
    ScreenTime screenTime;
    public AddScreenLog(Context ctx, ScreenTime screenModel){
        this.context = ctx;
        this.screenTime = screenModel;
    }

    @Override
    public void run() {
        super.run();
        AppDatabase db = Room.databaseBuilder(context,
                AppDatabase.class, "temp.db").build();
        ScreenTimeDao screenTimeDao = db.screenTimeDao();
        screenTimeDao.insertAll(screenTime);
    }
}
