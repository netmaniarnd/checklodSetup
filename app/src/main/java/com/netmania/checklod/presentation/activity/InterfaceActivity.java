package com.netmania.checklod.presentation.activity;

import android.app.AlertDialog;
import android.bluetooth.BluetoothGatt;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.netmania.checklod.R;
import com.netmania.checklod.data.vo.BaseVo;
import com.netmania.checklod.data.vo.DeviceLogVo;
import com.netmania.checklod.data.vo.DeviceVo;
import com.netmania.checklod.data.vo.IndexVo;
import com.netmania.checklod.data.vo.IntervalVo;
import com.netmania.checklod.data.vo.OffsetVo;
import com.netmania.checklod.data.vo.SingleOffsetVo;
import com.netmania.checklod.data.vo.StoreIndexVo;
import com.netmania.checklod.data.vo.TimeVo;
import com.netmania.checklod.databinding.ActivityInterfaceBinding;
import com.netmania.checklod.domain.gatt.GattApi;
import com.netmania.checklod.domain.listener.GattListener;
import com.netmania.checklod.domain.listener.RecyclerClickListener;
import com.netmania.checklod.domain.listener.RecyclerTouchListener;
import com.netmania.checklod.presentation.BaseActivity;
import com.netmania.checklod.presentation.adapter.GattListAdapter;
import com.netmania.checklod.util.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class InterfaceActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = InterfaceActivity.class.getSimpleName();
    private ActivityInterfaceBinding mBinding;
    private GattApi mGattApi;
    private long mLastClickTime = 0;
    private ArrayList<Integer> mResponseData;

    private JSONArray mGattArr;

    private View mPopupInputDialogView;
    private Button mBtnGetOffset;
    private Button mBtnOk;
    private Button mBtnCancel;
    public InterfaceActivity mActivity;

    private String mMac;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_interface);
        mActivity = this;
        mMac = getIntent().getStringExtra(EXTRA_MAC);
        mBinding.textMac.setText(mMac);
        mBinding.btnConnection.setOnClickListener(this);

        JSONArray jsonArray = new JSONArray();
        for (int i = 1; i <= 14; i++) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("index", i);
                switch (i) {
                    case 1:
                        jsonObject.put("name", "현재 데이터 요청");
                        break;
                    case 2:
                        jsonObject.put("name", "저장 데이터 요청");
                        break;
                    case 3:
                        jsonObject.put("name", "저장 인덱스 요청");
                        break;
                    case 4:
                        jsonObject.put("name", "인덱스 카운터 설정");
                        break;
                    case 5:
                        jsonObject.put("name", "기타 설정값 요청");
                        break;
                    case 6:
                        jsonObject.put("name", "기타 설정값 설정");
                        break;
                    case 7:
                        jsonObject.put("name", "현재 날짜 요청");
                        break;
                    case 8:
                        jsonObject.put("name", "현재 날짜 설정");
                        break;
                    case 9:
                        jsonObject.put("name", "센서 온도 보정 상태 요청");
                        break;
                    case 10:
                        jsonObject.put("name", "센서 온도 보정 수동 설정");
                        break;
                    case 11:
                        jsonObject.put("name", "센서 온도 보정 자동 설정");
                        break;
                    case 12:
                        jsonObject.put("name", "본체 온도 보정 상태 요청");
                        break;
                    case 13:
                        jsonObject.put("name", "본체 온도 보정 수동 설정");
                        break;
                    case 14:
                        jsonObject.put("name", "본체 온도 보정 자동 설정");
                        break;
                }
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mGattArr = jsonArray;
        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
        RecyclerView recyclerView = findViewById(R.id.logger_list);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));

        // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
        GattListAdapter adapter = new GattListAdapter(mGattArr);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerClickListener() {

            @Override
            public void onClick(View view, int position) {
                Log.e(TAG, "position : " + position);
                int realPosition = position + 1;
                if (mGattApi != null && mGattApi.getGattConnected()) {
                    // TODO : api별 예외처리
                    // 02(요청인덱스)
                    // 04(시작인덱스, 최대인덱스) : 0-65535
                    // 06(저장인터벌, BC인터벌) : 1-240 분단위(기본 1분)
                    // 0A(센서온도보정 -80, -40, -20, 5, 20 : 2바이트)
                    // 0B(센서각보정 자동설정 1 = -80, 2 = -40...) : 1-5
                    // 0D(센서온도보정 -80, -40, -20, 5, 20 : 2바이트)
                    // 0E(센서각보정 자동설정 1 = -80, 2 = -40...) : 1-5
                    // 나머지 값은 response 처리
                    if (realPosition == 2 || realPosition == 4 || realPosition == 6 || realPosition == 10 || realPosition == 11 || realPosition == 13 || realPosition == 14) {
                        openAlertDialog(position + 1);
                    } else {
                        mGattApi.sendCommandRead(position + 1);
                    }
                }
            }

            @Override
            public void onLongClick(View view, int position) {
            }
        }));
    }

    @Override
    public void onBackPressed() {
        if (mGattApi != null) mGattApi.disconnectGatt();
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        if (mGattApi != null) mGattApi.disconnectGatt();
        super.onDestroy();
    }

    private TextView requestIndex;
    private TextView startIndex;
    private TextView endIndex;
    private TextView logInterval;
    private TextView advInterval;
    private TextView cali1;
    private TextView cali2;
    private TextView cali3;
    private TextView cali4;
    private TextView cali5;
    private TextView caliIndex;

    private void openAlertDialog(final int type) {
        // container2 ... container11
        String title = "";
        String defaultStr = "0";
        LayoutInflater layoutInflater = LayoutInflater.from(InterfaceActivity.this);
        mPopupInputDialogView = layoutInflater.inflate(R.layout.popup_interface_dialog, null);
        mBtnOk = mPopupInputDialogView.findViewById(R.id.btn_ok);
        mBtnCancel = mPopupInputDialogView.findViewById(R.id.btn_cancel);

        View container2 = mPopupInputDialogView.findViewById(R.id.container2);
        View container4 = mPopupInputDialogView.findViewById(R.id.container4);
        View container6 = mPopupInputDialogView.findViewById(R.id.container6);
        View container10 = mPopupInputDialogView.findViewById(R.id.container10);
        View container11 = mPopupInputDialogView.findViewById(R.id.container11);

        requestIndex = container2.findViewById(R.id.request_index);
        startIndex = container4.findViewById(R.id.start_index);
        endIndex = container4.findViewById(R.id.end_index);
        logInterval = container6.findViewById(R.id.log_interval);
        advInterval = container6.findViewById(R.id.adv_interval);
        requestIndex.setText(defaultStr);
        startIndex.setText(defaultStr);
        endIndex.setText(defaultStr);
        logInterval.setText(defaultStr);
        advInterval.setText(defaultStr);

        mBtnGetOffset = container10.findViewById(R.id.btn_get_offset);

        cali1 = container10.findViewById(R.id.cali1);
        cali2 = container10.findViewById(R.id.cali2);
        cali3 = container10.findViewById(R.id.cali3);
        cali4 = container10.findViewById(R.id.cali4);
        cali5 = container10.findViewById(R.id.cali5);
        cali1.setText(defaultStr);
        cali2.setText(defaultStr);
        cali3.setText(defaultStr);
        cali4.setText(defaultStr);
        cali5.setText(defaultStr);

        caliIndex = container11.findViewById(R.id.cali_index);
        caliIndex.setText(defaultStr);
        container2.setVisibility(View.GONE);
        container4.setVisibility(View.GONE);
        container6.setVisibility(View.GONE);
        container10.setVisibility(View.GONE);
        container11.setVisibility(View.GONE);

        switch (type) {
            case 2:
                title = "저장 데이터 요청";
                container2.setVisibility(View.VISIBLE);
                break;
            case 4:
                title = "인덱스 카운터 설정";
                container4.setVisibility(View.VISIBLE);
                break;
            case 6:
                title = "기타 설정값 설정";
                container6.setVisibility(View.VISIBLE);
                break;
            case 10:
            case 13:
                // 동일
                if (type == 10) {
                    title = "센서 온도 보정 수동 설정";
                } else {
                    title = "본체 온도 보정 수동 설정";
                }
                container10.setVisibility(View.VISIBLE);
                break;
            case 11:
            case 14:
                // 동일
                if (type == 11) {
                    title = "센서 온도 보정 자동 설정";
                } else {
                    title = "본체 온도 보정 자동 설정";
                }
                container11.setVisibility(View.VISIBLE);
                break;
        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setIcon(R.drawable.ic_launcher_foreground);
        alertDialogBuilder.setView(mPopupInputDialogView);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        mBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                // TODO : 커맨드 호출
                // mGattApi.sendCmmandWrite(position + 1);
                mGattApi.sendCommandWrite(type, getTempArray(type));
            }
        });
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        mBtnGetOffset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO : offset정보 가져오기
                mGattApi.sendCommandRead(9);
            }
        });
    }

    private ArrayList<Integer> getTempArray(int index) {
        ArrayList<Integer> setTempList = new ArrayList<Integer>();
        switch (index) {
            case 2:
                setTempList.add(Integer.parseInt(requestIndex.getText().toString()));
                break;
            case 4:
                setTempList.add(Integer.parseInt(startIndex.getText().toString()));
                setTempList.add(Integer.parseInt(endIndex.getText().toString()));
                break;
            case 6:
                setTempList.add(Integer.parseInt(logInterval.getText().toString()));
                setTempList.add(Integer.parseInt(advInterval.getText().toString()));
                break;
            case 10:
            case 13:
                // TODO : 100 1도
                setTempList.add(Integer.parseInt(cali1.getText().toString()));
                setTempList.add(Integer.parseInt(cali2.getText().toString()));
                setTempList.add(Integer.parseInt(cali3.getText().toString()));
                setTempList.add(Integer.parseInt(cali4.getText().toString()));
                setTempList.add(Integer.parseInt(cali5.getText().toString()));
                break;
            case 11:
            case 14:
                setTempList.add(Integer.parseInt(caliIndex.getText().toString()));
                break;
        }
        return setTempList;
    }

    private GattListener mGattListener = new GattListener() {
        @Override
        public void onRequest(byte[] bytes) {
            showToast(Utility.byteArrayToHex(bytes));
        }

        @Override
        public void onLoaded(final byte[] bytes, BluetoothGatt bluetoothGatt) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            Log.e(TAG, "onCharacteristicChanged - value=" + Utility.byteArrayToHex(bytes));
            // Log.e(TAG, "CMD : " + bytes[0]); // 99
            // Log.e(TAG, "CMD : " + bytes[1]); // 커맨드값
            // Log.e(TAG, "command : " + Utility.convertCommand(bytes));
            int cmd = bytes[1];
            byte[] advertisement = bytes;
            BaseVo vo = new BaseVo();
            int offset = 0;
            switch (cmd) {
                case 1:
                case 2:
                    vo = new DeviceLogVo();
                    ((DeviceLogVo) vo).setMac(bluetoothGatt.getDevice().getAddress());
                    ((DeviceLogVo) vo).setRssi(0);
                    ((DeviceLogVo) vo).setOuterTemp(new Double(Utility.convertTemp(advertisement, offset + 2)));
                    ((DeviceLogVo) vo).setInnerTemp(new Double(Utility.convertTemp(advertisement, offset + 4)));
                    ((DeviceLogVo) vo).setRtc("" + advertisement[offset + 6] + ":" + advertisement[offset + 7] + ":" + advertisement[offset + 8] + ":" + advertisement[offset + 9]);
                    ((DeviceLogVo) vo).setSequence(Utility.convertSeq(advertisement, offset + 10));
                    break;
                case 3:
                    vo = new StoreIndexVo();
                    ((StoreIndexVo) vo).setStartIndex(Utility.convertDate(advertisement, offset + 2));
                    ((StoreIndexVo) vo).setStartRtc("" + advertisement[offset + 4] + ":" + advertisement[offset + 5] + ":" + advertisement[offset + 6] + ":" + advertisement[offset + 7]);
                    ((StoreIndexVo) vo).setEndIndex(Utility.convertDate(advertisement, offset + 8));
                    ((StoreIndexVo) vo).setEndRtc("" + advertisement[offset + 10] + ":" + advertisement[offset + 11] + ":" + advertisement[offset + 12] + ":" + advertisement[offset + 13]);
                    ((StoreIndexVo) vo).setTotalIndex(Utility.convertDate(advertisement, offset + 14));
                    break;
                case 4:
                    vo = new IndexVo();
                    ((IndexVo) vo).setMaxIndex(Utility.convertDate(advertisement, offset + 2));
                    ((IndexVo) vo).setStartIndex(Utility.convertDate(advertisement, offset + 4));
                    break;
                case 5:
                case 6:
                    vo = new IntervalVo();
                    ((IntervalVo) vo).setStoreInterval(advertisement[offset + 2]);
                    ((IntervalVo) vo).setBroadcastInterval(advertisement[offset + 3]);
                    break;
                case 7:
                case 8:
                    vo = new TimeVo();
                    ((TimeVo) vo).setRtc("" + advertisement[offset + 2] + ":" + advertisement[offset + 3] + ":" + advertisement[offset + 4] + " " +
                            advertisement[offset + 5] + ":" + advertisement[offset + 6] + ":" + advertisement[offset + 7]);
                    break;
                case 9:
                    setOffsetData(advertisement, offset);
                case 10:
                case 12:
                case 13:
                    vo = new OffsetVo();
                    ((OffsetVo) vo).setValue1(Utility.convertDate(advertisement, offset + 2));
                    ((OffsetVo) vo).setValue2(Utility.convertDate(advertisement, offset + 4));
                    ((OffsetVo) vo).setValue3(Utility.convertDate(advertisement, offset + 6));
                    ((OffsetVo) vo).setValue4(Utility.convertDate(advertisement, offset + 8));
                    ((OffsetVo) vo).setValue5(Utility.convertDate(advertisement, offset + 10));
                    break;
                case 11:
                case 14:
                    vo = new SingleOffsetVo();
                    ((SingleOffsetVo) vo).setIndex(advertisement[offset + 2]);
                    ((SingleOffsetVo) vo).setValue1(Utility.convertDate(advertisement, offset + 3));
                    break;
            }
            showToast(vo.toString());
        }
    };

    private void setOffsetData(byte[] advertisement, int offset) {
        BaseVo vo = new OffsetVo();
        cali1.setText(Utility.convertDate(advertisement, offset + 2));
        cali2.setText(Utility.convertDate(advertisement, offset + 4));
        cali3.setText(Utility.convertDate(advertisement, offset + 6));
        cali4.setText(Utility.convertDate(advertisement, offset + 8));
        cali5.setText(Utility.convertDate(advertisement, offset + 10));
    }

    private void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBinding.textConnection.setText(msg);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_connection:
                /*
                if (mGattApi != null && mGattApi.getGattConnected()) {
                    mGattApi.sendRoof();
                }
                */
                mGattApi = null;
                String mac = String.valueOf(mBinding.textMac.getText()).trim().toUpperCase();
                if (mac == "") {
                    Toast.makeText(this, "macAddress 입력", Toast.LENGTH_SHORT);
                }
                DeviceVo device = new DeviceVo();
                // macAddress = mac; // prefix + mac;
                device.setMacAddress(mac);
                if (mGattApi == null) {
                    mGattApi = new GattApi(this, device, mGattListener);
                }
                break;
        }
    }
}
