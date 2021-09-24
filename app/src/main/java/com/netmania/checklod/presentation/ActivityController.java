package com.netmania.checklod.presentation;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import com.netmania.checklod.data.constant.IntentVo;
import com.netmania.checklod.data.vo.TemperatureRangeItemVo;
import com.netmania.checklod.presentation.activity.InterfaceActivity;
import com.netmania.checklod.presentation.activity.DeviceListActivity;
import com.netmania.checklod.presentation.activity.MultiDeviceActivity;
import com.netmania.checklod.presentation.activity.MultiDeviceTempActivity;
import com.netmania.checklod.presentation.activity.ReportActivity;

public class ActivityController {

    public static void openBluetoothSettingActivity(Context context) {
        Intent tIntent = new Intent();
        tIntent.setAction(Settings.ACTION_BLUETOOTH_SETTINGS);
        context.startActivity(tIntent);
    }

    public static void openDeviceListActivity(Context context, String mac) {
        Intent tIntent = new Intent(context, DeviceListActivity.class);
        // tIntent.putExtra(ConstantUtil.EXTRA_DEVICEVO, mac);
        context.startActivity(tIntent);
    }

    public static void openMultiDeviceActivity(Context context, TemperatureRangeItemVo vo) {
        Intent tIntent = new Intent(context, MultiDeviceActivity.class);
        // tIntent.putExtra(ConstantUtil.EXTRA_TEMPVO, vo);
        context.startActivity(tIntent);
    }

    public static void openMultiDeviceTempActivity(Context context, TemperatureRangeItemVo vo) {
        Intent tIntent = new Intent(context, MultiDeviceTempActivity.class);
        tIntent.putExtra(IntentVo.EXTRA_TEMPVO, vo);
        context.startActivity(tIntent);
    }

    public static void openConnectionActivity(Context context, TemperatureRangeItemVo vo) {
        Intent tIntent = new Intent(context, InterfaceActivity.class);
        // tIntent.putExtra(ConstantUtil.EXTRA_TEMPVO, vo);
        context.startActivity(tIntent);
    }

    public static void openReportActivity(Context context, String mac) {
        Intent tIntent = new Intent(context, ReportActivity.class);
        tIntent.putExtra(IntentVo.EXTRA_DEVICEVO, mac);
        context.startActivity(tIntent);
    }

}
