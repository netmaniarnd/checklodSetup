package com.netmania.checklod.presentation.activity;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.os.Bundle;

import com.netmania.checklod.R;
import com.netmania.checklod.databinding.ActivityDeviceListBinding;
import com.netmania.checklod.domain.listener.DeviceListClickListener;
import com.netmania.checklod.presentation.BaseActivity;
import com.netmania.checklod.presentation.adapter.DeviceAdapter;

import lombok.SneakyThrows;

public class DeviceListActivity extends BaseActivity {

    public static String TAG = DeviceListActivity.class.getSimpleName();
    private int mMenuIndex = 0;
    private DeviceAdapter mDeviceAdapter;
    private ActivityDeviceListBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_device_list);
        mMenuIndex = getIntent().getIntExtra("title", 0);
        setTitle(list.get(mMenuIndex));
        init();
    }

    @SneakyThrows
    private void init() {
        mDeviceAdapter = new DeviceAdapter(DeviceListActivity.this, mDeviceListClickListener);
        mBinding.rvDevice.setAdapter(mDeviceAdapter);
        mBinding.rvDevice.setLayoutManager(new GridLayoutManager(this, 1));
        mTripViewModel.getAllTrip().observe(DeviceListActivity.this, tripEntities -> {
            mDeviceAdapter.submitList(tripEntities);
        });
    }

    DeviceListClickListener mDeviceListClickListener = (index, mac) -> {
        // TODO : title index로 예외처리
        Intent intent;
        switch (mMenuIndex) {
            case 2:
                intent = new Intent(getApplicationContext(), InterfaceActivity.class);
                intent.putExtra(EXTRA_MAC, mac);
                startActivity(intent);
                break;
            case 3:
                intent = new Intent(getApplicationContext(), OffsetActivity.class);
                intent.putExtra(EXTRA_MAC, mac);
                startActivity(intent);
                break;
            case 4:
                intent = new Intent(getApplicationContext(), DataSyncActivity.class);
                intent.putExtra(EXTRA_MAC, mac);
                startActivity(intent);
                break;
            default:
                break;
        }
    };
}