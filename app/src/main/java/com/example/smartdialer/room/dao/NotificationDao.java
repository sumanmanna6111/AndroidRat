package com.example.smartdialer.room.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.smartdialer.room.entity.Noti;
import com.example.smartdialer.room.entity.Sms;

import java.util.List;

@Dao
public interface NotificationDao {
    @Query("SELECT * FROM notification")
    List<Noti> getAll();

    @Insert
    void insertAll(Noti noti);

    @Query("DELETE FROM notification")
    void deleteAll();

    @Delete
    void delete(Noti noti);
}
