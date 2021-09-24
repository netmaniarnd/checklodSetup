package com.netmania.checklod.presentation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.netmania.checklod.R;
import com.netmania.checklod.util.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoggerListAdapter extends RecyclerView.Adapter<LoggerListAdapter.ViewHolder> {
    private JSONArray mData;

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView alias;
        TextView macaddress;
        ViewHolder(View itemView) {
            super(itemView);
            // 뷰 객체에 대한 참조. (hold strong reference)
            alias = itemView.findViewById(R.id.alias);
            macaddress = itemView.findViewById(R.id.macaddress);
        }
    }

    // 생성자에서 데이터 리스트 객체를 전달받음.
    public LoggerListAdapter(JSONArray list) {
        try {
            mData = Utility.sortJsonArray(list);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public LoggerListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.loggerlist_item, parent, false);
        LoggerListAdapter.ViewHolder vh = new LoggerListAdapter.ViewHolder(view);
        return vh;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(LoggerListAdapter.ViewHolder holder, int position) {
        JSONObject obj = null;
        try {
            obj = mData.getJSONObject(position);
            holder.alias.setText(obj.getString("alias"));
            holder.macaddress.setText(obj.getString("id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mData.length();
    }
}
