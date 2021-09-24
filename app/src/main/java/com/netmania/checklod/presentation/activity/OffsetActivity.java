package com.netmania.checklod.presentation.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.netmania.checklod.R;
import com.netmania.checklod.data.vo.DeviceVo;
import com.netmania.checklod.databinding.ActivityOffsetBinding;
import com.netmania.checklod.domain.gatt.OffsetApi;
import com.netmania.checklod.domain.listener.OffsetListener;
import com.netmania.checklod.presentation.BaseActivity;
import com.netmania.checklod.util.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class OffsetActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String READ_DATA = "CR";
    private static final String WRITE_DATA = "CW";

    private ActivityOffsetBinding mBinding;

    public Handler mHandler;
    public static final long SCAN_PERIOD = 1000 * 10;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;

    private OffsetApi mGattApi;
    private JsonArray mDeviceAlias;

    private String mMac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_offset);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_offset);

        mBinding.btnSelectSticker.setOnClickListener(this);
        mBinding.btnLoggerlist.setOnClickListener(this);
        mBinding.btnConnectGatt.setOnClickListener(this);
        mBinding.btnDisconnectGatt.setOnClickListener(this);
        mBinding.btnRead.setOnClickListener(this);
        mBinding.btnWrite.setOnClickListener(this);

        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        mMac = getIntent().getStringExtra(EXTRA_MAC);
        mBinding.macaddress.setText(mMac);
        getDeviceAlias();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mGattApi != null) {
            mGattApi.disconnectGatt();
        }
    }

    private void getDeviceAlias() {
        String json = Utility.getJsonFromAssets(getApplicationContext(), "device.json");// assets/device.json
        Gson gson = new Gson();
        mDeviceAlias = gson.fromJson(json, JsonArray.class);
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private long mLastClickTime = 0;
    private ArrayList<Integer> mResponseData;
    private OffsetListener loaderCallback = new OffsetListener() {
        @Override
        public void onConnection() {
            Toast.makeText(OffsetActivity.this, "Device Connection!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onLoaded(ArrayList<Integer> response) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            mResponseData = response;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mBinding.readOffset1.setText(Double.toString(mResponseData.get(0) / 100.0));
                    mBinding.readOffset2.setText(Double.toString(mResponseData.get(1) / 100.0));
                    mBinding.readOffset3.setText(Double.toString(mResponseData.get(2) / 100.0));
                    mBinding.readOffset4.setText(Double.toString(mResponseData.get(3) / 100.0));
                    mBinding.readOffset5.setText(Double.toString(mResponseData.get(4) / 100.0));

                    mBinding.offset1.setText(Integer.toString(mResponseData.get(0)));
                    mBinding.offset2.setText(Integer.toString(mResponseData.get(1)));
                    mBinding.offset3.setText(Integer.toString(mResponseData.get(2)));
                    mBinding.offset4.setText(Integer.toString(mResponseData.get(3)));
                    mBinding.offset5.setText(Integer.toString(mResponseData.get(4)));
                    notiComplete();
                }
            });
            Log.e(TAG, "tempList.size :" + response.size());
        }
    };

    private void notiComplete() {
        Toast.makeText(this, "Gatt Complete!", Toast.LENGTH_SHORT).show();
    }

    private void selectLogger() {
        DeviceVo logger = getLoggerDto(mBinding.macaddress.getText().toString());
        Log.e(TAG, "logger info : " + logger);
        if (logger.getMacAddress().equals("")) {
            Toast.makeText(this, "Please select another stickerNum", Toast.LENGTH_SHORT).show();
        }
        mBinding.macaddress.setText(logger.getMacAddress());
    }

    private DeviceVo getLoggerDto(String macAddress) {
        DeviceVo loggerDto = new DeviceVo();
        for (int i = 0; i < mDeviceAlias.size(); i++) {
            JsonObject obj = mDeviceAlias.get(i).getAsJsonObject();
            if (macAddress.toUpperCase().equals(obj.get("id").getAsString())) {
                loggerDto.setAlias(obj.get("alias").getAsString());
                loggerDto.setMacAddress(obj.get("id").getAsString());
            }
        }
        return loggerDto;
    }

    private ArrayList<Integer> getTempArray() {
        ArrayList<Integer> setTempList = new ArrayList<Integer>();
        setTempList.add(Integer.parseInt(mBinding.offset1.getText().toString()));
        setTempList.add(Integer.parseInt(mBinding.offset2.getText().toString()));
        setTempList.add(Integer.parseInt(mBinding.offset3.getText().toString()));
        setTempList.add(Integer.parseInt(mBinding.offset4.getText().toString()));
        setTempList.add(Integer.parseInt(mBinding.offset5.getText().toString()));
        return setTempList;
    }

    @Override
    public void onClick(View v) {
        DeviceVo logger = getLoggerDto(mBinding.macaddress.getText().toString());
        switch (v.getId()) {
            case R.id.btn_select_sticker:
                selectLogger();
                break;
            case R.id.btn_loggerlist:
                Intent intent = new Intent(OffsetActivity.this, LoggerListActivity.class);
                intent.putExtra("deviceList", mDeviceAlias.toString());
                startActivityForResult(intent, 100);
                break;
            case R.id.btn_connect_gatt:
                mGattApi = null;
                mGattApi = new OffsetApi(this, logger, loaderCallback);
                break;
            case R.id.btn_disconnect_gatt:
                if (mGattApi != null) {
                    mGattApi.disconnectGatt();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mBinding.offset1.setText("");
                        mBinding.offset2.setText("");
                        mBinding.offset3.setText("");
                        mBinding.offset4.setText("");
                        mBinding.offset5.setText("");
                    }
                });
                break;
            case R.id.btn_read:
                if (mGattApi == null) {
                    Toast.makeText(this, "Please Connect Gatt", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mGattApi.getGattConnected()) {
                    mGattApi.sendTempRead(logger, READ_DATA);
                } else {
                    Toast.makeText(this, "Please Connect Gatt", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_write:
                if (mBinding.offset1.getText().toString().equals("") || mBinding.offset2.getText().toString().equals("")
                        || mBinding.offset3.getText().toString().equals("") || mBinding.offset4.getText().toString().equals("")
                        || mBinding.offset5.getText().toString().equals("")) {

                    Toast.makeText(this, "empty Write offset value", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mGattApi.getGattConnected()) {
                    mGattApi.sendTempWrite(logger, WRITE_DATA, getTempArray());
                } else {
                    Toast.makeText(this, "Please Connect Gatt", Toast.LENGTH_SHORT).show();
                }
                long now = System.currentTimeMillis();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            if (requestCode == 100) {
                String result = data.getStringExtra("deviceList");
                if (!result.isEmpty()) {
                    try {
                        JSONObject obj = new JSONObject(result);
                        mBinding.stickerNum.setText(obj.get("alias").toString());
                        mBinding.macaddress.setText(obj.get("id").toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    mBinding.stickerNum.setText("");
                    mBinding.macaddress.setText("");
                }
                // Toast.makeText(this,"onActivityResult : " + result, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
