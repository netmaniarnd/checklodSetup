package com.netmania.ble.listener;

import android.bluetooth.BluetoothGatt;

public interface GattListener {
    void onConnect();
    void onLoaded(int type, byte[] bytes, BluetoothGatt bluetoothGatt);
    void onFailed();
}
