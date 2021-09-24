package com.netmania.checklod.domain.listener;

import android.bluetooth.BluetoothGatt;

import java.util.ArrayList;

public interface OffsetListener {
    void onConnection();
    void onLoaded(ArrayList<Integer> respose);
}
