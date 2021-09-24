package com.netmania.checklod.data.repository.entity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import lombok.Data;

@Entity(tableName = "log_table", indices = @Index(value = {"sequence"}, unique = true))
@Data
public class LogEntity {
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

    public static DiffUtil.ItemCallback<LogEntity> DIFF_CALLBACK = new  DiffUtil.ItemCallback<LogEntity>() {
        @Override
        public boolean areItemsTheSame(@NonNull LogEntity oldItem, @NonNull LogEntity newItem) {
            return oldItem.getIdx() == newItem.getIdx();
        }
        @Override
        public boolean areContentsTheSame(@NonNull LogEntity oldItem, @NonNull LogEntity newItem) {
            return oldItem.equals(newItem);
        }
    };
}
