package com.netmania.checklod.presentation.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.netmania.checklod.R;
import com.netmania.checklod.data.constant.ActivityRequestCode;
import com.netmania.checklod.data.repository.entity.DeviceLogEntity;
import com.netmania.checklod.data.vo.DeviceIntentVo;
import com.netmania.checklod.databinding.ActivityDataSyncStep2Binding;
import com.netmania.checklod.domain.gatt.ConnectionApi;
import com.netmania.checklod.domain.listener.ConnectionListener;
import com.netmania.checklod.domain.viewmodel.usecase.trip.InsertDeviceLogUseCase;
import com.netmania.checklod.domain.viewmodel.usecase.trip.listener.InsertDeviceLogListener;
import com.netmania.checklod.presentation.BaseActivity;
import com.netmania.checklod.presentation.adapter.LogAdapter;
import com.netmania.checklod.util.DialogUtil;

public class DataSyncActivity extends BaseActivity {

    private static final String TAG = DataSyncActivity.class.getSimpleName();

    private ActivityDataSyncStep2Binding mBinding;
    private ProgressDialog mProgressDialog;
    private LogAdapter mLogAdapter;
    private DeviceIntentVo mDeviceVo;

    private Boolean mStartFlag = false;
    private int mStartSeq = 0;
    private int mLastSeq = 0;
    private int mReqSeq = 0;

    private View mPopupInputDialogView;

    private Button mBtnOk;
    private Button mBtnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_data_sync_step_2);
        mContext = this;
        mDeviceVo = new DeviceIntentVo();
        mDeviceVo.setMac(getIntent().getStringExtra(EXTRA_MAC));
        setActionBar();

        mLogAdapter = new LogAdapter(mContext);
        mBinding.logList.setLayoutManager(new LinearLayoutManager(this));
        mBinding.logList.setAdapter(mLogAdapter);

        BaseActivity.mLogViewModel.delete(mDeviceVo.getMac());

        mLogViewModel.getAllLog(mDeviceVo.getMac()).observe(DataSyncActivity.this, logEntities -> {
            mLogAdapter.submitList(logEntities);
        });

        openSequencePopup();
    }

    private void openSequencePopup() {
        LayoutInflater layoutInflater = LayoutInflater.from(DataSyncActivity.this);
        mPopupInputDialogView = layoutInflater.inflate(R.layout.popup_sequence_dialog, null);
        EditText etStart = mPopupInputDialogView.findViewById(R.id.et_start);
        EditText etEnd = mPopupInputDialogView.findViewById(R.id.et_end);
        mBtnOk = mPopupInputDialogView.findViewById(R.id.btn_ok);
        mBtnCancel = mPopupInputDialogView.findViewById(R.id.btn_cancel);

        String title = "?????? ????????? ????????????";
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setIcon(R.drawable.ic_launcher_foreground);
        alertDialogBuilder.setView(mPopupInputDialogView);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        mBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etStart.getText().equals("") || etEnd.getText().equals("")) {
                    Toast.makeText(DataSyncActivity.this, "????????? ????????? ?????? ???????????????.", Toast.LENGTH_SHORT).show();
                    return;
                }
                mStartSeq = Integer.parseInt(String.valueOf(etStart.getText()));
                mLastSeq = Integer.parseInt(String.valueOf(etEnd.getText()));
                mReqSeq = mStartSeq;
                if (mStartSeq > mLastSeq) {
                    Toast.makeText(DataSyncActivity.this, "??????????????? ?????????????????? ?????????.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mLastSeq - mStartSeq > 1440) {
                    Toast.makeText(DataSyncActivity.this, "??????????????? ???????????? ?????? ????????????.", Toast.LENGTH_SHORT).show();
                    return;
                }
                ConnectionApi.getInstance().disconnectGatt();
                ConnectionApi.getInstance().ConnectionApi(DataSyncActivity.this, mDeviceVo, mConnectionListener);
                alertDialog.dismiss();
            }
        });
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                finish();
            }
        });
    }

    private void setActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setIcon(R.drawable.ic_launcher_foreground);
    }

    /**
     * ????????? ?????? insert ??????
     */
    InsertDeviceLogListener mDeviceLogListener = new InsertDeviceLogListener() {
        @Override
        public void onComplete(DeviceLogEntity logEntity) {
        }

        @Override
        public void onFail() {
            // getDeviceLog();
        }
    };

    /**
     * ??????????????? ?????? ?????? ?????? ?????? ??????
     */
    ConnectionListener mConnectionListener = new ConnectionListener() {
        @Override
        public void onConnect() {
            ConnectionApi.getInstance().sendCommandGetSaveData(2, mStartSeq);
        }

        @Override
        public void onLoaded(int type, byte[] bytes, String mac) {
            try {
                InsertDeviceLogUseCase.insertDeviceLog(mDeviceLogListener, bytes, mac);
                if (!mStartFlag) {
                    mStartFlag = true;
                    createProgressDialog();
                }
                getDeviceLog();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailed() {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> {
                ConnectionApi.getInstance().ConnectionApi(DataSyncActivity.this, mDeviceVo, mConnectionListener);
            }, 1000);
        }
    };

    /**
     * ??????????????? ?????? ?????? ?????? ??????
     */
    private void getDeviceLog() {
        mReqSeq += 1;
        if (mReqSeq <= mLastSeq) {
            Log.e(TAG, "insertBeaconLog : " + "?????? : " + mReqSeq);
            // ??????????????? ?????? delayTime ??????
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> {
                ConnectionApi.getInstance().sendCommandGetSaveData(2, mReqSeq);
            }, 100);
        } else {
            Log.e(TAG, "insertBeaconLog : " + "?????? : " + mReqSeq);
            ConnectionApi.getInstance().disconnectGatt();
            if (mProgressDialog != null) {
                removeProgressDialog();
            }
            Toast.makeText(DataSyncActivity.this, "????????? ?????? ??????.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ActivityRequestCode.POD_CODE:
                finish();
                break;
        }
    }

    private void createProgressDialog() {
        runOnUiThread(() -> {
            removeProgressDialog();
            String title = getResources().getString(R.string.progress_takeover_title);
            String msg = getResources().getString(R.string.progress_takeover_message);
            mProgressDialog = DialogUtil.progress(DataSyncActivity.this, title, msg, false);
        });
    }

    private void removeProgressDialog() {
        runOnUiThread(() -> {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        });
    }
}
