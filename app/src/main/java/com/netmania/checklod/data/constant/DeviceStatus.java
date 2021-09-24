package com.netmania.checklod.data.constant;

public class DeviceStatus {
    /**
     * 온도 안정
     */
    public static final int TEMP_STABLE = 0;
    /**
     * 온도 위험
     */
    public static final int TEMP_CAUTION = 1;
    /**
     * 온도 이탈
     */
    public static final int TEMP_EMERGENCY = 2;

    /**
     * 디바이스 꺼짐
     */
    public static final int DEVICE_OFF = -1;
    /**
     * 디바이스 준비
     */
    public static final int DEVICE_READY = 0;
    /**
     * 디바이스 시작
     */
    public static final int DEVICE_RUN = 1;
}
