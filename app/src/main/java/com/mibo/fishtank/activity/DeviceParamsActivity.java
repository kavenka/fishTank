package com.mibo.fishtank.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mibo.fishtank.FishTankmManage.DeviceParams;
import com.mibo.fishtank.FishTankmManage.DeviceParamsUtil;
import com.mibo.fishtank.FishTankmManage.FishTankApiManager;
import com.mibo.fishtank.FishTankmManage.event.SetParamsEvent;
import com.mibo.fishtank.FishTankmManage.timer.SwitchNumber;
import com.mibo.fishtank.R;
import com.mibo.fishtank.weight.RangeSeekBar;
import com.mibo.fishtank.weight.TitleBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class DeviceParamsActivity extends BaseActivity {

    private String uid;

    private RangeSeekBar rangePh;
    private RangeSeekBar rangeTemp;

    private DeviceParams deviceParams;

    private float ph[] = new float[2];
    private float temp[] = new float[2];


    public static Intent BuildIntent(Context context, String uid) {
        Intent intent = new Intent(context, DeviceParamsActivity.class);
        intent.putExtra("uid", uid);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_params_activity);
        initView();

        String uid = getIntent().getStringExtra("uid");
        if (TextUtils.isEmpty(uid)) {
            Toast.makeText(this, "设备异常", Toast.LENGTH_SHORT).show();
        } else {
            this.uid = uid;
            // TODO: 2017/5/30 do something
        }
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        setLoacalData();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    void onSetTimerListener(SetParamsEvent event) {
        if (event.result == 0) {
            DeviceParamsUtil.saveDeviceParams(this, event.uid, deviceParams);
            Toast.makeText(this, "设置成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "设置失败", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 获取本地缓存数据设置到界面上
     */
    private void setLoacalData() {
        // 获取本地缓存数据
        deviceParams = DeviceParamsUtil.getDeviceParams(this, uid);
        if (deviceParams != null) {
//            // 当ph最大值为0时，设置最大值为16
//            deviceParams.PhMax = deviceParams.PhMax == 0.0f ? 16 : deviceParams.PhMax;
//            // 当temp最大值为0时，设置最大值为40
//            deviceParams.TempMax = (int) deviceParams.TempMax == 0 ? 40 : deviceParams.TempMax;
            rangePh.setSelectedMinValue(deviceParams.PhMin);
            rangePh.setSelectedMaxValue(deviceParams.PhMax);
            rangeTemp.setSelectedMinValue( deviceParams.TempMin);
            rangeTemp.setSelectedMaxValue( deviceParams.TempMax);
        }
        Log.d("monty", "DeviceParamsActivity -> setLoacalData -> deviceParams : " + (deviceParams == null ? "null" : deviceParams.toString()));
    }

    private void initView() {
        TitleBar titleBar = (TitleBar) findViewById(R.id.set_params_title);
        titleBar.setCenterStr(R.string.device_connect);
        titleBar.setRightStr("保存");
        titleBar.setOnClickRightListener(new OnClickSaveListener());
        titleBar.setOnClickLeftListener(new OnClickLeftListener());

        Button editNumBtn = (Button) findViewById(R.id.edit_num);
        Button editPwdBtn = (Button) findViewById(R.id.edit_pwd);
        editNumBtn.setOnClickListener(new OnClickEditNumListener());
        editPwdBtn.setOnClickListener(new OnClickEditPwdListener());

        RelativeLayout light1Layout = (RelativeLayout) findViewById(R.id.dengguang1_layout);
        RelativeLayout light2Layout = (RelativeLayout) findViewById(R.id.dengguang2_layout);
        RelativeLayout rfu1Layout = (RelativeLayout) findViewById(R.id.dengguang3_layout);
        RelativeLayout rfu2Layout = (RelativeLayout) findViewById(R.id.dengguang4_layout);
        light1Layout.setOnClickListener(new OnClickListener());
        light1Layout.setTag(SwitchNumber.SWitchLight1);
        light2Layout.setOnClickListener(new OnClickListener());
        light2Layout.setTag(SwitchNumber.SWitchHeater2);
        rfu1Layout.setOnClickListener(new OnClickListener());
        rfu1Layout.setTag(SwitchNumber.SWitchRfu1);
        rfu2Layout.setOnClickListener(new OnClickListener());
        rfu2Layout.setTag(SwitchNumber.SWitchRfu2);

        rangePh = (RangeSeekBar) findViewById(R.id.range_ph);
        rangePh.setRangeValues(0.0f,14f);
        rangePh.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Object minValue, Object maxValue) {
                deviceParams.PhMin = (float) minValue;
                deviceParams.PhMax = (float) maxValue;
//                ph[0] = (float) minValue;
//                ph[1] = (float) maxValue;
            }
        });
        rangeTemp = (RangeSeekBar) findViewById(R.id.range_temp);
        rangeTemp.setRangeValues(0.0f,40.0f);
        rangeTemp.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Object minValue, Object maxValue) {
                deviceParams.TempMin = (float) minValue;
                deviceParams.TempMax = (float) maxValue;
//                temp[0] = (float) minValue;
//                temp[1] = (float) maxValue;
            }
        });
        // Set the range

    }

    private class OnClickLeftListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            finish();
        }
    }

    private class OnClickSaveListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            FishTankApiManager.getInstance().setPhAndTempParams(uid, deviceParams);
        }
    }

    private class OnClickEditNumListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = EditPushNumActivity.BuildIntent(context, uid);
            startActivity(intent);
        }
    }

    private class OnClickEditPwdListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, SetUserPwdActivity.class);
            startActivity(intent);
        }
    }

    private class OnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int switchId = (int) v.getTag();
            Intent intent = SetTimerActivity.BuildIntent(DeviceParamsActivity.this, uid, switchId);
            startActivity(intent);
        }
    }
}
