package com.netmania.checklod.data.repository.entity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import lombok.Data;

@Entity(tableName = "device_table", indices = @Index(value = {"idx", "mac"}, unique = true))

@Data
public class DeviceEntity {
    @PrimaryKey(autoGenerate = true)
    private int idx;

    private String mac;
    private Boolean flag = false;

    public static DiffUtil.ItemCallback<DeviceEntity> DIFF_CALLBACK = new DiffUtil.ItemCallback<DeviceEntity>() {
        @Override
        public boolean areItemsTheSame(@NonNull DeviceEntity oldItem, @NonNull DeviceEntity newItem) {
            return oldItem.getIdx() == newItem.getIdx();
        }
        @Override
        public boolean areContentsTheSame(@NonNull DeviceEntity oldItem, @NonNull DeviceEntity newItem) {
            return oldItem.equals(newItem);
        }
    };
}
