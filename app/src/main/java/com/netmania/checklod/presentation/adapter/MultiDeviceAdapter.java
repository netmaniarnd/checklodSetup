package com.netmania.checklod.presentation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.netmania.checklod.BaseApplication;
import com.netmania.checklod.R;
import com.netmania.checklod.data.repository.entity.TripEntity;
import com.netmania.checklod.databinding.RowLayoutListBinding;
import com.netmania.checklod.domain.listener.DeviceListClickListener;

import org.json.JSONObject;

public class MultiDeviceAdapter extends ListAdapter<TripEntity, MultiDeviceAdapter.ViewHolder> {
    private String TAG = MultiDeviceAdapter.class.getSimpleName();
    private Context mContext;
    private DeviceListClickListener mListener;
    private RowLayoutListBinding mBinding;
    private int mSpanCount = 1;

    public MultiDeviceAdapter(Context context, DeviceListClickListener listener) {
        super(TripEntity.DIFF_CALLBACK);
        mContext = context;
        mListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTxMac;
        private TextView mTxRssi;
        private TextView mTxTempOuter;
        private TextView mTxTempInner;
        private TextView mTxDate;
        private TextView mTxSeq;
        private LinearLayout mRowCenter;
        public ViewHolder(@NonNull View view) {
            super(view);
            itemView.setOnClickListener(v -> {
                mListener.onDeviceItemEvent(this.getLayoutPosition(), getItem(this.getLayoutPosition()).getMac());
            });
            mTxMac = mBinding.rowMacAddress;
            mTxRssi = mBinding.rowSensorRssi;
            mTxDate = mBinding.rowDate;
            mTxSeq = mBinding.rowSeq;
            mTxTempOuter = mBinding.rowTemp;
            mTxTempInner = mBinding.rowTemp1;
            mRowCenter = mBinding.rowCenter;
        }

        void onBind(TripEntity entity) {
            Double outerTemp = entity.getOuterTemp();
            mTxMac.setText(entity.getMac());
            mTxRssi.setText(String.valueOf(entity.getBattery()));
            mTxDate.setText(entity.getRtc());
            mTxSeq.setText(String.valueOf(entity.getEndSeq()));
            mTxTempOuter.setText(String.valueOf(entity.getOuterTemp()));
            mTxTempInner.setText(String.valueOf(entity.getInnerTemp()));
            // Log.e(TAG, entity.getMac());
            if (mSpanCount == 1) {
                mRowCenter.setVisibility(View.VISIBLE);
            } else {
                mRowCenter.setVisibility(View.GONE);
            }
            String[] tempOrangeArr = BaseApplication.getInstance().getResources().getStringArray(R.array.temp_orange);
            String[] tempRedArr = BaseApplication.getInstance().getResources().getStringArray(R.array.temp_red);
            for (int i = 0 ; i < tempOrangeArr.length ; i++) {
                try {
                    JSONObject obj = new JSONObject(tempOrangeArr[i]);
                    int minTemp = obj.getInt("min");
                    int maxTemp = obj.getInt("max");
                    if (minTemp < outerTemp && maxTemp >= outerTemp ){
                        mTxTempOuter.setTextColor(BaseApplication.getInstance().getResources().getColor(R.color.colorOrange));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            for (int i = 0 ; i < tempRedArr.length ; i++) {
                try {
                    JSONObject obj = new JSONObject(tempRedArr[i]);
                    int minTemp = obj.getInt("min");
                    int maxTemp = obj.getInt("max");
                    if (minTemp < outerTemp && maxTemp >= outerTemp ){
                        mTxTempOuter.setTextColor(BaseApplication.getInstance().getResources().getColor(R.color.warnning_red));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @NonNull
    @Override
    public MultiDeviceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mBinding = DataBindingUtil.inflate(inflater, R.layout.row_layout_list, viewGroup, false);
        MultiDeviceAdapter.ViewHolder vh = new MultiDeviceAdapter.ViewHolder(mBinding.rowListLayout);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MultiDeviceAdapter.ViewHolder holder, int position) {
        TripEntity entity = getItem(position);
        // Log.e(TAG, "onBindViewHolder = " + position);
        if (entity != null) {
            holder.onBind(entity);
        }
    }

    public void setSpanCount(int spanCount) {
        mSpanCount = spanCount;
        notifyDataSetChanged();
    }
}