package com.netmania.checklod.presentation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.netmania.checklod.R;
import com.netmania.checklod.data.repository.entity.LogEntity;
import com.netmania.checklod.databinding.ItemTemperatureReportBinding;

public class LogAdapter extends ListAdapter<LogEntity, LogAdapter.ViewHolder> {
    private String TAG = LogAdapter.class.getSimpleName();
    private Context mContext;
    private ItemTemperatureReportBinding mBinding;

    public LogAdapter(Context context) {
        super(LogEntity.DIFF_CALLBACK);
        mContext = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mItemDate;
        private TextView mItemSequence;
        private TextView mItemTemp;
        private ImageView mItemDot;


        public ViewHolder(@NonNull View view) {
            super(view);
            mItemDate = mBinding.itemDate;
            mItemSequence = mBinding.itemSeq;
            mItemTemp = mBinding.itemTemperature;
            mItemDot = mBinding.itemDot;
        }

        void onBind(LogEntity entity, int position) {
            mItemDate.setText(entity.getRtc());
            mItemSequence.setText(String.valueOf(entity.getSequence()));
            mItemTemp.setText(entity.getOuterTemp() + "(" + entity.getInnerTemp() + ")");
            if (getItemCount() == 1 && position == 0) {
                mItemDot.setImageResource(R.drawable.ft_img_dot_bottom);
            } else if (position == 0) {
                mItemDot.setImageResource(R.drawable.ft_img_dot_top);
            } else if (position == getItemCount() - 1) {
                mItemDot.setImageResource(R.drawable.ft_img_dot_bottom);
            } else {
                mItemDot.setImageResource(R.drawable.ft_img_dot_mid);
            }
        }
    }

    @NonNull
    @Override
    public LogAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mBinding = DataBindingUtil.inflate(inflater, R.layout.item_temperature_report, viewGroup, false);
        LogAdapter.ViewHolder vh = new LogAdapter.ViewHolder(mBinding.reportListLayout);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull LogAdapter.ViewHolder holder, int position) {
        LogEntity entity = getItem(position);
        // Log.e(TAG, entity.toString());
        if (entity != null) {
            holder.onBind(entity, position);
        }
    }
}
