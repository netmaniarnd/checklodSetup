package com.netmania.checklod.presentation.activity;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanRecord;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaDrm;
import android.media.UnsupportedSchemeException;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.netmania.ble.advertise.AdvertiseApi;
import com.netmania.ble.listener.AdvertiseListener;
import com.netmania.checklod.BaseApplication;
import com.netmania.checklod.R;
import com.netmania.checklod.data.constant.DeviceUUID;
import com.netmania.checklod.data.repository.entity.DeviceLogEntity;
import com.netmania.checklod.data.repository.entity.TripEntity;
import com.netmania.checklod.databinding.ActivityMainBinding;
import com.netmania.checklod.domain.listener.MenuListClickListener;
import com.netmania.checklod.domain.listener.PermissionListener;
import com.netmania.checklod.domain.parser.DeviceLogParser;
import com.netmania.checklod.domain.viewmodel.usecase.PermissionUseCase;
import com.netmania.checklod.presentation.BaseActivity;
import com.netmania.checklod.presentation.adapter.MenuAdapter;
import com.netmania.checklod.util.DateTimeUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import lombok.SneakyThrows;

public class MainActivity extends BaseActivity {

    public static String TAG = MainActivity.class.getSimpleName();
    private MenuAdapter mAdapter;
    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        PermissionUseCase.setUserPermission(mContext, mPermissionListener);
    }

    private void init() {
        mAdapter = new MenuAdapter(list, mMenuListClickListener);
        mBinding.rvMenu.setAdapter(mAdapter);
        mBinding.rvMenu.setLayoutManager(new GridLayoutManager(this, 1));
        AdvertiseApi.getInstance().init(getApplicationContext(), mAdvertiseListener, DeviceUUID.RX_SERVICE_UUID);

        getAndroidID();
        Log.e(TAG, String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())));
        Log.e(TAG, String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())));
        Log.e(TAG, String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())));

        Log.e(TAG, "holes.size() : " + getArrayList().size());
    }

    public List getArrayList() {
        List<Integer> holes = new ArrayList<>();
        int maxSequence = 65536;
        int minSequence = maxSequence-(1440*40);
        for (int sequence = minSequence; sequence <= maxSequence; sequence++) {
            holes.add(new Integer(sequence));
        }
        Log.e(TAG, "minSequence : " + minSequence);
        Log.e(TAG, "maxSequence : " + maxSequence);
        Log.e(TAG, "holes.size() : " + holes.size());
        return holes;
    }

    /**
     * Returns the unique identifier for the device
     * @return unique identifier for the device
     */
    public void getAndroidID() {
        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.e(TAG, "AndroidID : " + androidId);
        Log.e(TAG, "AndroidID : " + androidId.length());
    }

    /**
     * 각 서비스 이동
     */
    MenuListClickListener mMenuListClickListener = new MenuListClickListener() {
        @Override
        public void onItemEvent(int pos) {
            Intent intent;
            switch (pos) {
                case 0:
                    intent = new Intent(getApplicationContext(), TimeSetupActivity.class);
                    intent.putExtra("title", pos);
                    startActivity(intent);
                    break;
                case 1:
                    intent = new Intent(getApplicationContext(), MultiDeviceActivity.class);
                    intent.putExtra("title", pos);
                    startActivity(intent);
                    break;
                default:
                    intent = new Intent(getApplicationContext(), DeviceListActivity.class);
                    intent.putExtra("title", pos);
                    startActivity(intent);
                    break;

            }
        }
    };

    /**
     * Permission 설정
     */
    PermissionListener mPermissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            init();
        }

        @Override
        public void onPermissionDenied() {
            finishAffinity();
            System.exit(0);
        }
    };

    AdvertiseListener mAdvertiseListener = new AdvertiseListener() {

        @SneakyThrows
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onScanResult(BluetoothDevice device, int rssi, ScanRecord record) {
            // Log.e(TAG, record.toString());
            String mac = device.getAddress();
            TripEntity tripEntity = null;
            DeviceLogEntity logEntity = DeviceLogParser.getDeviceLogEntity(record.getBytes(), mac, DeviceLogParser.PROTOCOL_OFFSET);
            try {
                tripEntity = mTripViewModel.getTripInfoByMac(mac);
                if (tripEntity == null) {
                    tripEntity = new TripEntity();
                    tripEntity.setMac(mac);
                    tripEntity.setStartSeq(logEntity.getSequence());
                    mTripViewModel.insert(tripEntity);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            logEntity.setTimeStamp(DateTimeUtil.getCurrentTimestamp());
            int batteryValue = Integer.parseInt(String.valueOf(Math.round(logEntity.getBattery())));
            tripEntity.setEndSeq(logEntity.getSequence());
            tripEntity.setRssi(logEntity.getRssi());
            tripEntity.setRtc(logEntity.getRtc());
            tripEntity.setOuterTemp(logEntity.getOuterTemp());
            tripEntity.setInnerTemp(logEntity.getInnerTemp());
            tripEntity.setBattery(batteryValue);
            mDeviceLogViewModel.insert(logEntity);
            mTripViewModel.update(tripEntity);
        }
    };
}