package com.netmania.checklod.data.repository;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.netmania.checklod.data.repository.dao.DeviceDao;
import com.netmania.checklod.data.repository.dao.DeviceLogDao;
import com.netmania.checklod.data.repository.dao.LogDao;
import com.netmania.checklod.data.repository.dao.TripDao;
import com.netmania.checklod.data.repository.entity.DeviceEntity;
import com.netmania.checklod.data.repository.entity.DeviceLogEntity;
import com.netmania.checklod.data.repository.entity.LogEntity;
import com.netmania.checklod.data.repository.entity.TripEntity;

@Database(entities = {TripEntity.class, DeviceLogEntity.class, DeviceEntity.class, LogEntity.class}, version = 1, exportSchema = false)
public abstract class AppDataBase extends RoomDatabase {

    // Dao setting
    public abstract DeviceDao deviceDao();
    public abstract TripDao tripDao();
    public abstract DeviceLogDao deviceLogDao();
    public abstract LogDao logDao();

    private static String dbName = "setup.db";

    private static AppDataBase INSTANCE;

    static final Migration MIGRATION_1_1 = new Migration(1, 1) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Since we didn't alter the table, there's nothing else to do here.
        }
    };

    public static AppDataBase getDatabase(Context context) {
        synchronized (AppDataBase.class) {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDataBase.class, dbName)
                        .addMigrations(MIGRATION_1_1).fallbackToDestructiveMigration().build();
            }
            return INSTANCE;
        }
    }
}
