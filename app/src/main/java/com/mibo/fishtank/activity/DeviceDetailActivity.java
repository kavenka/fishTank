package com.mibo.fishtank.activity;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.landstek.iFishTank.IFishTankApi;
import com.mibo.fishtank.FishTankmManage.DeviceParams;
import com.mibo.fishtank.FishTankmManage.DeviceParamsUtil;
import com.mibo.fishtank.FishTankmManage.FishTankApiManager;
import com.mibo.fishtank.FishTankmManage.SharedPreferencesUtil;
import com.mibo.fishtank.FishTankmManage.event.GetParameterEvent;
import com.mibo.fishtank.FishTankmManage.event.LoginEvent;
import com.mibo.fishtank.FishTankmManage.event.SetParamsEvent;
import com.mibo.fishtank.R;
import com.mibo.fishtank.entity.Device;
import com.mibo.fishtank.utils.DataBaseManager;
import com.mibo.fishtank.weight.DeviceSwitch.DeviceSwitchView;
import com.mibo.fishtank.weight.LoadingDialog;
import com.mibo.fishtank.weight.TitleBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Monty on 2017/5/29.
 */
public class DeviceDetailActivity extends BaseActivity implements DeviceSwitchView.OnSwitchClickListener {

    private LoadingDialog loadingDialog;
//    private String uid = "5CCF7F07B170";
//    private String uid = "5CCF7F07AB24";

    private TextView mTvTempLevel = null;
    private TextView mTvPhLevel = null;
    private TextView mTvTemp = null;
    private TextView mTvPh = null;

    private DeviceSwitchView mDsvLight1;
    private DeviceSwitchView mDsvLight2;
    private DeviceSwitchView mDsvHeater1;
    private DeviceSwitchView mDsvHeater2;
    private DeviceSwitchView mDsvPump;
    private DeviceSwitchView mDsvOxygenPump;
    private DeviceSwitchView mDsvRfu1;
    private DeviceSwitchView mDsvRfu2;

    private DeviceSwitchView mCurrentSwitchView;

    private Device mDevice = null;

    private Map<Integer, Animator> animatorMap;

    private ValueAnimator phAnimator;
    private ValueAnimator tempAnimator;

    private Handler handler = new Handler();

    private long intervalTime = 1000 * 10;

    private class TimingRunable implements Runnable {

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    FishTankApiManager.getInstance().getDeviceParam(mDevice.getUid());
                }
            });
            handler.postDelayed(this, intervalTime);
        }
    }


    public static Intent BuildIntent(Context context, String deviceUid, String title) {
        Intent intent = new Intent(context, DeviceDetailActivity.class);
        intent.putExtra("deviceUid", deviceUid);
        intent.putExtra("title", title);
        return intent;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("monty", "onStart");
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        loadingDialog.show();
        setDeviceParams(DeviceParamsUtil.getDeviceParams(this, mDevice.getUid()));
        FishTankApiManager.getInstance().loginDevice(mDevice.getUid(), mDevice.getDevPwd());
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        handler.removeCallbacks(runable);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("monty", "onCreate");
        setContentView(R.layout.device_detail_activity);

        initView();

        Device device = DataBaseManager.queryDevice(getIntent().getStringExtra("deviceUid"));

        if (null != device) {
            mDevice = device;
        }

        animatorMap = new HashMap<>();
//        mDevice.setDevPwd("1111");
    }

    @Override
    protected void onDestroy() {
        runable = null;
        handler = null;
        super.onDestroy();

    }

    private TimingRunable runable = new TimingRunable();

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginLstener(LoginEvent event) {
        if (event.loginSuccess) {
            FishTankApiManager.getInstance().getDeviceParam(mDevice.getUid());
            handler.postDelayed(runable, intervalTime);
        } else {
            if (event.result == -2) {
                Toast.makeText(this, "设备密码错误或者超时", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "登录失败", Toast.LENGTH_SHORT).show();
            }
            loadingDialog.close();
        }
    }

    private int failureCount = 0;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetParameterLstener(GetParameterEvent event) {
        IFishTankApi.MsgGetParamRsp msgGetParamRsp = event.msgGetParamRsp;
        String uid = event.uid;
        int result = event.result;
        if (result == 0) {
            failureCount = 0;
            Log.d("monty", "设备参数获取成功，更新到界面上");
            setDeviceParams(DeviceParamsUtil.parseMsgGetParamRep2DeviceParams(msgGetParamRsp));
            boolean b = DeviceParamsUtil.saveDeviceParams(this, uid, msgGetParamRsp);
            if (b) {
                Log.d("monty", "参数保存成功");
            } else {
                Log.d("monty", "参数保存失败");
            }
        } else {
            failureCount++;
            if (failureCount == 5) {
                Toast.makeText(this, "自动获取设备信息失败5次，不再自动获取", Toast.LENGTH_SHORT).show();
                handler.removeCallbacks(runable);
            } else {
                if (event.result == -2) {
                    Toast.makeText(this, "设备密码错误或者超时", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "获取失败", Toast.LENGTH_SHORT).show();
                }
            }
        }
        loadingDialog.close();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSetParamsListener(SetParamsEvent event) {
        if (event.result == 0) {
            if (mCurrentSwitchView != null) {
                mCurrentSwitchView.setSwitch();
            }
        } else {
            if (TextUtils.isEmpty(event.msg)) {
                if (event.result == -2) {
                    Toast.makeText(this, "设备密码错误或者超时", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "操作失败", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, event.msg, Toast.LENGTH_SHORT).show();
            }
        }
        loadingDialog.close();
    }

    private void setPhAnimator(float current, float max, float min) {
        if (phAnimator == null) {
            Integer colorFrom = Color.RED;
            Integer colorTo = Color.YELLOW;
            phAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
            phAnimator.setRepeatCount(ValueAnimator.INFINITE);
            phAnimator.setRepeatMode(ValueAnimator.RESTART);
            phAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    mTvPh.setTextColor((int) valueAnimator.getAnimatedValue());
                    mTvPhLevel.setTextColor((int) valueAnimator.getAnimatedValue());
                }
            });
            phAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    mTvPh.setTextColor(getResources().getColor(R.color.color_00ffaa));
                    mTvPhLevel.setTextColor(getResources().getColor(R.color.color_00ffaa));
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                    mTvPh.setTextColor(getResources().getColor(R.color.color_00ffaa));
                    mTvPhLevel.setTextColor(getResources().getColor(R.color.color_00ffaa));
                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
        }

        if (isNormal(current, max, min)) {
            phAnimator.cancel();
            mTvPh.setTextColor(getResources().getColor(R.color.color_00ffaa));
            mTvPhLevel.setTextColor(getResources().getColor(R.color.color_00ffaa));
        } else {
            phAnimator.start();
        }
    }

    private void setTempAnimator(float current, float max, float min) {
        if (tempAnimator == null) {
            Integer colorFrom = Color.RED;
            Integer colorTo = Color.YELLOW;
            tempAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
            tempAnimator.setRepeatCount(ValueAnimator.INFINITE);
            tempAnimator.setRepeatMode(ValueAnimator.RESTART);
            tempAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    mTvTemp.setTextColor((int) valueAnimator.getAnimatedValue());
                    mTvTempLevel.setTextColor((int) valueAnimator.getAnimatedValue());
                }
            });
            tempAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    mTvTemp.setTextColor(getResources().getColor(R.color.color_FFD400));
                    mTvTempLevel.setTextColor(getResources().getColor(R.color.color_FFD400));
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                    mTvTemp.setTextColor(getResources().getColor(R.color.color_FFD400));
                    mTvTempLevel.setTextColor(getResources().getColor(R.color.color_FFD400));
                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
        }

        if (isNormal(current, max, min)) {
            tempAnimator.cancel();
            mTvTemp.setTextColor(getResources().getColor(R.color.color_FFD400));
            mTvTempLevel.setTextColor(getResources().getColor(R.color.color_FFD400));
        } else {
            tempAnimator.start();
        }
    }

//    private void setAnimation(TextView view, float current, float max, float min, Integer defaultColor) {
//        if (isNormal(current, max, min)) {
//            Animator animator = animatorMap.get(view.getId());
//            if (animator != null) {
//                animator.end();
//                animatorMap.remove(view.getId());
//            }
//        } else {
//            startAnimation(view, defaultColor);
//        }
//    }

    public void setDeviceParams(DeviceParams deviceParams) {
        if (deviceParams == null) {
            return;
        }
        mTvPh.setText(deviceParams.Ph + "");
        mTvTemp.setText(deviceParams.Temp + "");

        mTvPhLevel.setText(sumLevel(deviceParams.Ph, deviceParams.PhMax, deviceParams.PhMin));
        mTvTempLevel.setText(sumLevel(deviceParams.Temp, deviceParams.TempMax, deviceParams.TempMin));

//        setAnimation(mTvPh, deviceParams.Ph, deviceParams.PhMax, deviceParams.PhMin, R.color.color_00ffaa);
//        setAnimation(mTvPhLevel, deviceParams.Ph, deviceParams.PhMax, deviceParams.PhMin, R.color.color_00ffaa);
//
//        setAnimation(mTvTemp, deviceParams.Temp, deviceParams.TempMax, deviceParams.TempMin, R.color.color_FFD400);
//        setAnimation(mTvTempLevel, deviceParams.Temp, deviceParams.TempMax, deviceParams.TempMin, R.color.color_FFD400);

        setPhAnimator(deviceParams.Ph, deviceParams.PhMax, deviceParams.PhMin);
        setTempAnimator(deviceParams.Temp, deviceParams.TempMax, deviceParams.TempMin);

        mDsvLight1.setSwitch(deviceParams.Light1);
        mDsvLight2.setSwitch(deviceParams.Light2);
        mDsvHeater1.setSwitch(deviceParams.Heater1);
        mDsvHeater2.setSwitch(deviceParams.Heater2);
        mDsvPump.setSwitch(deviceParams.Pump);
        mDsvOxygenPump.setSwitch(deviceParams.OxygenPump);
        mDsvRfu1.setSwitch(deviceParams.Rfu1);
        mDsvRfu2.setSwitch(deviceParams.Rfu2);

//        List<DeviceSwitch> deviceSwitches = generateSwitchParameter(msgGetParamRsp.Light1, msgGetParamRsp.Light2, msgGetParamRsp.Heater1, msgGetParamRsp.Heater2, msgGetParamRsp.Pump, msgGetParamRsp.OxygenPump, msgGetParamRsp.Rfu1, msgGetParamRsp.Rfu2);

//        deviceSwitchAdapter.notifyDataSetChanged(deviceSwitches);
    }

    private boolean isNormal(float current, float max, float min) {
        if (current < min || current > max) {
            return false;
        }
        return true;
    }

//    private void setAnimation(TextView view,float current,float max,float min){
//        if (max == 0) {
//            max = Float.MAX_VALUE;
//        }
//
//        if (current < min || current > max) {
//            startAnimation(view);
//        }else{
//            Animator animator = animatorMap.get(view.getId());
//            if(animator!=null){
//                animator.cancel();
//            }
//        }
//    }

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

    //创建动画一个从红色到黄色的动画一直闪
    public void startAnimation(final TextView view, final Integer defaultColor) {
        ValueAnimator animator = (ValueAnimator) animatorMap.get(view.getId());
        if (animator == null) {
            Integer colorFrom = Color.RED;
            Integer colorTo = Color.YELLOW;
            animator = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setRepeatMode(ValueAnimator.RESTART);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    view.setTextColor((int) valueAnimator.getAnimatedValue());
                }
            });
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    view.setTextColor(getResources().getColor(defaultColor));
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                    view.setTextColor(getResources().getColor(defaultColor));
                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            animatorMap.put(view.getId(), animator);
        } else {
            animator = (ValueAnimator) animatorMap.get(view.getId());
        }
        animator.start();
    }

    private void initView() {
        TitleBar titleBar = (TitleBar) findViewById(R.id.device_detail_title);
        titleBar.setCenterStr(getIntent().getStringExtra("title"));
        titleBar.setRightStr("设置");

        loadingDialog = new LoadingDialog(context, "加载中");

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

        mDsvRfu1.setSwitchTitleEditEnable(true);
        mDsvRfu2.setSwitchTitleEditEnable(true);

        String dsvRfu1Title = SharedPreferencesUtil.getString(this, mDsvRfu1.getId() + "", "自定义1");
        mDsvRfu1.setSwitchTitle(dsvRfu1Title);

        String dsvRfu2Title = SharedPreferencesUtil.getString(this, mDsvRfu2.getId() + "", "自定义2");
        mDsvRfu2.setSwitchTitle(dsvRfu2Title);
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
                msgSetParamCmd.Rfu2 = isOpen;
                break;
        }
        FishTankApiManager.getInstance().setDeviceParam(mDevice.getUid(), msgSetParamCmd);
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
            Intent intent = DeviceParamsActivity.BuildIntent(context, mDevice.getUid());
            startActivity(intent);
        }
    }
}
