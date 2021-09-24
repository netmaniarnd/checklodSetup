package com.netmania.checklod.util;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.BatteryManager;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;

import java.util.UUID;

public class SystemUtil {
    private static final String TAG = SystemUtil.class.getSimpleName();

    public static final int NETWORK_STATUS_NONE = 0;
    public static final int NETWORK_STATUS_WIFI = 1;
    public static final int NETWORK_STATUS_MOBILE = 2;

    //네트워크 상태
    public static boolean isNetworkAvaliable(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        /*
        Log.i(TAG, "[NETWORK_INFO] 네트워크 타입 : " + wifi.getTypeName());
        Log.i(TAG, "[NETWORK_INFO] 와이파이 연결 : " + wifi.isConnected ());
        Log.i(TAG, "[NETWORK_INFO] 통신사 연결 : " + mobile.isConnected ());
        */
        return (wifi.isConnected() || mobile.isConnected());
    }

    public static int getNetworkStatus(Context context) {
        int result = 0;
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        android.net.NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		/*
	    Log.i(TAG, "[NETWORK_INFO] 네트워크 타입 : " + wifi.getTypeName());
	    Log.i(TAG, "[NETWORK_INFO] 와이파이 연결 : " + wifi.isConnected ());
	    Log.i(TAG, "[NETWORK_INFO] 통신사 연결 : " + mobile.isConnected ());
	    */
        if (wifi.isConnected()) {
            result = NETWORK_STATUS_WIFI;
        } else if (mobile.isConnected()) {
            result = NETWORK_STATUS_MOBILE;
        } else {
            result = NETWORK_STATUS_NONE;
        }
        return result;
    }

    public static double getAvailableSpaceInMB() {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long bytesAvailable;
        bytesAvailable = (long)stat.getBlockSize() * (long)stat.getAvailableBlocks();
        double available = (bytesAvailable / (1024 * 1024)) / 1024.0;
        return available;
    }

    public static int getUsedMemoryRatio(ActivityManager manager) {
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        manager.getMemoryInfo(memoryInfo);
        int available = (int) (memoryInfo.availMem / (1024 * 1024));
        return available;
    }

    public static int getBattery(Context context) {
        BatteryManager bm = (BatteryManager) context.getSystemService(context.BATTERY_SERVICE);
        int batLevel = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        }
        return batLevel;
    }

    @SuppressLint({"MissingPermission", "HardwareIds"})
    public static String getPhoneNumber(Context context) {
        TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String telPhoneNo = "";
        try {
            telPhoneNo = telephony.getLine1Number(); //--> 권한 체크 앱 시작시 한다.
            telPhoneNo = telPhoneNo.replace("+82", "0");
        } catch (Exception e) {
            //테스트 용 ---------------------------------------------------------------------------
            telPhoneNo = "*";
            telPhoneNo = "01063279830";
        }
        if ("*".equals(telPhoneNo)) {
            final String tmDevice, tmSerial, androidId;
            tmDevice = "" + telephony.getDeviceId();
            tmSerial = "" + telephony.getSimSerialNumber();
            androidId = "" + android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
            String deviceId = deviceUuid.toString();
        }
        // telPhoneNo = "01086823688";
        // Log.e(TAG, "telPhoneNo : " + telPhoneNo);
        return telPhoneNo;
    }

    public static String getAppVersion(Context context) {
        String version = "";
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    public static int getVersionNum(String strVersion) {
        int verNo = 0;
        String[] v = strVersion.split(" ");
        if (v.length > 1) { //베타나 알파 스트링이 앞에 붙은것으로 간주
            // verNo = (v[0].equals("Alpha")) ? 1000 : 2000; //Alpha 버전은 1000번대 Beta 버전은 2000번대.
            verNo += Integer.parseInt(v[1].replaceAll("\\.", ""));
        } else {
            verNo += Integer.parseInt(v[0].replaceAll("\\.", ""));
        }
        return verNo;
    }

    public static ActivityManager.RunningServiceInfo getRunnsingServiceInstance (Context context, Class<?> cls) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (cls.getName().equals(service.service.getClassName())) {
                return service;
            }
        }
        return null;
    }

}
