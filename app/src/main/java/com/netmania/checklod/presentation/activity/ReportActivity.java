package com.netmania.checklod.presentation.activity;

import android.graphics.Color;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.netmania.checklod.R;
import com.netmania.checklod.data.constant.IntentVo;
import com.netmania.checklod.data.repository.entity.DeviceLogEntity;
import com.netmania.checklod.databinding.ActivityReportBinding;
import com.netmania.checklod.presentation.BaseActivity;
import com.netmania.checklod.presentation.adapter.DeviceLogAdapter;

import java.util.ArrayList;
import java.util.List;

public class ReportActivity extends BaseActivity {
    private String TAG = ReportActivity.class.getSimpleName();

    private ActivityReportBinding mBinding;
    private DeviceLogAdapter mDeviceLogAdapter;
    private GridLayoutManager mLayoutManager;
    private String mMac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_report);
        mMac = getIntent().getStringExtra(IntentVo.EXTRA_DEVICEVO);
        mDeviceLogAdapter = new DeviceLogAdapter(this);
        mLayoutManager = new GridLayoutManager(this, 1);
        mBinding.rvDataList.setAdapter(mDeviceLogAdapter);
        mBinding.rvDataList.setLayoutManager(mLayoutManager);
        mBinding.textMac.setText(mMac);
        init();
    }

    private void init() {
        Thread t = new Thread(() -> {
            List<DeviceLogEntity> beaconLogEntities = mDb.deviceLogDao().getAll(mMac);
            ArrayList<Entry> innerTempArr = new ArrayList<Entry>();
            ArrayList<Entry> outerTempArr = new ArrayList<Entry>();
            if (beaconLogEntities != null && beaconLogEntities.size() > 0) {
                for (int i = 0; i < beaconLogEntities.size(); i++) {
                    float val = beaconLogEntities.get(i).getOuterTemp().floatValue();
                    float val1 = beaconLogEntities.get(i).getInnerTemp().floatValue();
                    outerTempArr.add(new Entry(i, val));
                    innerTempArr.add(new Entry(i, val1));
                }
            }
            drawLineChart(outerTempArr, innerTempArr);
            mDeviceLogAdapter.setItems(beaconLogEntities);
        });
        t.start();
    }

    private void drawLineChart(ArrayList<Entry> values, ArrayList<Entry> values1) {
        runOnUiThread(() -> {
            LineChart chart = mBinding.itemChart;
            // Log.e(TAG, values.toString());
            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(getLineDataSet(values, "외부온도", Color.RED)); // add the datasets
            dataSets.add(getLineDataSet(values1, "내부온도", Color.GREEN)); // add the datasets
            LineData lineData = new LineData(dataSets);
            chart.setData(lineData);

            XAxis xAxis = chart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setTextSize(10f);
            xAxis.setDrawAxisLine(false);
            xAxis.setDrawGridLines(true);
            xAxis.setTextColor(Color.rgb(21, 76, 182));
            xAxis.setCenterAxisLabels(false);
            xAxis.setGranularity(1f); // one hour
            xAxis.enableGridDashedLine(8, 24, 0);

            YAxis yLAxis = chart.getAxisLeft();
            yLAxis.setTextColor(Color.BLACK);

            YAxis yRAxis = chart.getAxisRight();
            yRAxis.setDrawLabels(false);
            yRAxis.setDrawAxisLine(false);
            yRAxis.setDrawGridLines(false);

            Description description = new Description();
            description.setText("");

            chart.setDoubleTapToZoomEnabled(false);
            chart.setDrawGridBackground(false);
            chart.setDescription(description);
            chart.animateY(1000, Easing.EaseInCubic);
            chart.invalidate();

            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        });
    }

    private LineDataSet getLineDataSet(ArrayList<Entry> values, String label, int color) {
        LineDataSet lineDataSet = new LineDataSet(values, label);
        lineDataSet.setLineWidth(2);
        lineDataSet.setCircleRadius(6);
        lineDataSet.setCircleColor(Color.BLACK);
        lineDataSet.setColor(color);
        lineDataSet.setDrawCircles(true);
        lineDataSet.setDrawHorizontalHighlightIndicator(false);
        lineDataSet.setDrawHighlightIndicators(false);
        lineDataSet.setDrawValues(false);
        return lineDataSet;
    }
}