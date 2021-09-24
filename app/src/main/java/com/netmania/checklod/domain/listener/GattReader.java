package com.netmania.checklod.domain.listener;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;

import java.util.ArrayList;
import java.util.HashMap;

public interface GattReader {
    BluetoothGattCallback getGattCallback();
    void connectDevice(BluetoothGatt gatt, LoaderListener loaderCallback) throws Exception;
    ArrayList<HashMap<Integer, Double>> read(ArrayList<Integer> needed);
    void close();
}
