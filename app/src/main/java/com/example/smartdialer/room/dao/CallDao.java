package com.example.smartdialer.room.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.smartdialer.room.entity.Calls;

import java.util.List;

@Dao
public interface CallDao {
    @Query("SELECT * FROM calls")
    List<Calls> getAll();

    /*@Query("SELECT * FROM user WHERE uid IN (:userIds)")
    List<User> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM user WHERE first_name LIKE :first AND " +
            "last_name LIKE :last LIMIT 1")
    User findByName(String first, String last);*/
    @Query("DELETE FROM calls")
    void deleteAll();

    @Insert
    void insertAll(Calls calls);

    @Delete
    void delete(Calls calls);
}
