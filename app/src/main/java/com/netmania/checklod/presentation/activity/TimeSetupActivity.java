package com.netmania.checklod.presentation.activity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.netmania.ble.advertise.AdvertiseApi;
import com.netmania.ble.listener.AdvertiseListener;
import com.netmania.ble.listener.GattListener;
import com.netmania.checklod.BaseApplication;
import com.netmania.checklod.R;
import com.netmania.checklod.data.repository.entity.DeviceEntity;
import com.netmania.checklod.data.vo.DeviceIntentVo;
import com.netmania.checklod.databinding.ActivityMainBinding;
import com.netmania.checklod.databinding.ActivityTimeSetupBinding;
import com.netmania.checklod.domain.gatt.ConnectionApi;
import com.netmania.checklod.domain.listener.ConnectionListener;
import com.netmania.checklod.domain.listener.DeviceListClickListener;
import com.netmania.checklod.presentation.BaseActivity;
import com.netmania.checklod.presentation.adapter.DeviceAdapter;
import com.netmania.checklod.presentation.adapter.DeviceTimeSetupAdapter;

import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.Vector;

import lombok.SneakyThrows;

public class TimeSetupActivity extends BaseActivity {

    private String TAG = MainActivity.class.getSimpleName();

    public static final UUID RX_SERVICE_UUID = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb");

    private ActivityTimeSetupBinding mBinding;
    private DeviceTimeSetupAdapter mDeviceAdapter;
    private GridLayoutManager mLayoutManager;

    private static BluetoothAdapter mBluetoothAdapter;
    private static BluetoothLeScanner mBluetoothLeScanner;

    private DeviceEntity mDeviceEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_time_setup);

        mDeviceAdapter = new DeviceTimeSetupAdapter(this, deviceListClickListener);
        mLayoutManager = new GridLayoutManager(this, 1);

        mBinding.rvDevice.setAdapter(mDeviceAdapter);
        mBinding.rvDevice.setLayoutManager(mLayoutManager);

        mDeviceViewModel.getAll().observe(this, deviceEntities -> {
            mDeviceAdapter.submitList(deviceEntities);
            Thread t = new Thread(() -> {
                mBinding.tvDeviceCount.setText("연결 : " + BaseActivity.mDb.deviceDao().getCount() + ", 완료 : " + BaseActivity.mDb.deviceDao().getFlagCount(true));
            });
            t.start();
        });

        BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();

        setUserPermission();
    }

    private void setUserPermission() {
        Log.e(TAG, "VERSION : " + Build.VERSION.SDK_INT);
        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                )
                .check();
    }

    PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                AdvertiseApi.getInstance().init(mContext, advertiseListener, RX_SERVICE_UUID);
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        setDeviceTime();
                    }
                };
                Timer time = new Timer();
                time.schedule(timerTask, 0, 30000);
            }
        }

        @Override
        public void onPermissionDenied(List<String> deniedPermissions) {
            // 권한 설정 거부
            finishAffinity();
            System.exit(0);
        }
    };

    DeviceListClickListener deviceListClickListener = (index, mac) -> {
        Log.e(TAG, "mac : " + mac);
    };

    AdvertiseListener advertiseListener = new AdvertiseListener() {
        @SneakyThrows
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onScanResult(BluetoothDevice device, int rssi, ScanRecord record) {
            insertDeviceData(device, rssi, record.getBytes());
        }
    };

    private void insertDeviceData(final BluetoothDevice bleDevice, final int rssi, final byte[] adv) {
        Log.e(TAG, bleDevice.getAddress());// 4e45544d414e494131323334
        Thread t = new Thread(() -> {
            try {
                DeviceEntity entity = BaseActivity.mDb.deviceDao().getDeviceByMac(bleDevice.getAddress());
                if (entity == null) {
                    entity = new DeviceEntity();
                    entity.setMac(bleDevice.getAddress());
                    BaseActivity.mDb.deviceDao().insert(entity);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t.start();
    }

    /**
     * 디바이스 gatt 시간설정
     */
    GattListener mGattListener = new GattListener() {
        @Override
        public void onConnect() {
            Log.e(TAG, "gatt 연결완료!");
            com.netmania.ble.gatt.GattApi.getInstance().sendCommandRead(8);
        }

        @Override
        public void onLoaded(int type, byte[] bytes, BluetoothGatt bluetoothGatt) {
            Thread t = new Thread(() -> {
                mDeviceEntity = BaseActivity.mDb.deviceDao().getDeviceByMac(mDeviceEntity.getMac());
                mDeviceEntity.setFlag(true);
                BaseActivity.mDb.deviceDao().updateDevice(mDeviceEntity);
            });
            t.start();
        }

        @Override
        public void onFailed() {
            Log.e(TAG, "gatt 연결종료!");
        }
    };

    /**
     * 디바이스 gatt 시간설정
     */
    private void setDeviceTime() {
        Thread t = new Thread(() -> {
            List<DeviceEntity> entities = BaseActivity.mDb.deviceDao().getDeviceByFlag(false);
            Random random = new Random();
            if (entities.size() != 0) {
                int index = random.nextInt(entities.size());
                mDeviceEntity = entities.get(index);
                Log.e(TAG, mDeviceEntity.toString());
                com.netmania.ble.gatt.GattApi.getInstance().init(this, mGattListener, mDeviceEntity.getMac());
            }
        });
        t.start();
    }
}