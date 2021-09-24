package com.netmania.checklod.data.repository.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.netmania.checklod.data.repository.entity.DeviceLogEntity;

import java.util.List;

@Dao
public interface DeviceLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDeviceLog(DeviceLogEntity deviceLogEntity);

    @Query("SELECT * FROM DEVICE_LOG_TABLE WHERE mac = :mac ORDER BY sequence DESC")
    List<DeviceLogEntity> getAll(String mac);

    @Query("SELECT COUNT(*) FROM DEVICE_LOG_TABLE WHERE mac = :mac")
    int getCount(String mac);

    @Query("SELECT * FROM DEVICE_LOG_TABLE WHERE mac = :mac AND sequence = :sequence ORDER BY sequence DESC LIMIT 1")
    DeviceLogEntity getDeviceLog(String mac, int sequence);

    @Query("SELECT * FROM DEVICE_LOG_TABLE WHERE mac = :mac ORDER BY sequence ASC LIMIT 1")
    DeviceLogEntity getFirstLog(String mac);

    @Query("SELECT * FROM DEVICE_LOG_TABLE WHERE mac = :mac ORDER BY sequence DESC LIMIT 1")
    DeviceLogEntity getLastLog(String mac);

    @Query("SELECT * FROM DEVICE_LOG_TABLE WHERE mac = :mac ORDER BY outerTemp DESC LIMIT 1")
    DeviceLogEntity getMaxTempLog(String mac);

    @Query("SELECT * FROM DEVICE_LOG_TABLE WHERE mac = :mac ORDER BY outerTemp ASC LIMIT 1")
    DeviceLogEntity getMinTempLog(String mac);

    @Query("SELECT * FROM DEVICE_LOG_TABLE WHERE mac = :mac AND sent=0")
    List<DeviceLogEntity> getNotSent(String mac);

    @Query("SELECT sequence FROM DEVICE_LOG_TABLE WHERE mac = :mac ORDER BY sequence DESC")
    List<Integer> getLostData(String mac);

    @Query("UPDATE DEVICE_LOG_TABLE SET sent=1 WHERE mac = :mac AND sequence = :sequence")
    void updateDeviceSent(String mac, int sequence);

    @Query("DELETE FROM DEVICE_LOG_TABLE WHERE mac = :mac")
    void deleteLog(String mac);

}
