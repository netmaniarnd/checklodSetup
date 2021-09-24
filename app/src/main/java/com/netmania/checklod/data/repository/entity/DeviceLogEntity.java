package com.netmania.checklod.data.repository.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import lombok.Data;

@Entity(tableName = "device_log_table", indices = @Index(value = {"sequence"}, unique = true))
@Data
public class DeviceLogEntity {
    @PrimaryKey(autoGenerate = true)
    private int idx;

    private String mac;
    private int sequence;
    private String rtc;
    private long timeStamp;
    private Double outerTemp = 0.0;
    private Double innerTemp = 0.0;
    private int battery;
    private int rssi;
    private int sent = 0;
}
