package com.example.smartdialer.room.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.smartdialer.room.entity.Calls;
import com.example.smartdialer.room.entity.Sms;

import java.util.List;

@Dao
public interface SmsDao {

    @Query("SELECT * FROM sms")
    List<Sms> getAll();

    @Query("DELETE FROM sms")
    void deleteAll();

    @Insert
    void insertAll(Sms sms);

    @Delete
    void delete(Sms sms);
}
