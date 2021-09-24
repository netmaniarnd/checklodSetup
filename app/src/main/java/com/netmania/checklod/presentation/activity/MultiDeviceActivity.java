package com.netmania.checklod.presentation.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.netmania.checklod.BaseApplication;
import com.netmania.checklod.R;
import com.netmania.checklod.data.vo.TemperatureRangeItemVo;
import com.netmania.checklod.databinding.ActivityMultiDeviceBinding;
import com.netmania.checklod.domain.listener.DeviceListClickListener;
import com.netmania.checklod.presentation.ActivityController;
import com.netmania.checklod.presentation.BaseActivity;
import com.netmania.checklod.presentation.adapter.MultiDeviceAdapter;
import com.netmania.checklod.util.DialogUtil;

import org.json.JSONObject;

import java.util.ArrayList;

public class MultiDeviceActivity extends BaseActivity implements View.OnClickListener {
    private String TAG = MainActivity.class.getSimpleName();

    private ActivityMultiDeviceBinding mBinding;
    private MultiDeviceAdapter mDeviceAdapter;
    private GridLayoutManager mLayoutManager;
    private int mLayoutCount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_multi_device);
        mBinding.btnInit.setOnClickListener(this);
        mBinding.btnTemp.setOnClickListener(this);
        mBinding.btnLayout.setOnClickListener(this);

        mDeviceAdapter = new MultiDeviceAdapter(this, beaconListClickListener);
        mLayoutManager = new GridLayoutManager(this, mLayoutCount);
        mLayoutManager.setItemPrefetchEnabled(false);
        mBinding.rvBeaconList.setAdapter(mDeviceAdapter);
        mBinding.rvBeaconList.setLayoutManager(mLayoutManager);
        mBinding.rvBeaconList.setNestedScrollingEnabled(false);

        mTripViewModel.getAllTrip().observe(this, deviceTripEntities -> {
            mDeviceAdapter.submitList(deviceTripEntities);
        });
    }

    @Override
    public void onBackPressed() {
        init();
        super.onBackPressed();
    }

    DeviceListClickListener beaconListClickListener = (index, mac) -> {
        ActivityController.openReportActivity(MultiDeviceActivity.this, mac);
    };

    private void init() {
        Thread t = new Thread(() -> {
            mDb.clearAllTables();
        });
        t.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_init:
                init();
                break;
            case R.id.btn_temp:
                String[] tempListArr = BaseApplication.getInstance().getResources().getStringArray(R.array.temp_list);
                ArrayList<Integer> minTempArr = new ArrayList<>();
                ArrayList<Integer> maxTempArr = new ArrayList<>();
                for (int i = 0; i < tempListArr.length; i++) {
                    try {
                        JSONObject obj = new JSONObject(tempListArr[i]);
                        int minTemp = obj.getInt("min");
                        int maxTemp = obj.getInt("max");
                        minTempArr.add(minTemp);
                        maxTempArr.add(maxTemp);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                DialogUtil.singleChoice(
                        MultiDeviceActivity.this,
                        "온도선택",
                        BaseApplication.getInstance().getResources().getStringArray(R.array.temp_name),
                        -1,
                        (dialog, which) -> {
                            TemperatureRangeItemVo vo = new TemperatureRangeItemVo();
                            vo.minimum = minTempArr.get(which);
                            vo.maximum = maxTempArr.get(which);
                            ActivityController.openMultiDeviceTempActivity(MultiDeviceActivity.this, vo);
                            dialog.dismiss();
                        }, true
                );
                break;
            case R.id.btn_layout:
                mLayoutCount++;
                if (mLayoutCount == 4) {
                    mLayoutCount = 1;
                }
                mLayoutManager.setSpanCount(mLayoutCount);
                mDeviceAdapter.setSpanCount(mLayoutCount);
                break;
        }
    }
}