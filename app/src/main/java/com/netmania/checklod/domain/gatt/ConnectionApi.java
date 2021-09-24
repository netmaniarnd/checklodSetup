package com.netmania.checklod.domain.gatt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.netmania.checklod.data.constant.DeviceUUID;
import com.netmania.checklod.data.vo.DeviceIntentVo;
import com.netmania.checklod.domain.listener.ConnectionListener;
import com.netmania.checklod.domain.listener.GattReader;
import com.netmania.checklod.domain.parser.DeviceLogParser;
import com.netmania.checklod.presentation.BaseActivity;

import java.util.Calendar;

public class ConnectionApi {

    private static final String TAG = ConnectionApi.class.getSimpleName();
    private Context mContext;
    private DeviceIntentVo mBeaconVo;
    private BluetoothGatt mGatt;
    private GattReader mGattReader = null;
    private Boolean mGattConnected = false;
    private ConnectionListener mConnectionListener;
    private int mGattType = 0;

    private static ConnectionApi mInstance;

    // 0-16 번지 데이터 세팅
    public static ConnectionApi getInstance() {
        if (mInstance == null) {
            mInstance = new ConnectionApi();
        }
        return mInstance;
    }

    public void ConnectionApi(Context context, DeviceIntentVo logger, ConnectionListener connectionListener) {
        mContext = context;
        mBeaconVo = logger;
        mConnectionListener = connectionListener;
        connectGatt();
    }

    public boolean getGattConnected() {
        return mGattConnected;
    }

    private void connectGatt() {
        if (mGattConnected) {
            return;
        }
        BluetoothDevice device = getDeviceGatt();
        if (device == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mGatt = device.connectGatt(mContext, false, gattCallback, 2);
        } else {
            mGatt = device.connectGatt(mContext, false, gattCallback);
        }
    }

    public void sendCommandWrite(int type) {
        Log.e(TAG, "sendCommandWrite : " + type);
        BluetoothGattCharacteristic characteristic;
        mGattType = type;
        byte[] byteValues = {};
        switch (type) {
            case 1:
                byteValues = new byte[]{DeviceUUID.GATT_COMMAND_STX, 0x01};
                break;
            case 8:
                Calendar calendar = Calendar.getInstance();
                String year = Integer.toString(calendar.get(Calendar.YEAR)).substring(2);
                byteValues = new byte[]{DeviceUUID.GATT_COMMAND_STX, 0x08,
                        Byte.parseByte(Integer.toHexString(Integer.parseInt(year)), 16),
                        Byte.parseByte(Integer.toHexString(calendar.get(Calendar.MONTH) + 1), 16),
                        Byte.parseByte(Integer.toHexString(calendar.get(Calendar.DAY_OF_MONTH)), 16),
                        Byte.parseByte(Integer.toHexString(calendar.get(Calendar.HOUR_OF_DAY)), 16),
                        Byte.parseByte(Integer.toHexString(calendar.get(Calendar.MINUTE)), 16),
                        Byte.parseByte(Integer.toHexString(calendar.get(Calendar.SECOND)), 16)};
                break;
        }
        try {
            Log.e(TAG, DeviceUUID.RX_CHAR_UUID.toString());
            characteristic = mGatt.getService(DeviceUUID.RX_SERVICE_UUID).getCharacteristic(DeviceUUID.RX_CHAR_UUID);
        } catch (Exception e) {
            Log.e(TAG, "Exception ::" + e.toString());
            return;
        }
        characteristic.setValue(byteValues);
        mGatt.writeCharacteristic(characteristic);
    }

    public void sendCommandGetSaveData(int type, int sequence) {
        BluetoothGattCharacteristic characteristic;
        mGattType = type;
        byte[] bytes = DeviceLogParser.convertLittleEndian(sequence);
        byte[] byteValues = new byte[]{DeviceUUID.GATT_COMMAND_STX, 0x02, bytes[0], bytes[1]};//, Byte.parseByte(Integer.toString(checkSum))};
        try {
            Log.e(TAG, DeviceUUID.RX_CHAR_UUID.toString());
            characteristic = mGatt.getService(DeviceUUID.RX_SERVICE_UUID).getCharacteristic(DeviceUUID.RX_CHAR_UUID);
        } catch (Exception e) {
            Log.e(TAG, "Exception ::" + e.toString());
            return;
        }
        characteristic.setValue(byteValues);
        mGatt.writeCharacteristic(characteristic);
    }

    public void disconnectGatt() {
        Log.e(TAG, "ConnectionApi : disconnectGatt");
        if (mGatt != null) {
            mGatt.disconnect();
            mGatt.close();
            mGatt = null;
            mGattConnected = false;
        }
    }

    private void enableCmdReceveToGatt() {
        BluetoothGattService RxService = mGatt.getService(DeviceUUID.RX_SERVICE_UUID);
        if (RxService == null) {
            Log.i(TAG, "Rx service not found!");
            return;
        }

        BluetoothGattCharacteristic TxChar = RxService.getCharacteristic(DeviceUUID.TX_CHAR_UUID);
        if (TxChar == null) {
            Log.i(TAG, "Tx charateristic not found!");
            return;
        }
        mGatt.setCharacteristicNotification(TxChar, true);
        BluetoothGattDescriptor descriptor = TxChar.getDescriptor(DeviceUUID.CCCD);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        mGatt.writeDescriptor(descriptor);
    }

    private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, final int status, int newState) {
            Log.e(TAG, "status, newState = " + status + ", " + newState);
            if (status == 0 && newState == 2) {
                mGattConnected = true;
                startGattDiscover();
            } else {
                mConnectionListener.onFailed();
                disconnectGatt();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.i(TAG, "onServicesDiscovered : ");
            BluetoothGattService mBluetoothGattService = mGatt.getService(DeviceUUID.RX_SERVICE_UUID);
            if (gatt != null) {
                // Log.e(TAG, "Service characteristic UUID found: " + mBluetoothGattService.getUuid().toString());
            } else {
                // Log.e(TAG, "Service characteristic not found for UUID: " + BeaconUtil.RX_SERVICE_UUID);
            }
            enableCmdReceveToGatt();
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            byte[] rxBytes = characteristic.getValue();
            if (DeviceUUID.TX_CHAR_UUID.equals(characteristic.getUuid())) {
                Log.i(TAG, "onCharacteristicChanged - uuid : " + characteristic.getUuid());
                mConnectionListener.onLoaded(mGattType, rxBytes, mBeaconVo.getMac().trim());
                // disconnectGatt();
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.e(TAG, "onCharacteristicRead - status : " + status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            Log.e(TAG, "onDescriptorWrite - status : " + status);
            mGattConnected = true;
            mConnectionListener.onConnect();
        }
    };

    private void startGattDiscover() {
        mGatt.discoverServices();
    }

    private BluetoothDevice getDeviceGatt() {
        BluetoothAdapter bluetoothAdapter;
        final BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        BluetoothDevice device;
        if (BluetoothAdapter.checkBluetoothAddress(mBeaconVo.getMac().trim())) {
            device = bluetoothAdapter.getRemoteDevice(mBeaconVo.getMac().trim());
            return device;
        } else {
            mGattConnected = false;
            return null;
        }
    }
}
