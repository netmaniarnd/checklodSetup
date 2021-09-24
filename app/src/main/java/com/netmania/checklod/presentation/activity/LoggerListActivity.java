package com.netmania.checklod.presentation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.netmania.checklod.R;
import com.netmania.checklod.domain.listener.RecyclerClickListener;
import com.netmania.checklod.domain.listener.RecyclerTouchListener;
import com.netmania.checklod.presentation.BaseActivity;
import com.netmania.checklod.presentation.adapter.LoggerListAdapter;
import com.netmania.checklod.util.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoggerListActivity extends BaseActivity {

    private JSONArray mDeviceAlias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logger_list);
        setTitle("Device List");
        try {
            // TODO : 배열 변경
            mDeviceAlias = Utility.sortJsonArray(new JSONArray(getIntent().getExtras().getString("deviceList")));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
        RecyclerView recyclerView = findViewById(R.id.logger_list);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));

        // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
        LoggerListAdapter adapter = new LoggerListAdapter(mDeviceAlias);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerClickListener() {
            @Override
            public void onClick(View view, int position) {
                JSONObject obj;
                try {
                    obj = mDeviceAlias.getJSONObject(position);
                    Intent intent = new Intent();
                    intent.putExtra("deviceList", obj.toString());
                    setResult(100, intent);
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("deviceList", "");
        setResult(100, intent);
        super.onBackPressed();
        finish();
    }
}
