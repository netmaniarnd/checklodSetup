package com.netmania.ble.listener;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanRecord;

public interface AdvertiseListener {
    void onScanResult(BluetoothDevice device, int rssi, ScanRecord record);
}
