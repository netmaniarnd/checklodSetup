package com.netmania.checklod.data.repository.entity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.netmania.checklod.data.constant.DeviceStatus;

import lombok.Data;

@Entity(tableName = "trip_table", indices = @Index(value = {"mac", "stickerNo", "invoiceNo"}, unique = true))
@Data
public class TripEntity {
    @PrimaryKey(autoGenerate = true)
    private int idx;

    private int step = 0;
    private int tempStatus = 0;
    private String mac;
    private String stickerNo;
    private String invoiceNo;
    private String tempString;
    private Double innerTemp = 0.0;
    private Double outerTemp = 0.0;
    private Double lowerLimit = 0.0;
    private Double upperLimit = 0.0;
    private String rtc;
    private int startSeq;
    private int endSeq;
    private int battery = 0;
    private int rssi;
    private int upload = 0;
    private int takeOver = 0; // 인수예정
    private Boolean isStartCheck = false;
    private Boolean isDeviceModel = false;
    private int beaconStatus = DeviceStatus.DEVICE_OFF;

    public static DiffUtil.ItemCallback<TripEntity> DIFF_CALLBACK = new  DiffUtil.ItemCallback<TripEntity>() {
        @Override
        public boolean areItemsTheSame(@NonNull TripEntity oldItem, @NonNull TripEntity newItem) {
            return oldItem.getIdx() == newItem.getIdx();
        }
        @Override
        public boolean areContentsTheSame(@NonNull TripEntity oldItem, @NonNull TripEntity newItem) {
            return oldItem.equals(newItem);
        }
    };
}
