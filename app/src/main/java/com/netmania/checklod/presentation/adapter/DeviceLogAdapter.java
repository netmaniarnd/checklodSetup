package com.netmania.checklod.presentation.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.netmania.checklod.R;
import com.netmania.checklod.data.repository.entity.DeviceLogEntity;
import com.netmania.checklod.data.repository.entity.LogEntity;
import com.netmania.checklod.databinding.ItemTemperatureReportBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DeviceLogAdapter extends RecyclerView.Adapter<DeviceLogAdapter.ViewHolder> {
    private String TAG = DeviceLogAdapter.class.getSimpleName();
    private Context mContext;
    private ItemTemperatureReportBinding binding;
    private List<DeviceLogEntity> mData = new ArrayList<>();

    public DeviceLogAdapter(Context context) {
        mContext = context;
        mData.clear();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View view) {
            super(view);
        }

        void onBind(DeviceLogEntity entity, int position) {
            binding.itemDate.setText(entity.getRtc());
            binding.itemSeq.setText(String.valueOf(entity.getSequence()));
            binding.itemTemperature.setText(entity.getOuterTemp() + "(" + entity.getInnerTemp() + ")");
            if (getItemCount() == 1 && position == 0) {
                binding.itemDot.setImageResource(R.drawable.ft_img_dot_bottom);
            } else if (position == 0) {
                binding.itemDot.setImageResource(R.drawable.ft_img_dot_top);
            } else if (position == getItemCount() - 1) {
                binding.itemDot.setImageResource(R.drawable.ft_img_dot_bottom);
            } else {
                binding.itemDot.setImageResource(R.drawable.ft_img_dot_mid);
            }
        }
    }

    @NonNull
    @Override
    public DeviceLogAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        binding = DataBindingUtil.inflate(inflater, R.layout.item_temperature_report, viewGroup, false);
        DeviceLogAdapter.ViewHolder vh = new DeviceLogAdapter.ViewHolder(binding.reportListLayout);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DeviceLogEntity entity = getEntity(position);
        // Log.e(TAG, entity.toString());
        if (entity != null) {
            holder.onBind(entity, position);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public DeviceLogEntity getEntity(int position) {
        return mData.get(position);
    }

    public void setItems(List<DeviceLogEntity> entities) {
        mData.addAll(entities);
        notifyDataSetChanged();
    }
}
