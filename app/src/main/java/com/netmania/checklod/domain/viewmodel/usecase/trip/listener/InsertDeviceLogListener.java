package com.netmania.checklod.domain.viewmodel.usecase.trip.listener;

import com.netmania.checklod.data.repository.entity.DeviceLogEntity;

public interface InsertDeviceLogListener {
    void onComplete(DeviceLogEntity entity);
    void onFail();
}
