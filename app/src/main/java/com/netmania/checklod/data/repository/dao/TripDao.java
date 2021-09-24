package com.netmania.checklod.data.repository.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.netmania.checklod.data.repository.entity.TripEntity;

import java.util.List;

@Dao
public interface TripDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertTrip(TripEntity entity);

    @Query("SELECT * FROM TRIP_TABLE ORDER BY mac ASC")
    LiveData<List<TripEntity>> getAllTrip();

    @Query("SELECT * FROM TRIP_TABLE WHERE outerTemp > :min AND outerTemp < :max ORDER BY outerTemp ASC")
    LiveData<List<TripEntity>> getTempAll(int min, int max);

    @Query("SELECT * FROM TRIP_TABLE ORDER BY idx DESC")
    List<TripEntity> getAll();

    @Query("SELECT COUNT(*) FROM TRIP_TABLE")
    int getCount();

    @Query("SELECT * FROM TRIP_TABLE WHERE mac = :mac")
    TripEntity getTripInfoByMac(String mac);

    @Query("SELECT * FROM TRIP_TABLE WHERE stickerNo = :stickerNo")
    TripEntity getTripInfoByStickerNo(String stickerNo);

    @Update
    void updateTrip(TripEntity entity);

    @Query("DELETE FROM TRIP_TABLE WHERE mac = :mac")
    void deleteTrip(String mac);

}
