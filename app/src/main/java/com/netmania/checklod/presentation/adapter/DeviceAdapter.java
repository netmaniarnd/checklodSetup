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
import com.netmania.checklod.data.repository.entity.TripEntity;
import com.netmania.checklod.databinding.ItemMenuBinding;
import com.netmania.checklod.domain.listener.DeviceListClickListener;

public class DeviceAdapter extends ListAdapter<TripEntity, DeviceAdapter.ViewHolder> {

    public static String TAG = DeviceAdapter.class.getSimpleName();
    private ItemMenuBinding mBinding;
    private DeviceListClickListener mClickListener;

    // 생성자에서 데이터 리스트 객체를 전달받음.
    public DeviceAdapter(Context context, DeviceListClickListener listener) {
        super(TripEntity.DIFF_CALLBACK);
        mClickListener = listener;
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTxMac;
        ViewHolder(View itemView) {
            super(itemView);
            // 뷰 객체에 대한 참조. (hold strong reference)
            itemView.setOnClickListener(v -> {
                mClickListener.onDeviceItemEvent(this.getLayoutPosition(), getItem(this.getLayoutPosition()).getMac());
            });
            mTxMac = mBinding.tvMenu;
        }

        void onBind(TripEntity entity) {
            mTxMac.setText(entity.getMac());
        }
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public DeviceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mBinding = DataBindingUtil.inflate(inflater, R.layout.item_menu, parent, false);
        DeviceAdapter.ViewHolder vh = new DeviceAdapter.ViewHolder(mBinding.rowListLayout);
        return vh;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(@NonNull DeviceAdapter.ViewHolder holder, int position) {
        TripEntity entity = getItem(position);
        // Log.e(TAG, "onBindViewHolder = " + position);
        if (entity != null) {
            holder.onBind(entity);
        }
    }
}
