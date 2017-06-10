package com.mibo.fishtank.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.mibo.fishtank.FishTankmManage.DeviceParams;
import com.mibo.fishtank.FishTankmManage.DeviceParamsUtil;
import com.mibo.fishtank.FishTankmManage.FishTankApiManager;
import com.mibo.fishtank.FishTankmManage.event.SetTimerEvent;
import com.mibo.fishtank.FishTankmManage.timer.SwitchNumber;
import com.mibo.fishtank.R;
import com.mibo.fishtank.weight.SelectTimeDialog;
import com.mibo.fishtank.weight.SelectWeekDialog;
import com.mibo.fishtank.weight.TimerView;
import com.mibo.fishtank.weight.TitleBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;

/**
 * 开关定时设置
 * Created by Monty on 2017/5/30.
 */
public class SetTimerActivity extends BaseActivity implements View.OnClickListener, TimerView.OnToggleChangeListener, TimerView.OnToggleClickListener {

    private String uid;
    private int switchNo;

    private TimerView timerView1, timerView2, timerView3, timerView4;
    private DeviceParams deviceParams;
    private DeviceParams.Alarm[] alarms;
    private int[] indexes = new int[4];

    public static Intent BuildIntent(Context context, String uid, int switchNo) {
        Intent intent = new Intent(context, SetTimerActivity.class);
        intent.putExtra("uid", uid);
        intent.putExtra("switchNo", switchNo);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_timer_activity);

        getIntentData();
        initView();
        setLoacalData();

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }


    private void getIntentData() {
        String uid = getIntent().getStringExtra("uid");
        if (TextUtils.isEmpty(uid)) {
            Toast.makeText(this, "设备异常", Toast.LENGTH_SHORT).show();
        } else {
            this.uid = uid;
        }
        int switchNo = getIntent().getIntExtra("switchNo", -1);
        if (switchNo == -1) {
            Toast.makeText(this, "设备异常", Toast.LENGTH_SHORT).show();
        } else {
            this.switchNo = switchNo;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    private void initView() {
        TitleBar titleBar = (TitleBar) findViewById(R.id.set_timer_title);
        titleBar.setCenterStr(R.string.timer_setting);
        setTitleBarCenterStr(titleBar, switchNo);

        titleBar.setRightStr("保存");
        titleBar.setOnClickRightListener(new OnClickSaveListener());
        titleBar.setOnClickLeftListener(new OnClickLeftListener());

        this.timerView1 = (TimerView) findViewById(R.id.timerView1);
        this.timerView2 = (TimerView) findViewById(R.id.timerView2);
        this.timerView3 = (TimerView) findViewById(R.id.timerView3);
        this.timerView4 = (TimerView) findViewById(R.id.timerView4);

        this.timerView1.setOnClickListener(this);
        this.timerView2.setOnClickListener(this);
        this.timerView3.setOnClickListener(this);
        this.timerView4.setOnClickListener(this);

        this.timerView1.setOnToggleChangeListener(this);
        this.timerView2.setOnToggleChangeListener(this);
        this.timerView3.setOnToggleChangeListener(this);
        this.timerView4.setOnToggleChangeListener(this);

        this.timerView1.setOnToggleClickListner(this);
        this.timerView2.setOnToggleClickListner(this);
        this.timerView3.setOnToggleClickListner(this);
        this.timerView4.setOnToggleClickListner(this);

    }

    private void setTitleBarCenterStr(TitleBar titleBar, int switchNo) {
        switch (switchNo) {
            case SwitchNumber.SWitchLight1:
                titleBar.setCenterStr("灯光1定时器设置");
                break;
            case SwitchNumber.SWitchLight2:
                titleBar.setCenterStr("灯光2定时器设置");
                break;
            case SwitchNumber.SWitchRfu1:
                titleBar.setCenterStr("备用1定时器设置");
                break;
            case SwitchNumber.SWitchRfu2:
                titleBar.setCenterStr("备用2定时器设置");
                break;
            default:
                titleBar.setCenterStr("定时器设置");
                break;
        }
    }

    /**
     * 获取本地缓存数据设置到界面上
     */
    private void setLoacalData() {
        // 获取本地缓存数据
        deviceParams = DeviceParamsUtil.getDeviceParams(this, uid);
        if (deviceParams != null) {
            alarms = deviceParams.Alarms;
        }

        indexes = DeviceParamsUtil.getTimerParamsIndexFromSwitchNo(switchNo, alarms);

        setSwitchNo();

        setTimerData(timerView1, alarms[indexes[0]]);
        setTimerData(timerView2, alarms[indexes[1]]);
        setTimerData(timerView3, alarms[indexes[2]]);
        setTimerData(timerView4, alarms[indexes[3]]);

        timerView1.setTag(alarms[indexes[0]]);
        timerView2.setTag(alarms[indexes[1]]);
        timerView3.setTag(alarms[indexes[2]]);
        timerView4.setTag(alarms[indexes[3]]);

//        Log.d("monty", "SetTimerActivity -> setLoacalData -> deviceParams : " + (deviceParams == null ? "null" : deviceParams.toString()));
    }

    private void setSwitchNo() {
        alarms[indexes[0]].SwNo = (byte) switchNo;
        alarms[indexes[1]].SwNo = (byte) switchNo;
        alarms[indexes[2]].SwNo = (byte) switchNo;
        alarms[indexes[3]].SwNo = (byte) switchNo;
    }

    public void setTimerData(TimerView v, DeviceParams.Alarm alarm) {
        v.setSwitch(alarm.getAlarmSwitch());
        v.setOnOff(alarm.getOnOff());
        v.setTime(alarm.Hour, alarm.Min);
        v.setWeek(alarm.getWeekMask());
    }

    @Override
    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.timerView1:
//                v.setTag(alarms[indexes[0]]);
//                break;
//            case R.id.timerView2:
//                v.setTag(alarms[indexes[1]]);
//                break;
//            case R.id.timerView3:
//                v.setTag(alarms[indexes[2]]);
//                break;
//            case R.id.timerView4:
//                v.setTag(alarms[indexes[3]]);
//                break;
//        }
        showTimeDialog((TimerView) v);
    }

    @Override
    public void onToggleChanged(TimerView v, boolean isChecked) {

        switch (v.getId()) {
            case R.id.timerView1:
                alarms[indexes[0]].setAlarmSwitch(isChecked);
                break;
            case R.id.timerView2:
                alarms[indexes[1]].setAlarmSwitch(isChecked);
                break;
            case R.id.timerView3:
                alarms[indexes[2]].setAlarmSwitch(isChecked);
                break;
            case R.id.timerView4:
                alarms[indexes[3]].setAlarmSwitch(isChecked);
                break;
        }
    }

    @Override
    public void onToggleClicked(TimerView v) {
        // 如果执行打开操作，并且未设置星期，则视为此操作无效，让用户输入星期
        if (v.getSwitch() && !DeviceParamsUtil.checkAvailability(v.getWeek())) {
            v.setSwitch(false);
            Toast.makeText(this, "请先选择星期", Toast.LENGTH_SHORT).show();
            showTimeDialog(v);
            return;
        }
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
            Log.d("monty", "SetTimerActivity -> setTimerParams -> switchNo : " + switchNo);
            Log.d("monty", "SetTimerActivity -> setTimerParams -> indexes : " + Arrays.toString(indexes));

            Log.d("monty", "SetTimerActivity -> setTimerParams -> alarms : " + Arrays.toString(alarms));
            FishTankApiManager.getInstance().setTimerParams(uid, alarms);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSetTimerListener(SetTimerEvent event) {
        if (event.result == 0) {
            deviceParams.Alarms = alarms;
            DeviceParamsUtil.saveDeviceParams(this, event.uid, deviceParams);
            Toast.makeText(this, "定时器设置成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "设置失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void showTimeDialog(final TimerView v) {
        final DeviceParams.Alarm alarm = (DeviceParams.Alarm) v.getTag();
        SelectTimeDialog selectTimeDialog = new SelectTimeDialog(context, new SelectTimeDialog.OnTimeSelectListener() {
            @Override
            public void onSelect(int hour, int minute) {
                Log.d("monty", "hour:" + hour + " ,minute:" + minute);
                v.setTime(hour, minute);
                alarm.Hour = (byte) hour;
                alarm.Min = (byte) minute;
                showWeekDialog(v);
            }
        });
        selectTimeDialog.show(alarm.Hour, alarm.Min);
    }

    private void showWeekDialog(final TimerView v) {
        final DeviceParams.Alarm alarm = (DeviceParams.Alarm) v.getTag();
        new SelectWeekDialog(context, v.getWeek(), new SelectWeekDialog.OnWeekSelectListener() {
            @Override
            public void onCheck(SelectWeekDialog dialog, boolean[] value) {
                Log.d("monty", "oncheck:" + Arrays.toString(value));
                boolean result = v.setWeek(value);
                if (result) {
                    alarm.setWeek(value);
                    dialog.dismiss();
                    showActionDialog(v);
                } else {
                    Toast.makeText(context, "请先选择星期", Toast.LENGTH_SHORT).show();
                }
            }
        }).show();
    }

    private void showActionDialog(final TimerView v) {
        final DeviceParams.Alarm alarm = (DeviceParams.Alarm) v.getTag();
        new AlertDialog.Builder(this, R.style.dialog).setTitle("请选择执行的动作").setNegativeButton("定时关闭", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                alarm.setOnOff(false);
                v.setOnOff(false);
                v.setSwitch(true);
            }
        }).setPositiveButton("定时开启", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                alarm.setOnOff(true);
                v.setOnOff(true);
                v.setSwitch(true);
            }
        }).show();
    }
}
