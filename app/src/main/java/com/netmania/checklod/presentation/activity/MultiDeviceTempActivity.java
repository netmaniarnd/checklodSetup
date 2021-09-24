package com.netmania.checklod.presentation.activity;

import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.netmania.checklod.R;
import com.netmania.checklod.data.constant.IntentVo;
import com.netmania.checklod.data.vo.TemperatureRangeItemVo;
import com.netmania.checklod.databinding.ActivityTempBinding;
import com.netmania.checklod.domain.listener.DeviceListClickListener;
import com.netmania.checklod.presentation.ActivityController;
import com.netmania.checklod.presentation.BaseActivity;
import com.netmania.checklod.presentation.adapter.MultiDeviceAdapter;

public class MultiDeviceTempActivity extends BaseActivity implements View.OnClickListener {
    private String TAG = MultiDeviceTempActivity.class.getSimpleName();

    private ActivityTempBinding mBinding;
    private MultiDeviceAdapter mBeaconAdapter;
    private GridLayoutManager mLayoutManager;
    private TemperatureRangeItemVo mTempVo;
    private int mLayoutCount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_temp);
        mBinding.btnLayout.setOnClickListener(this);
        mBeaconAdapter = new MultiDeviceAdapter(this, beaconListClickListener);
        mLayoutManager = new GridLayoutManager(this, mLayoutCount);
        mLayoutManager.setItemPrefetchEnabled(false);
        mBinding.rvBeaconTempList.setAdapter(mBeaconAdapter);
        mBinding.rvBeaconTempList.setLayoutManager(mLayoutManager);
        mBinding.rvBeaconTempList.setNestedScrollingEnabled(false);

        mTempVo = (TemperatureRangeItemVo) getIntent().getSerializableExtra(IntentVo.EXTRA_TEMPVO);
        int minTemp = mTempVo.getMinimum();
        int maxTemp = mTempVo.getMaximum();
        mBinding.textTempRange.setText(""+minTemp+"℃ ~ "+maxTemp+"℃");
        mTripViewModel.getTempAll(minTemp, maxTemp).observe(this, tripEntities -> {
            mBeaconAdapter.submitList(tripEntities);
        });
    }

    DeviceListClickListener beaconListClickListener = (index, mac) -> {
        ActivityController.openReportActivity(MultiDeviceTempActivity.this, mac);
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_layout:
                mLayoutCount++;
                if (mLayoutCount == 4) {
                    mLayoutCount = 1;
                }
                mLayoutManager.setSpanCount(mLayoutCount);
                mBeaconAdapter.setSpanCount(mLayoutCount);
                break;
        }
    }
}