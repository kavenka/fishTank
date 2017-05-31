package com.mibo.fishtank.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.mibo.fishtank.R;
import com.mibo.fishtank.weight.SelectTimeDialog;
import com.mibo.fishtank.weight.SelectWeekDialog;
import com.mibo.fishtank.weight.TimerView;
import com.mibo.fishtank.weight.TitleBar;

import java.util.Arrays;

public class SetTimerActivity extends BaseActivity implements View.OnClickListener {

    private int mHour = -1;
    private int mMinute = -1;

    private TimerView timerView1, timerView2, timerView3, timerView4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_timer_activity);
        initView();
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

        this.timerView1.setOnClickListener(this);
        this.timerView2.setOnClickListener(this);
        this.timerView3.setOnClickListener(this);
        this.timerView4.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        TimerView timerView = (TimerView) v;
        int[] time = timerView.getTime();
        showTimeDialog((TimerView) v, time[0], time[1]);
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
            finish();
        }
    }


    private void showTimeDialog(final TimerView v, int hour, int minute) {
        SelectTimeDialog selectTimeDialog = new SelectTimeDialog(context, new SelectTimeDialog.OnTimeSelectListener() {
            @Override
            public void onSelect(int hour, int minute) {
                Log.d("monty", "hour:" + hour + " ,minute:" + minute);
                mHour = hour;
                mMinute = minute;
                v.setTime(hour, minute);
                showWeekDialog(v, new boolean[]{false, false, false, false, false, false, false});
            }
        });
        if (mHour == -1 || mMinute == -1) {  // 未设置过默认显示当前时间
            selectTimeDialog.show();
        } else {
            selectTimeDialog.show(hour, minute);
        }
    }

    private void showWeekDialog(final TimerView v, boolean[] weeksBool) {
        new SelectWeekDialog(context, weeksBool, new SelectWeekDialog.OnWeekSelectListener() {
            @Override
            public void onCheck(SelectWeekDialog dialog,boolean[] value) {

                Log.d("monty", "oncheck:" + Arrays.toString(value));
                boolean result = v.setWeek(value);
                if (result) {
                    dialog.dismiss();
                    showActionDialog(v);
                } else {
                    Toast.makeText(context, "请先选择星期", Toast.LENGTH_SHORT).show();
                }
            }
        }).show();
    }

    private void showActionDialog(final TimerView v) {
        new AlertDialog.Builder(this, R.style.dialog).setTitle("请选择执行的动作").setNegativeButton("定时关闭", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                v.setSwitch(false);
            }
        }).setPositiveButton("定时开启", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                v.setSwitch(true);
            }
        }).show();

    }
}
