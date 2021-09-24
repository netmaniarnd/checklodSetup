package com.netmania.checklod.data.repository.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.netmania.checklod.data.repository.entity.LogEntity;

import java.util.List;

@Dao
public interface LogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDeviceLog(LogEntity LogEntity);

    @Query("SELECT * FROM LOG_TABLE WHERE mac = :mac ORDER BY sequence ASC")
    LiveData<List<LogEntity>> getAllLog(String mac);

    @Query("SELECT * FROM LOG_TABLE WHERE mac = :mac ORDER BY sequence DESC")
    List<LogEntity> getAll(String mac);

    @Query("SELECT COUNT(*) FROM LOG_TABLE WHERE mac = :mac")
    int getCount(String mac);

    @Query("SELECT * FROM LOG_TABLE WHERE mac = :mac AND sequence = :sequence ORDER BY sequence DESC LIMIT 1")
    LogEntity getDeviceLog(String mac, int sequence);

    @Query("SELECT * FROM LOG_TABLE WHERE mac = :mac ORDER BY sequence ASC LIMIT 1")
    LogEntity getFirstLog(String mac);

    @Query("SELECT * FROM LOG_TABLE WHERE mac = :mac ORDER BY sequence DESC LIMIT 1")
    LogEntity getLastLog(String mac);

    @Query("SELECT * FROM LOG_TABLE WHERE mac = :mac ORDER BY outerTemp DESC LIMIT 1")
    LogEntity getMaxTempLog(String mac);

    @Query("SELECT * FROM LOG_TABLE WHERE mac = :mac ORDER BY outerTemp ASC LIMIT 1")
    LogEntity getMinTempLog(String mac);

    @Query("SELECT * FROM LOG_TABLE WHERE mac = :mac AND sent=0")
    List<LogEntity> getNotSent(String mac);

    @Query("SELECT sequence FROM LOG_TABLE WHERE mac = :mac ORDER BY sequence DESC")
    List<Integer> getLostData(String mac);

    @Query("UPDATE LOG_TABLE SET sent=1 WHERE mac = :mac AND sequence = :sequence")
    void updateDeviceSent(String mac, int sequence);

    @Query("DELETE FROM LOG_TABLE WHERE mac = :mac")
    void deleteLog(String mac);

}
