package com.netmania.checklod.domain.listener;

import android.bluetooth.BluetoothGatt;

public interface GattListener {
    void onRequest(byte[] bytes);
    void onLoaded(byte[] bytes, BluetoothGatt bluetoothGatt);
}
