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

import com.netmania.checklod.R;
import com.netmania.checklod.databinding.ItemMenuBinding;
import com.netmania.checklod.domain.listener.MenuListClickListener;

import java.util.ArrayList;
import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {

    public static String TAG = MenuAdapter.class.getSimpleName();
    private List<String> mData;
    private ItemMenuBinding mBinding;
    private MenuListClickListener mClickListener;

    // 생성자에서 데이터 리스트 객체를 전달받음.
    public MenuAdapter(ArrayList<String> list, MenuListClickListener listener) {
        mData = list ;
        mClickListener = listener;
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTxMac;
        ViewHolder(View itemView) {
            super(itemView);
            // 뷰 객체에 대한 참조. (hold strong reference)
            itemView.setOnClickListener(v -> {
                mClickListener.onItemEvent(this.getLayoutPosition());
            });
            mTxMac = mBinding.tvMenu;
        }

        void onBind(String menu) {
            mTxMac.setText(menu);
        }
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public MenuAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mBinding = DataBindingUtil.inflate(inflater, R.layout.item_menu, parent, false);
        MenuAdapter.ViewHolder vh = new MenuAdapter.ViewHolder(mBinding.rowListLayout);
        return vh;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(@NonNull MenuAdapter.ViewHolder holder, int position) {
        holder.onBind(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
