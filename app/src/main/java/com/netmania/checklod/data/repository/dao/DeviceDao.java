package com.netmania.checklod.data.repository.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.netmania.checklod.data.repository.entity.DeviceEntity;

import java.util.List;

@Dao
public interface DeviceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DeviceEntity stepEntity);

    @Query("SELECT * FROM DEVICE_TABLE ORDER BY flag DESC")
    LiveData<List<DeviceEntity>> getAll();

    @Query("SELECT * FROM DEVICE_TABLE WHERE mac = :mac")
    DeviceEntity getDeviceByMac(String mac);

    @Query("SELECT * FROM DEVICE_TABLE WHERE flag = :flag")
    List<DeviceEntity> getDeviceByFlag(Boolean flag);

    @Query("SELECT COUNT (*) FROM DEVICE_TABLE")
    int getCount();

    @Query("SELECT COUNT (*) FROM DEVICE_TABLE WHERE flag = :flag")
    int getFlagCount(Boolean flag);

    @Update
    void updateDevice(DeviceEntity entity);

    @Query("DELETE FROM DEVICE_TABLE")
    void deleteAll();

}
