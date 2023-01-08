package com.example.smartdialer.room.worker;


import android.content.Context;

import androidx.room.Room;

import com.example.smartdialer.room.AppDatabase;
import com.example.smartdialer.room.dao.CallDao;
import com.example.smartdialer.room.dao.SmsDao;
import com.example.smartdialer.room.entity.Sms;

public class AddSmsLog extends Thread {
    Context ctx;
    Sms data;
    public AddSmsLog(Context context, Sms sms) {
        this.ctx = context;
        this.data = sms;
    }

    @Override
    public void run() {
        super.run();
        AppDatabase db = Room.databaseBuilder(ctx,
                AppDatabase.class, "temp.db").build();
        SmsDao smsDao = db.smsDao();
        smsDao.insertAll(data);
    }
}
