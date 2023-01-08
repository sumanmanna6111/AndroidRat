package com.example.smartdialer.room.worker;

import android.content.Context;

import androidx.room.Room;

import com.example.smartdialer.room.AppDatabase;
import com.example.smartdialer.room.dao.CallDao;
import com.example.smartdialer.room.entity.Calls;

import java.util.List;

public class AddCallLog extends Thread{
    Context context;
    Calls calls;
    public AddCallLog(Context ctx, Calls callModel){
        this.context = ctx;
        this.calls = callModel;
    }

    @Override
    public void run() {
        super.run();
        AppDatabase db = Room.databaseBuilder(context,
                AppDatabase.class, "temp.db").build();
        CallDao callDao = db.callDao();
        callDao.insertAll(calls);
    }
}
