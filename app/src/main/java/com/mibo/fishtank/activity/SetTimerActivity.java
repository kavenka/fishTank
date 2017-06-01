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
import com.mibo.fishtank.FishTankmManage.event.SetParamsEvent;
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
public class SetTimerActivity extends BaseActivity implements View.OnClickListener, TimerView.OnToggleChangeListener {

    private String uid;
    private int switchNo;

    private TimerView timerView1, timerView2, timerView3, timerView4;
    private DeviceParams deviceParams;
    private DeviceParams.Alarm[] alarms;

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
        String uid = getIntent().getStringExtra("uid");
        if (TextUtils.isEmpty(uid)) {
            Toast.makeText(this, "设备异常", Toast.LENGTH_SHORT).show();
        } else {
            this.uid = uid;
            // TODO: 2017/5/30 do something
        }
        int switchNo = getIntent().getIntExtra("switchNo", -1);
        if (switchNo == -1) {
            Toast.makeText(this, "设备异常", Toast.LENGTH_SHORT).show();
        } else {
            this.switchNo = switchNo;
            // TODO: 2017/5/30 do something
        }
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        initView();
        setLoacalData();
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
        titleBar.setRightStr("保存");
        titleBar.setOnClickRightListener(new OnClickSaveListener());
        titleBar.setOnClickLeftListener(new OnClickLeftListener());

        this.timerView1 = (TimerView) findViewById(R.id.timerView1);
        this.timerView2 = (TimerView) findViewById(R.id.timerView2);
        this.timerView3 = (TimerView) findViewById(R.id.timerView3);
        this.timerView4 = (TimerView) findViewById(R.id.timerView4);

        this.timerView1.setIndexText(1);
        this.timerView2.setIndexText(2);
        this.timerView3.setIndexText(3);
        this.timerView4.setIndexText(4);

        this.timerView1.setOnClickListener(this);
        this.timerView2.setOnClickListener(this);
        this.timerView3.setOnClickListener(this);
        this.timerView4.setOnClickListener(this);

        this.timerView1.setOnToggleChangeListener(this);
        this.timerView2.setOnToggleChangeListener(this);
        this.timerView3.setOnToggleChangeListener(this);
        this.timerView4.setOnToggleChangeListener(this);

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
        setTimerData(timerView1, alarms[0]);
        setTimerData(timerView2, alarms[1]);
        setTimerData(timerView3, alarms[2]);
        setTimerData(timerView4, alarms[3]);
        Log.d("monty", "SetTimerActivity -> setLoacalData -> deviceParams : " + (deviceParams == null ? "null" : deviceParams.toString()));
    }

    public void setTimerData(TimerView v, DeviceParams.Alarm alarm) {
        v.setSwitch(alarm.getAlarmSwitch());
        v.setOnOff(alarm.getOnOff());
        v.setTime(alarm.Hour, alarm.Min);
        v.setWeek(alarm.getWeekMask());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.timerView1:
                v.setTag(alarms[0]);
                break;
            case R.id.timerView2:
                v.setTag(alarms[1]);
                break;
            case R.id.timerView3:
                v.setTag(alarms[2]);
                break;
            case R.id.timerView4:
                v.setTag(alarms[3]);
                break;
        }
        showTimeDialog((TimerView) v);
    }

    @Override
    public void onToggleChanged(TimerView v, boolean isChecked) {
        switch (v.getId()) {
            case R.id.timerView1:
                alarms[0].setAlarmSwitch(isChecked);
                break;
            case R.id.timerView2:
                alarms[1].setAlarmSwitch(isChecked);
                break;
            case R.id.timerView3:
                alarms[2].setAlarmSwitch(isChecked);
                break;
            case R.id.timerView4:
                alarms[3].setAlarmSwitch(isChecked);
                break;
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
            FishTankApiManager.getInstance().setTimerParams(uid, alarms);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    void onSetTimerListener(SetParamsEvent event) {
        if (event.result == 0) {
            deviceParams.Alarms = alarms;
            DeviceParamsUtil.saveDeviceParams(this, event.uid, deviceParams);
            Toast.makeText(this, "设置成功", Toast.LENGTH_SHORT).show();
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
            }
        }).setPositiveButton("定时开启", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                alarm.setOnOff(true);
                v.setOnOff(true);
            }
        }).show();
    }
}
