package com.netmania.checklod.data.vo;

import lombok.Data;

@Data
public class DeviceLogVo extends BaseVo {
    private int idx;

    private String mac;
    private int sequence;
    private String rtc;
    private Double outerTemp;
    private Double innerTemp;
    private int rssi;
    private int battery;
}
