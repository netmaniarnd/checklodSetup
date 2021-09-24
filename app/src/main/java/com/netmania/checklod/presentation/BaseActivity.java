package com.netmania.checklod.presentation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.netmania.checklod.BaseApplication;
import com.netmania.checklod.data.repository.AppDataBase;
import com.netmania.checklod.domain.viewmodel.db.DeviceLogViewModel;
import com.netmania.checklod.domain.viewmodel.db.DeviceViewModel;
import com.netmania.checklod.domain.viewmodel.db.LogViewModel;
import com.netmania.checklod.domain.viewmodel.db.TripViewModel;

import java.util.ArrayList;
import java.util.Arrays;

public class BaseActivity extends AppCompatActivity {

    public static String TAG = BaseActivity.class.getSimpleName();
    public Context mContext;
    public ArrayList<String> list = new ArrayList<>(Arrays.asList("Time Setup", "Mass Device", "Device interface", "Device Offset", "Sequence Load"));

    public static final String EXTRA_MAC = "macAddress";

    public static AppDataBase mDb;

    public static TripViewModel mTripViewModel;
    public static DeviceLogViewModel mDeviceLogViewModel;
    public static LogViewModel mLogViewModel;
    public static DeviceViewModel mDeviceViewModel;

    private ViewModelProvider.AndroidViewModelFactory mViewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDb = BaseApplication.mDb;
        mContext = this;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getIntent().addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        if (mViewModelFactory == null) {
            mViewModelFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication());
        }
        mTripViewModel = new ViewModelProvider(this, mViewModelFactory).get(TripViewModel.class);
        mDeviceLogViewModel = new ViewModelProvider(this, mViewModelFactory).get(DeviceLogViewModel.class);
        mDeviceViewModel = new ViewModelProvider(this, mViewModelFactory).get(DeviceViewModel.class);
        mLogViewModel = new ViewModelProvider(this, mViewModelFactory).get(LogViewModel.class);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
