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

import com.mibo.fishtank.BuildConfig;
import com.mibo.fishtank.FishTankmManage.DeviceParams;
import com.mibo.fishtank.FishTankmManage.DeviceParamsUtil;
import com.mibo.fishtank.FishTankmManage.FishTankApiManager;
import com.mibo.fishtank.R;
import com.mibo.fishtank.weight.RangeSelectionView;
import com.mibo.fishtank.weight.TitleBar;

public class DeviceParamsActivity extends BaseActivity {

    private String uid;

    private RangeSelectionView rangePh;
    private RangeSelectionView rangeTemp;

    private DeviceParams deviceParams;


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

        setLoacalData();


    }

    /**
     * 获取本地缓存数据设置到界面上
     */
    private void setLoacalData() {
        // 获取本地缓存数据
        deviceParams = DeviceParamsUtil.getDeviceParams(this, uid);
        if (deviceParams != null) {
            // 当ph最大值为0时，设置最大值为16
            deviceParams.PhMax = deviceParams.PhMax == 0.0f ? 16 : deviceParams.PhMax;
            // 当temp最大值为0时，设置最大值为40
            deviceParams.TempMax = (int) deviceParams.TempMax == 0 ? 40 : deviceParams.TempMax;

            rangePh.setRange(deviceParams.PhMin, deviceParams.PhMax);
            rangeTemp.setRange(deviceParams.TempMin, deviceParams.TempMax);
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

        RelativeLayout deng1Layout = (RelativeLayout) findViewById(R.id.dengguang1_layout);
        RelativeLayout deng2Layout = (RelativeLayout) findViewById(R.id.dengguang2_layout);
        RelativeLayout deng3Layout = (RelativeLayout) findViewById(R.id.dengguang3_layout);
        RelativeLayout deng4Layout = (RelativeLayout) findViewById(R.id.dengguang4_layout);
        deng1Layout.setOnClickListener(new OnClickDengListener());
        deng2Layout.setOnClickListener(new OnClickDengListener());
        deng3Layout.setOnClickListener(new OnClickDengListener());
        deng4Layout.setOnClickListener(new OnClickDengListener());

        rangePh = (RangeSelectionView) findViewById(R.id.range_ph);
        rangeTemp = (RangeSelectionView) findViewById(R.id.range_temp);
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
            if(BuildConfig.DEBUG){
                deviceParams.PhMin = 1.0f;
                deviceParams.PhMax = 25.0f;
                deviceParams.TempMin = 0.0f;
                deviceParams.TempMax = 39.0f;
            }

            FishTankApiManager.getInstance().setPhAndTempParam(uid, deviceParams.PhMin, deviceParams.PhMax, deviceParams.TempMin, deviceParams.TempMax);
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

    private class OnClickDengListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, SetTimerActivity.class);
            startActivity(intent);
        }
    }
}
