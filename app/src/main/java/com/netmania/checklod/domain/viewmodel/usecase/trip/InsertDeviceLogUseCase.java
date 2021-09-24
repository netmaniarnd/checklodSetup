package com.netmania.checklod.domain.viewmodel.usecase.trip;

import com.netmania.checklod.data.repository.entity.DeviceLogEntity;
import com.netmania.checklod.data.repository.entity.LogEntity;
import com.netmania.checklod.domain.parser.DeviceLogParser;
import com.netmania.checklod.domain.viewmodel.usecase.trip.listener.InsertDeviceLogListener;
import com.netmania.checklod.presentation.BaseActivity;
import com.netmania.checklod.util.DateTimeUtil;

import lombok.SneakyThrows;

public class InsertDeviceLogUseCase {

    public static String TAG = InsertDeviceLogUseCase.class.getSimpleName();

    /**
     * 디비에 로그 insert
     */
    @SneakyThrows
    public static void insertDeviceLog(InsertDeviceLogListener listener, byte[] bytes, String mac) {
        DeviceLogEntity logEntity = DeviceLogParser.getDeviceLogEntity(bytes, mac, 0);
        LogEntity entity = new LogEntity();
        entity.setSequence(logEntity.getSequence());
        entity.setInnerTemp(logEntity.getInnerTemp());
        entity.setOuterTemp(logEntity.getOuterTemp());
        entity.setRtc(logEntity.getRtc());
        entity.setBattery(logEntity.getBattery());
        entity.setMac(mac);
        // entity.setRssi(rssi);
        entity.setTimeStamp(DateTimeUtil.getCurrentTimestamp());
        entity.setSent(0);
        BaseActivity.mLogViewModel.insert(entity);
        listener.onComplete(logEntity);
    }
}
