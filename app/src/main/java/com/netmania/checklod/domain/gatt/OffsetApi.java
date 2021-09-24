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
import com.netmania.checklod.domain.listener.GattReader;
import com.netmania.checklod.domain.listener.LoaderListener;
import com.netmania.checklod.domain.listener.OffsetListener;
import com.netmania.checklod.presentation.activity.OffsetActivity;
import com.netmania.checklod.util.Utility;

import java.util.ArrayList;

public class OffsetApi {

    private static final String TAG = GattApi.class.getSimpleName();
    private OffsetActivity mContext;
    private DeviceVo mDeviceVo;
    private ArrayList<Integer> mData;
    private String mApiTag;
    private BluetoothGatt mGatt;
    private GattReader mGattReader = null;
    private Boolean mGattConnected = false;
    private OffsetListener mGattCallback;

    // 0-16 번지 데이터 세팅

    public OffsetApi(OffsetActivity context, DeviceVo logger, OffsetListener gattCallback) {
        mContext = context;
        mDeviceVo = logger;
        mGattCallback = gattCallback;
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
        mGatt = null;
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

    public void sendTempRead(DeviceVo logger, String tag) {
        mDeviceVo = logger;
        mApiTag = tag;
        BluetoothGattCharacteristic characteristic;
        try {
            Log.e(TAG, Utility.RX_CHAR_UUID.toString());
            characteristic = mGatt.getService(Utility.RX_SERVICE_UUID).getCharacteristic(Utility.RX_CHAR_UUID);
        } catch(Exception e) {
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

    public void sendTempWrite(DeviceVo vo, String tag, ArrayList<Integer> data) {
        /*
        조정 범위  -128 ~ +127 (*10 해서 전달)
        온도조정범위 : -12.8 ~  +12.7
        */
        mDeviceVo = vo;
        mData = data;
        mApiTag = tag;
        BluetoothGattCharacteristic characteristic;
        try {
            Log.e(TAG, Utility.RX_CHAR_UUID.toString());
            characteristic = mGatt.getService(Utility.RX_SERVICE_UUID).getCharacteristic(Utility.RX_CHAR_UUID);
        } catch(Exception e) {
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

    public void disconnectGatt() {
        if (mGatt != null) {
            mGatt.close();
            mGatt = null;
            mGattConnected = false;
        }
    }

    private void enableCmdReceveToGatt () {
        BluetoothGattService RxService = mGatt.getService(Utility.RX_SERVICE_UUID);
        if (RxService == null) {
            Log.i (TAG, "Rx service not found!");
            //broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_UART);
            return;
        }
        BluetoothGattCharacteristic TxChar = RxService.getCharacteristic(Utility.TX_CHAR_UUID);
        if (TxChar == null) {
            Log.i (TAG, "Tx charateristic not found!");
            //broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_UART);
            return;
        }
        mGatt.setCharacteristicNotification(TxChar,true);
        BluetoothGattDescriptor descriptor = TxChar.getDescriptor(Utility.CCCD);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        mGatt.writeDescriptor(descriptor);
    }

    private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, final int status, int newState) {
            //Log.d(TAG, Utility.getThreadSignature());
            Log.e(TAG, "status, newState = " + status + ", " + newState);
            if (status == 0 && newState == 2) {
                // 연결성공
                mGattConnected = true;
                // TODO : Listener변경
                // BusProvider.getInstance().post("Connection Success");
            } else if (status == 0 && newState == 0) {
                // 연결종료
            } else {
                loaderCallback.onFailed(status, newState);
            }
            if (newState == BluetoothProfile.STATE_CONNECTED){  // 2
                startGattDiscover();
                mGattCallback.onConnection();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {   // 0
                disconnectGatt();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status){
            Log.e(TAG, "onServicesDiscovered");
            Log.e(TAG, "1 in : status  --> " + status + " TYPE :::  " + gatt.getDevice().getType());
            enableCmdReceveToGatt();
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            // RSP  : CR 온도1 온도2 온도3 온도4 온도5   (온도1 : 1Byte)
            // RSP  : CW 온도1 온도2 온도3 온도4 온도5
            //Log.d(TAG, Utility.getThreadSignature());
            byte[] rxBytes = characteristic.getValue();
            Log.e (TAG, "onCharacteristicChanged - uuid=" + characteristic.getUuid());
            Log.e (TAG, "onCharacteristicChanged - value="  + Utility.byteArrayToHex(rxBytes));
            String tag = new String(new byte[]{rxBytes[0], rxBytes[1]});
            Integer tempVal1, tempVal2, tempVal3, tempVal4, tempVal5;
            tempVal1 = Utility.convertTemp(rxBytes, 2);
            tempVal2 = Utility.convertTemp(rxBytes, 4);
            tempVal3 = Utility.convertTemp(rxBytes, 6);
            tempVal4 = Utility.convertTemp(rxBytes, 8);
            tempVal5 = Utility.convertTemp(rxBytes, 10);
            ArrayList<Integer> tempReadList = new ArrayList<>();
            tempReadList.add(tempVal1);
            tempReadList.add(tempVal2);
            tempReadList.add(tempVal3);
            tempReadList.add(tempVal4);
            tempReadList.add(tempVal5);
            Log.e (TAG, "tempReadList - value="  + tempReadList);
            if(Utility.TX_CHAR_UUID.equals(characteristic.getUuid())) {
                mGattCallback.onLoaded(tempReadList);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            Log.e (TAG, "onCharacteristicRead - status=" + status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt,
                                      BluetoothGattDescriptor descriptor, int status) {
            Log.e (TAG, "onDescriptorWrite - status=" + status);
            if(! mGattConnected) mGattConnected = true;
        }
    };

    private void startGattDiscover() {
        Log.e (TAG, "startGattDiscover:");
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

    private BluetoothDevice getDeviceGatt() {
        BluetoothAdapter bluetoothAdapter;
        final BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        BluetoothDevice device;
        if (BluetoothAdapter.checkBluetoothAddress(mDeviceVo.getMacAddress().trim())) {
            device = bluetoothAdapter.getRemoteDevice(mDeviceVo.getMacAddress().trim());
            return device;
        } else {
            mGattConnected = false;
            return null;
        }
    }
}
