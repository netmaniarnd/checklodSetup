package com.netmania.ble.advertise;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Build;
import android.os.ParcelUuid;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.netmania.ble.listener.AdvertiseListener;

import java.util.List;
import java.util.UUID;
import java.util.Vector;

public class AdvertiseApi {
    public static final String TAG = AdvertiseApi.class.getSimpleName();

    public static AdvertiseApi mInstance;
    private Context mContext;
    private UUID mUuid;
    private AdvertiseListener mListener;
    private static BluetoothAdapter mBluetoothAdapter;
    private static BluetoothLeScanner mBluetoothLeScanner;

    public static AdvertiseApi getInstance() {
        if (mInstance == null) {
            mInstance = new AdvertiseApi();
        }
        return mInstance;
    }

    public void init(Context context, AdvertiseListener listener, UUID uuid) {
        mListener = listener;
        mContext = context;
        mUuid = uuid;
        initBluetooth();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void close() {
        mBluetoothLeScanner.stopScan(mScanCallback);
        mBluetoothLeScanner = null;
    }

    private void initBluetooth() {
        BluetoothManager mBluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            initBluetoothAdapter();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initBluetoothAdapter() {
        Thread t = new Thread(() -> {
            ScanSettings.Builder sb = new ScanSettings.Builder();
            sb.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).setReportDelay(0);// SCAN_MODE_LOW_LATENCY : 스캔주기 2초, SCAN_MODE_LOW_POWER : 기본 10초
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                sb.setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE).setNumOfMatches(ScanSettings.MATCH_NUM_MAX_ADVERTISEMENT);
            }
            ScanSettings scanSettings = sb.build();
            // 안드로이드 9.x macAddress 추가
            List<ScanFilter> scanFilters = new Vector<>();
            ScanFilter.Builder scanFilter = new ScanFilter.Builder();
            // serviceUuid 설정시 디바이스 여러종료 한꺼번에 받지 못함
            scanFilter.setServiceUuid(new ParcelUuid(mUuid));
            scanFilters.add(scanFilter.build());
            mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
            mBluetoothLeScanner.startScan(scanFilters, scanSettings, mScanCallback);
        });
        t.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            mListener.onScanResult(result.getDevice(), result.getRssi(), result.getScanRecord());
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            for (int i = 0; i < results.size(); i++) {
                mListener.onScanResult(results.get(i).getDevice(), results.get(i).getRssi(), results.get(i).getScanRecord());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.e(TAG, "onScanFailed : " + errorCode);
        }
    };
}