package com.netmania.checklod.data.constant;

import java.util.UUID;

public class DeviceUUID {
    public static final UUID CCCD = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    /*
    // 구버전 디바이스
    public static final UUID RX_SERVICE_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
    public static final UUID RX_CHAR_UUID = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");
    public static final UUID TX_CHAR_UUID = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e");
    */
    public static final UUID RX_SERVICE_UUID = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");
    public static final UUID RX_CHAR_UUID = UUID.fromString("0000fff2-0000-1000-8000-00805f9b34fb"); // 10,12,13
    public static final UUID TX_CHAR_UUID = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");
    public static final int GATT_COMMAND_STX = 0x63;
}
