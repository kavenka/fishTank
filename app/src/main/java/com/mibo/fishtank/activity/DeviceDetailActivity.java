package com.mibo.fishtank.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.landstek.iFishTank.IFishTankApi;
import com.mibo.fishtank.FishTankmManage.FishTankApiManager;
import com.mibo.fishtank.FishTankmManage.event.GetParameterEvent;
import com.mibo.fishtank.FishTankmManage.event.LoginEvent;
import com.mibo.fishtank.FishTankmManage.event.SetParamsEvent;
import com.mibo.fishtank.R;
import com.mibo.fishtank.weight.DeviceSwitch.DeviceSwitchView;
import com.mibo.fishtank.weight.LoadingDialog;
import com.mibo.fishtank.weight.TitleBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class DeviceDetailActivity extends BaseActivity implements DeviceSwitchView.OnSwitchClickListener {

    private LoadingDialog loadingDialog;
    private String mDstDevUid = "5CCF7F07B170";

    private TextView mTvTempLevel = null;
    private TextView mTvPhLevel = null;
    private TextView mTvTemp = null;
    private TextView mTvPh = null;
//    private GridView mSwitchGrid = null;
//    private DeviceSwitchAdapter deviceSwitchAdapter = null;

    private DeviceSwitchView mDsvLight1;
    private DeviceSwitchView mDsvLight2;
    private DeviceSwitchView mDsvHeater1;
    private DeviceSwitchView mDsvHeater2;
    private DeviceSwitchView mDsvPump;
    private DeviceSwitchView mDsvOxygenPump;
    private DeviceSwitchView mDsvRfu1;
    private DeviceSwitchView mDsvRfu2;

    private DeviceSwitchView mCurrentSwitchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_detail_activity);
        initView();
        loadingDialog.show();

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        loadingDialog.show();
        FishTankApiManager.getInstance().loginDevice(mDstDevUid);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginLstener(LoginEvent event) {
        if (event.loginSuccess) {
            FishTankApiManager.getInstance().getDeviceParam(mDstDevUid);
        } else {
            Toast.makeText(this, "登录失败", Toast.LENGTH_SHORT).show();
            loadingDialog.close();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetParameterLstener(GetParameterEvent event) {
        IFishTankApi.MsgGetParamRsp msgGetParamRsp = event.msgGetParamRsp;
        String uid = event.uid;
        int result = event.result;
        if (result == 0) {
            Log.d("monty", "参数获取成功，更新到界面上");
            setDeviceParams(msgGetParamRsp);
        } else {
            Toast.makeText(this, "获取失败", Toast.LENGTH_SHORT).show();
        }
        loadingDialog.close();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSetParamsListener(SetParamsEvent event){
        if(event.result == 0){
            if(mCurrentSwitchView!=null){
                mCurrentSwitchView.setSwitch();
            }
        }else{
            Toast.makeText(this, "操作失败", Toast.LENGTH_SHORT).show();
        }
        loadingDialog.close();
    }

    public void setDeviceParams(IFishTankApi.MsgGetParamRsp msgGetParamRsp) {
        mTvPh.setText(msgGetParamRsp.Ph + "");
        mTvTemp.setText(msgGetParamRsp.Temp + "");

        mTvPhLevel.setText(sumLevel(msgGetParamRsp.Ph, msgGetParamRsp.PhMax, msgGetParamRsp.PhMin));
        mTvTempLevel.setText(sumLevel(msgGetParamRsp.Temp, msgGetParamRsp.TempMax, msgGetParamRsp.TempMin));


        mDsvLight1.setSwitch(msgGetParamRsp.Light1);
        mDsvLight2.setSwitch(msgGetParamRsp.Light2);
        mDsvHeater1.setSwitch(msgGetParamRsp.Heater1);
        mDsvHeater2.setSwitch(msgGetParamRsp.Heater2);
        mDsvPump.setSwitch(msgGetParamRsp.Pump);
        mDsvOxygenPump.setSwitch(msgGetParamRsp.OxygenPump);
        mDsvRfu1.setSwitch(msgGetParamRsp.Rfu1);
        mDsvRfu2.setSwitch(msgGetParamRsp.Rfu2);


//        List<DeviceSwitch> deviceSwitches = generateSwitchParameter(msgGetParamRsp.Light1, msgGetParamRsp.Light2, msgGetParamRsp.Heater1, msgGetParamRsp.Heater2, msgGetParamRsp.Pump, msgGetParamRsp.OxygenPump, msgGetParamRsp.Rfu1, msgGetParamRsp.Rfu2);

//        deviceSwitchAdapter.notifyDataSetChanged(deviceSwitches);
    }

    private String sumLevel(float current, float max, float min) {

        if (max == 0) {
            max = Float.MAX_VALUE;
        }

        if (current < min) {
            return "偏低";
        } else if (current > max) {
            return "偏高";
        } else {
            return "正常";
        }

    }


    private void initView() {
        TitleBar titleBar = (TitleBar) findViewById(R.id.device_detail_title);
        titleBar.setCenterStr(R.string.set_params_title);
        titleBar.setRightStr("设置");

        loadingDialog = new LoadingDialog(context, "加载中");
        loadingDialog.setCancelable(false);

        titleBar.setOnClickRightListener(new OnClickEditListener());
        titleBar.setOnClickLeftListener(new OnClickLeftListener());

        mTvPh = (TextView) findViewById(R.id.tv_Ph);
        mTvPhLevel = (TextView) findViewById(R.id.tv_ph_level);
        mTvTemp = (TextView) findViewById(R.id.tv_Temp);
        mTvTempLevel = (TextView) findViewById(R.id.tv_Temp_level);

        mDsvLight1 = (DeviceSwitchView) findViewById(R.id.dsv_Light1);
        mDsvLight2 = (DeviceSwitchView) findViewById(R.id.dsv_Light2);
        mDsvHeater1 = (DeviceSwitchView) findViewById(R.id.dsv_Heater1);
        mDsvHeater2 = (DeviceSwitchView) findViewById(R.id.dsv_Heater2);
        mDsvPump = (DeviceSwitchView) findViewById(R.id.dsv_Pump);
        mDsvOxygenPump = (DeviceSwitchView) findViewById(R.id.dsv_OxygenPump);
        mDsvRfu1 = (DeviceSwitchView) findViewById(R.id.dsv_Rfu1);
        mDsvRfu2 = (DeviceSwitchView) findViewById(R.id.dsv_Rfu2);

        mDsvLight1.setSwitchTitle("照明灯1");
        mDsvLight2.setSwitchTitle("照明灯2");
        mDsvHeater1.setSwitchTitle("加热棒1");
        mDsvHeater2.setSwitchTitle("加热棒2");
        mDsvPump.setSwitchTitle("水泵");
        mDsvOxygenPump.setSwitchTitle("氧气泵");
        mDsvRfu1.setSwitchTitle("自定义1");
        mDsvRfu2.setSwitchTitle("自定义2");

        mDsvLight1.setSwitchIcon(R.drawable.zhaom);
        mDsvLight2.setSwitchIcon(R.drawable.zhaom);
        mDsvHeater1.setSwitchIcon(R.drawable.jiarb_white);
        mDsvHeater2.setSwitchIcon(R.drawable.jiarb_white);
        mDsvPump.setSwitchIcon(R.drawable.shuib);
        mDsvOxygenPump.setSwitchIcon(R.drawable.shuib);
        mDsvRfu1.setSwitchIcon(R.drawable.zidy_white);
        mDsvRfu2.setSwitchIcon(R.drawable.zidy_white);

        mDsvLight1.setOnSwitchClickListener(this);
        mDsvLight2.setOnSwitchClickListener(this);
        mDsvHeater1.setOnSwitchClickListener(this);
        mDsvHeater2.setOnSwitchClickListener(this);
        mDsvPump.setOnSwitchClickListener(this);
        mDsvOxygenPump.setOnSwitchClickListener(this);
        mDsvRfu1.setOnSwitchClickListener(this);
        mDsvRfu2.setOnSwitchClickListener(this);


//        mSwitchGrid = (GridView) findViewById(R.id.gv_device_switch);


//        deviceSwitchAdapter = new DeviceSwitchAdapter(this, generateSwitchParameter(true,true,true,true,true,true,true,true));
//        mSwitchGrid.setAdapter(deviceSwitchAdapter);
    }


    @Override
    public void onSwitch(View v, boolean isOpen) {
        mCurrentSwitchView = (DeviceSwitchView) v;
        loadingDialog.show();
        IFishTankApi.MsgSetParamCmd msgSetParamCmd = null;
        switch (v.getId()) {
            case R.id.dsv_Light1:
                msgSetParamCmd = new IFishTankApi.MsgSetParamCmd();
                msgSetParamCmd.Light1 = isOpen;
                break;
            case R.id.dsv_Light2:
                msgSetParamCmd = new IFishTankApi.MsgSetParamCmd();
                msgSetParamCmd.Light2 = isOpen;
                break;
            case R.id.dsv_Heater1:
                msgSetParamCmd = new IFishTankApi.MsgSetParamCmd();
                msgSetParamCmd.Heater1 = isOpen;
                break;
            case R.id.dsv_Heater2:
                msgSetParamCmd = new IFishTankApi.MsgSetParamCmd();
                msgSetParamCmd.Heater2 = isOpen;
                break;
            case R.id.dsv_Pump:
                msgSetParamCmd = new IFishTankApi.MsgSetParamCmd();
                msgSetParamCmd.Pump = isOpen;
                break;
            case R.id.dsv_OxygenPump:
                msgSetParamCmd = new IFishTankApi.MsgSetParamCmd();
                msgSetParamCmd.OxygenPump = isOpen;
                break;
            case R.id.dsv_Rfu1:
                msgSetParamCmd = new IFishTankApi.MsgSetParamCmd();
                msgSetParamCmd.Rfu1 = isOpen;
                break;
            case R.id.dsv_Rfu2:
                msgSetParamCmd = new IFishTankApi.MsgSetParamCmd();
                msgSetParamCmd.Rfu1 = isOpen;
                break;
        }
        FishTankApiManager.getInstance().setDeviceParam(mDstDevUid, msgSetParamCmd);
    }

//    private List<DeviceSwitch> generateSwitchParameter(boolean Light1, boolean Light2, boolean Heater1, boolean Heater2, boolean Pump, boolean OxygenPump, boolean Rfu1, boolean Rfu2) {
//        List<DeviceSwitch> deviceSitchs = new ArrayList<>(8);
//        deviceSitchs.add(new DeviceSwitch("照明灯1",Light1));
//        deviceSitchs.add(new DeviceSwitch("照明灯2",Light2));
//        deviceSitchs.add(new DeviceSwitch("加热棒1",Heater1));
//        deviceSitchs.add(new DeviceSwitch("加热棒2",Heater2));
//        deviceSitchs.add(new DeviceSwitch("水泵",Pump));
//        deviceSitchs.add(new DeviceSwitch("氧气泵",OxygenPump));
//        deviceSitchs.add(new DeviceSwitch("自定义1",Rfu1));
//        deviceSitchs.add(new DeviceSwitch("自定义2",Rfu2));
//        return deviceSitchs;
//
//    }

    private class OnClickLeftListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            finish();
        }
    }

    private class OnClickEditListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, SetParamsActivity.class);
            startActivity(intent);
        }
    }
}
