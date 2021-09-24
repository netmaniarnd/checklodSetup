package com.netmania.checklod.presentation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.netmania.checklod.R;
import com.netmania.checklod.data.repository.entity.DeviceEntity;
import com.netmania.checklod.databinding.TimeSetupRowLayoutListBinding;
import com.netmania.checklod.domain.listener.DeviceListClickListener;

public class DeviceTimeSetupAdapter extends ListAdapter<DeviceEntity, DeviceTimeSetupAdapter.ViewHolder> {
    private String TAG = DeviceTimeSetupAdapter.class.getSimpleName();

    private Context mContext;
    private DeviceListClickListener mListener;
    private TimeSetupRowLayoutListBinding mBinding;
    private int mSpanCount = 1;

    public DeviceTimeSetupAdapter(Context context, DeviceListClickListener listener) {
        super(DeviceEntity.DIFF_CALLBACK);
        mContext = context;
        mListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTvDeviceMac;
        private TextView mTvDeviceTimeset;
        public ViewHolder(@NonNull View view) {
            super(view);
            itemView.setOnClickListener(v -> {
                mListener.onDeviceItemEvent(this.getLayoutPosition(), getItem(this.getLayoutPosition()).getMac());
            });
            mTvDeviceMac = mBinding.tvDeviceMac;
            mTvDeviceTimeset = mBinding.tvDeviceTimeset;
        }

        void onBind(DeviceEntity entity) {
            // TODO : time setting 완료시 backgroud 컬러 변경 red -> green
            mTvDeviceMac.setText(entity.getMac());
            mTvDeviceTimeset.setText(String.valueOf(entity.getFlag()));
        }
    }

    @NonNull
    @Override
    public DeviceTimeSetupAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mBinding = DataBindingUtil.inflate(inflater, R.layout.time_setup_row_layout_list, viewGroup, false);
        DeviceTimeSetupAdapter.ViewHolder vh = new DeviceTimeSetupAdapter.ViewHolder(mBinding.rowListLayout);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceTimeSetupAdapter.ViewHolder holder, int position) {
        DeviceEntity entity = getItem(position);
        // Log.e(TAG, "onBindViewHolder = " + position);
        if (entity != null) {
            holder.onBind(entity);
        }
    }
}
