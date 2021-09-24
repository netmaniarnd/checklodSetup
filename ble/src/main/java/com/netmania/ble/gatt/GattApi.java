package com.netmania.ble.gatt;

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

import com.netmania.ble.listener.GattListener;
import com.netmania.ble.util.Utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class GattApi {
    public static String TAG = GattApi.class.getSimpleName();

    private static GattApi mInstance;
    private Context mContext;
    private String mDeviceMac;
    private GattListener mListener;
    private Boolean mGattConnected = false;
    private BluetoothGatt mGatt;
    private int mGattType;

    public static synchronized GattApi getInstance() {
        if (mInstance == null) {
            mInstance = new GattApi();
        }
        return mInstance;
    }

    public void init(Context context, GattListener listener, String mac) {
        mListener = listener;
        mContext = context;
        mDeviceMac = mac;
        connectGatt();
    }

    private void connectGatt() {
        if (mGattConnected) {
            disconnectGatt();
        }
        BluetoothDevice device = getDeviceGatt();
        if (device == null) {
            disconnectGatt();
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mGatt = device.connectGatt(mContext, false, gattCallback, 2);
        } else {
            mGatt = device.connectGatt(mContext, false, gattCallback);
        }
    }

    public void sendCommandRead(int index) {
        mGattType = index;
        BluetoothGattCharacteristic characteristic;
        Log.e(TAG, "index : " + index);
        byte[] byteValues = {};
        switch (index) {
            case 1:
                byteValues = new byte[]{Utility.GATT_COMMAND_STX, 0x01};
                break;
            case 3:
                byteValues = new byte[]{Utility.GATT_COMMAND_STX, 0x03};
                break;
            case 5:
                byteValues = new byte[]{Utility.GATT_COMMAND_STX, 0x05};
                break;
            case 7:
                byteValues = new byte[]{Utility.GATT_COMMAND_STX, 0x07};
                break;
            case 8:
                Calendar calendar = Calendar.getInstance();
                String year = Integer.toString(calendar.get(Calendar.YEAR)).substring(2);
                byteValues = new byte[]{Utility.GATT_COMMAND_STX, 0x08,
                        Byte.parseByte(Integer.toHexString(Integer.parseInt(year)), 16),
                        Byte.parseByte(Integer.toHexString(calendar.get(Calendar.MONTH) + 1), 16),
                        Byte.parseByte(Integer.toHexString(calendar.get(Calendar.DAY_OF_MONTH)), 16),
                        Byte.parseByte(Integer.toHexString(calendar.get(Calendar.HOUR_OF_DAY)), 16),
                        Byte.parseByte(Integer.toHexString(calendar.get(Calendar.MINUTE)), 16),
                        Byte.parseByte(Integer.toHexString(calendar.get(Calendar.SECOND)), 16)};
                break;
            case 9:
                byteValues = new byte[]{Utility.GATT_COMMAND_STX, 0x09};
                break;
            case 12:
                byteValues = new byte[]{Utility.GATT_COMMAND_STX, 0x0C};
                break;
            default:
                byteValues = new byte[]{};
                break;
        }
        Log.e(TAG, "sendCmmandRead command:" + Arrays.toString(byteValues));
        try {
            characteristic = mGatt.getService(Utility.RX_SERVICE_UUID).getCharacteristic(Utility.RX_CHAR_UUID);
        } catch (Exception e) {
            Log.e(TAG, "Exception ::" + e.toString());
            return;
        }
        characteristic.setValue(byteValues);
        mGatt.writeCharacteristic(characteristic);
    }

    public void sendCommandWrite(int index, ArrayList<Integer> data) {
        mGattType = index;
        BluetoothGattCharacteristic characteristic;
        byte[] byteValues = {};
        byte[] currentByte = {};
        byte[] currentByte1 = {};
        byte[] cali1 = {};
        byte[] cali2 = {};
        byte[] cali3 = {};
        byte[] cali4 = {};
        byte[] cali5 = {};
        switch (index) {
            case 2:
                byte[] bytes = Utility.convertLittleEndian(Integer.parseInt(data.get(0).toString()));
                byteValues = new byte[]{Utility.GATT_COMMAND_STX, 0x02, bytes[0], bytes[1]};//, Byte.parseByte(Integer.toString(checkSum))};
                break;
            case 4:
                currentByte = Utility.convertLittleEndian(Integer.parseInt(data.get(0).toString()));
                currentByte1 = Utility.convertLittleEndian(Integer.parseInt(data.get(1).toString()));
                byteValues = new byte[]{Utility.GATT_COMMAND_STX, 0x04,
                        currentByte[0], currentByte[1],
                        currentByte1[0], currentByte1[1]};//, Byte.parseByte(Integer.toString(checkSum))};
                break;
            case 6:
                byteValues = new byte[]{Utility.GATT_COMMAND_STX, 0x06,
                        Byte.parseByte(data.get(0).toString()),
                        Byte.parseByte(data.get(1).toString())};//, Byte.parseByte(Integer.toString(checkSum))};
                break;
            case 10:
            case 13:
                cali1 = Utility.convertLittleEndian(Integer.parseInt(data.get(0).toString()));
                cali2 = Utility.convertLittleEndian(Integer.parseInt(data.get(1).toString()));
                cali3 = Utility.convertLittleEndian(Integer.parseInt(data.get(2).toString()));
                cali4 = Utility.convertLittleEndian(Integer.parseInt(data.get(3).toString()));
                cali5 = Utility.convertLittleEndian(Integer.parseInt(data.get(4).toString()));
                if (index == 10) {
                    byteValues = new byte[]{Utility.GATT_COMMAND_STX, 0x0A,
                            cali1[0], cali1[1],
                            cali2[0], cali2[1],
                            cali3[0], cali3[1],
                            cali4[0], cali4[1],
                            cali5[0], cali5[1]};//,Byte.parseByte(Integer.toString(checkSum))};
                } else {
                    byteValues = new byte[]{Utility.GATT_COMMAND_STX, 0x0D,
                            cali1[0], cali1[1],
                            cali2[0], cali2[1],
                            cali3[0], cali3[1],
                            cali4[0], cali4[1],
                            cali5[0], cali5[1]};//,Byte.parseByte(Integer.toString(checkSum))};
                }
                break;
            case 11:
            case 14:
                if (index == 11) {
                    byteValues = new byte[]{Utility.GATT_COMMAND_STX, 0x0B,
                            Byte.parseByte(data.get(0).toString())};//, Byte.parseByte(Integer.toString(checkSum))};
                } else {
                    byteValues = new byte[]{Utility.GATT_COMMAND_STX, 0x0E,
                            Byte.parseByte(data.get(0).toString())};//, Byte.parseByte(Integer.toString(checkSum))};
                }
                break;
        }
        Log.e(TAG, "sendCommandWrite command:" + Arrays.toString(byteValues));
        try {
            characteristic = mGatt.getService(Utility.RX_SERVICE_UUID).getCharacteristic(Utility.RX_CHAR_UUID);
        } catch (Exception e) {
            Log.e(TAG, "Exception ::" + e.toString());
            return;
        }
        characteristic.setValue(byteValues);
        mGatt.writeCharacteristic(characteristic);
    }

    public void disconnectGatt() {
        // Log.e(TAG, "ConnectionApi : disconnectGatt");
        if (mGatt != null) {
            mGatt.disconnect();
            mGatt.close();
            mGatt = null;
            mGattConnected = false;
            mListener.onFailed();
        }
    }

    private void enableCmdReceiveToGatt() {
        BluetoothGattService RxService = mGatt.getService(Utility.RX_SERVICE_UUID);
        if (RxService == null) {
            Log.i(TAG, "Rx service not found!");
            return;
        }
        BluetoothGattCharacteristic TxChar = RxService.getCharacteristic(Utility.TX_CHAR_UUID);
        if (TxChar == null) {
            Log.i(TAG, "Tx charateristic not found!");
            return;
        }
        mGatt.setCharacteristicNotification(TxChar, true);
        BluetoothGattDescriptor descriptor = TxChar.getDescriptor(Utility.CCCD);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        mGatt.writeDescriptor(descriptor);
    }

    private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, final int status, int newState) {
            Log.e(TAG, "status, newState = " + status + ", " + newState);
            if (status == 0 && newState == 2) {
                // 연결성공
                mGattConnected = true;
                mGatt.discoverServices();
            } else {
                disconnectGatt();
                connectGatt();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.i(TAG, "onServicesDiscovered : ");
            BluetoothGattService mBluetoothGattService = mGatt.getService(Utility.RX_SERVICE_UUID);
            if (gatt != null) {
                Log.i(TAG, "Service characteristic UUID found: " + mBluetoothGattService.getUuid().toString());
            } else {
                Log.i(TAG, "Service characteristic not found for UUID: " + Utility.RX_SERVICE_UUID);
            }
            enableCmdReceiveToGatt();
        }

        /**
         * 누락데이터 일때 비지니스 로직에서 연결종료 시키기
         * @param gatt
         * @param characteristic
         */
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            byte[] rxBytes = characteristic.getValue();
            if (Utility.TX_CHAR_UUID.equals(characteristic.getUuid())) {
                Log.e(TAG, "onCharacteristicChanged - uuid : " + characteristic.getUuid());
                mListener.onLoaded(mGattType, rxBytes, gatt);
                if (mGattType != 2) {
                    disconnectGatt();
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.i(TAG, "onCharacteristicRead - status : " + status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            Log.i(TAG, "onDescriptorWrite - status : " + status);
            mGattConnected = true;
            mListener.onConnect();
        }
    };

    private BluetoothDevice getDeviceGatt() {
        BluetoothAdapter bluetoothAdapter;
        final BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        BluetoothDevice device;
        if (BluetoothAdapter.checkBluetoothAddress(mDeviceMac.trim())) {
            device = bluetoothAdapter.getRemoteDevice(mDeviceMac.trim());
            return device;
        } else {
            mGattConnected = false;
            return null;
        }
    }
}