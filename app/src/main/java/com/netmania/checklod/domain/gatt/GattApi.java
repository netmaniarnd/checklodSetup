package com.netmania.checklod.domain.gatt;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.netmania.checklod.data.vo.DeviceVo;
import com.netmania.checklod.domain.listener.GattListener;
import com.netmania.checklod.domain.listener.GattReader;
import com.netmania.checklod.domain.listener.LoaderListener;
import com.netmania.checklod.domain.listener.OffsetListener;
import com.netmania.checklod.util.Utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import okio.ByteString;

public class GattApi {

    private static final String TAG = GattApi.class.getSimpleName();
    private Context mContext;
    private DeviceVo mLogger;
    private ArrayList<Integer> mData;
    private String mApiTag;
    private BluetoothGatt mGatt;
    private GattReader mGattReader = null;
    private Boolean mGattConnected = false;
    private GattListener mNewGattCallback;

    private Boolean mRoofFlag = false;
    private int mRoofIndex = 0;

    // 0-16 번지 데이터 세팅

    public GattApi(Context context, DeviceVo logger, GattListener newGattCallback) {
        mContext = context;
        mLogger = logger;
        mNewGattCallback = newGattCallback;
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
        try {
            mGattReader.connectDevice(mGatt, loaderCallback); // W/System.err101
        } catch (Exception e) {
            e.printStackTrace();
            mGattReader = null;
            return;
        }
    }

    public void sendRoof() {
        mRoofFlag = true;
        BluetoothGattCharacteristic characteristic;
        byte[] byteValues = {};
        Log.e(TAG, "mRoofIndex : " + mRoofIndex);
        if (mRoofIndex == 500) {
            mRoofFlag = false;
        } else {
            byte[] bytes = Utility.convertLittleEndian(230);
            byteValues = new byte[]{Utility.GATT_COMMAND_STX, 0x02, bytes[0], bytes[1], 0x02};
            try {
                Log.e(TAG, Utility.TX_CHAR_UUID.toString());
                // RX_SERVICE_UUID
                characteristic = mGatt.getService(Utility.RX_SERVICE_UUID).getCharacteristic(Utility.RX_CHAR_UUID);
            } catch (Exception e) {
                Log.e(TAG, "Exception ::" + e.toString());
                return;
            }
            characteristic.setValue(byteValues);
            mGatt.writeCharacteristic(characteristic);
            mRoofIndex++;
        }
    }

    public void sendCommandRead(int index) {
        mRoofFlag = false;
        BluetoothGattCharacteristic characteristic;
        // byte[] byteValues = {0x63, 0x01, 0x01};
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
                // TODO : 확인
                // Calendar 참조
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
            Log.e(TAG, Utility.TX_CHAR_UUID.toString());
            // RX_SERVICE_UUID
            characteristic = mGatt.getService(Utility.RX_SERVICE_UUID).getCharacteristic(Utility.RX_CHAR_UUID);
        } catch (Exception e) {
            Log.e(TAG, "Exception ::" + e.toString());
            return;
        }
        characteristic.setValue(byteValues);
        mGatt.writeCharacteristic(characteristic);
    }

    public void sendCommandWrite(int index, ArrayList<Integer> data) {
        mRoofFlag = false;
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
                // todo : 128 에러
                int val1 = Integer.parseInt(data.get(0).toString());
                int val2 = Integer.parseInt(data.get(1).toString());
                val1 = Math.min(val1, 127);
                val2 = Math.min(val2, 127);
                byteValues = new byte[]{com.netmania.ble.util.Utility.GATT_COMMAND_STX, 0x06,
                        (byte) val1,
                        (byte) val2};//, Byte.parseByte(Integer.toString(checkSum))};
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
            Log.e(TAG, Utility.TX_CHAR_UUID.toString());
            // RX_SERVICE_UUID
            characteristic = mGatt.getService(Utility.RX_SERVICE_UUID).getCharacteristic(Utility.RX_CHAR_UUID);
        } catch (Exception e) {
            Log.e(TAG, "Exception ::" + e.toString());
            return;
        }
        characteristic.setValue(byteValues);
        mNewGattCallback.onRequest(byteValues);
        mGatt.writeCharacteristic(characteristic);
    }

    public static byte u2b(int in) {
        if(in>127) {
            return (byte)(in - 256);
        } else {
            return (byte)in;
        }
    }

    private void resetGatt(int status) {
        if (status == 133) {
            //refreshDeviceCache(gatt);
        }
        mGatt.close();
        mGatt = null;
        connectGatt();
    }

    public void disconnectGatt() {
        if (getGattConnected()) {
            mGatt.disconnect();
            mGatt.close();
            mGatt = null;
            mGattConnected = false;
        }
    }

    private void enableCmdReceveToGatt() {
        BluetoothGattService RxService = mGatt.getService(Utility.RX_SERVICE_UUID);
        if (RxService == null) {
            Log.i(TAG, "Rx service not found!");
            //broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_UART);
            return;
        }

        BluetoothGattCharacteristic TxChar = RxService.getCharacteristic(Utility.TX_CHAR_UUID);
        if (TxChar == null) {
            Log.i(TAG, "Tx charateristic not found!");
            //broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_UART);
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
                startGattDiscover();
            } else if (status == 0 && newState == 0) {
                // 연결종료
                loaderCallback.onFailed(status, newState);
                disconnectGatt();
            } else {
                loaderCallback.onFailed(status, newState);
                disconnectGatt();
            }
            if (newState == BluetoothProfile.STATE_CONNECTED) {  // 2
                startGattDiscover();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {   // 0
                disconnectGatt();
                resetGatt(status);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.e(TAG, "onServicesDiscovered");
            Log.e(TAG, "1 in : status  --> " + status + " TYPE :::  " + gatt.getDevice().getType());
            BluetoothGattService mBluetoothGattService = mGatt.getService(Utility.RX_SERVICE_UUID);
            if (gatt != null) {
                Log.e(TAG, "Service characteristic UUID found: " + mBluetoothGattService.getUuid().toString());
            } else {
                Log.e(TAG, "Service characteristic not found for UUID: " + Utility.RX_SERVICE_UUID);
            }
            enableCmdReceveToGatt();
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            byte[] rxBytes = characteristic.getValue();
            if (Utility.TX_CHAR_UUID.equals(characteristic.getUuid())) {
                Log.e(TAG, "onCharacteristicChanged - uuid=" + characteristic.getUuid());
                if (mRoofFlag) {
                    sendRoof();
                }
                mNewGattCallback.onLoaded(rxBytes, gatt);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            Log.e(TAG, "onCharacteristicRead - status=" + status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt,
                                      BluetoothGattDescriptor descriptor, int status) {
            Log.e(TAG, "onDescriptorWrite - status=" + status);
            if (!mGattConnected) mGattConnected = true;
        }
    };

    private void startGattDiscover() {
        Log.e(TAG, "startGattDiscover:");
        mGatt.discoverServices();
    }

    private LoaderListener loaderCallback = new LoaderListener() {
        @Override
        public void onLoaded(final long count) {
            Log.d(TAG, String.format("loaderCallback : %d", count));
        }

        @Override
        public void onFailed(final int status, final int newStatus) {
            mGattConnected = false;
        }
    };

    public void sendTempRead(DeviceVo logger, String tag) {
        mLogger = logger;
        mApiTag = tag;
        BluetoothGattCharacteristic characteristic;
        try {
            Log.e(TAG, Utility.RX_CHAR_UUID.toString());
            characteristic = mGatt.getService(Utility.RX_SERVICE_UUID).getCharacteristic(Utility.RX_CHAR_UUID);
        } catch (Exception e) {
            Log.e(TAG, "Exception ::" + e.toString());
            return;
        }
        Log.e(TAG, tag);
        /*byte[] sendTag = mApiTag.getBytes();
        int tParam = 0;
        byte[] params = ByteBuffer.allocate(4).putInt(tParam).array(); // security code
        byte[] byteValues = {sendTag[0], sendTag[1]}; // , params[0]};*/
        byte[] byteValues = new byte[]{Utility.GATT_COMMAND_STX, 0x09};
        Log.e(TAG, "byteValues command:" + Utility.byteArrayToHex(byteValues));
        characteristic.setValue(byteValues);
        mGatt.writeCharacteristic(characteristic);
    }

    public void sendTempWrite(DeviceVo logger, String tag, ArrayList<Integer> data) {
        /*
        조정 범위  -128 ~ +127 (*10 해서 전달)
        온도조정범위 : -12.8 ~  +12.7
        */
        mLogger = logger;
        mData = data;
        mApiTag = tag;
        BluetoothGattCharacteristic characteristic;
        try {
            Log.e(TAG, Utility.RX_CHAR_UUID.toString());
            characteristic = mGatt.getService(Utility.RX_SERVICE_UUID).getCharacteristic(Utility.RX_CHAR_UUID);
        } catch (Exception e) {
            Log.e(TAG, "Exception ::" + e.toString());
            return;
        }
        Log.e(TAG, tag);
        /*byte[] sendTag = mApiTag.getBytes();
        byte[] params1 = ByteBuffer.allocate(4).putInt(mData.get(0)).array();
        byte[] params2 = ByteBuffer.allocate(4).putInt(mData.get(1)).array();
        byte[] params3 = ByteBuffer.allocate(4).putInt(mData.get(2)).array();
        byte[] params4 = ByteBuffer.allocate(4).putInt(mData.get(3)).array();
        byte[] params5 = ByteBuffer.allocate(4).putInt(mData.get(4)).array();
        byte[] byteValues = {sendTag[0], sendTag[1],
                params1[params1.length-2],params1[params1.length-1],
                params2[params2.length-2],params2[params2.length-1],
                params3[params3.length-2],params3[params3.length-1],
                params4[params4.length-2],params4[params4.length-1],
                params5[params5.length-2],params5[params5.length-1]
        };*/
        byte[] cali1 = {};
        byte[] cali2 = {};
        byte[] cali3 = {};
        byte[] cali4 = {};
        byte[] cali5 = {};
        byte[] byteValues = {};
        cali1 = Utility.convertLittleEndian(Integer.parseInt(data.get(0).toString()));
        cali2 = Utility.convertLittleEndian(Integer.parseInt(data.get(1).toString()));
        cali3 = Utility.convertLittleEndian(Integer.parseInt(data.get(2).toString()));
        cali4 = Utility.convertLittleEndian(Integer.parseInt(data.get(3).toString()));
        cali5 = Utility.convertLittleEndian(Integer.parseInt(data.get(4).toString()));
        byteValues = new byte[]{Utility.GATT_COMMAND_STX, 0x0A,
                cali1[0], cali1[1],
                cali2[0], cali2[1],
                cali3[0], cali3[1],
                cali4[0], cali4[1],
                cali5[0], cali5[1]};//,Byte.parseByte(Integer.toString(checkSum))};
        Log.e(TAG, "byteValues command:" + Utility.byteArrayToHex(byteValues));
        characteristic.setValue(byteValues);
        mGatt.writeCharacteristic(characteristic);
    }

    private BluetoothDevice getDeviceGatt() {
        BluetoothAdapter bluetoothAdapter;
        final BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        BluetoothDevice device;
        if (BluetoothAdapter.checkBluetoothAddress(mLogger.getMacAddress().trim())) {
            device = bluetoothAdapter.getRemoteDevice(mLogger.getMacAddress().trim());
            return device;
        } else {
            mGattConnected = false;
            return null;
        }
    }
}
