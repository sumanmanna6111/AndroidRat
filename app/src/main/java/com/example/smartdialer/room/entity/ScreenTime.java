package com.example.smartdialer.room.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "screen")
public class ScreenTime {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "status")
    public String status;

    @ColumnInfo(name = "time")
    public String time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
