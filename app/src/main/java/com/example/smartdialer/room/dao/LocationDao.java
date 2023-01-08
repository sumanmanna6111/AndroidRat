package com.example.smartdialer.room.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.smartdialer.room.entity.Calls;
import com.example.smartdialer.room.entity.LocationEntity;

import java.util.List;

@Dao
public interface LocationDao {
    @Query("SELECT * FROM location")
    List<LocationEntity> getAll();

    /*@Query("SELECT * FROM user WHERE uid IN (:userIds)")
    List<User> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
            "last_name LIKE :last LIMIT 1")
    User findByName(String first, String last);*/
    @Query("DELETE FROM location")
    void deleteAll();

    @Insert
    void insertAll(LocationEntity location);

    @Delete
    void delete(LocationEntity location);
}
