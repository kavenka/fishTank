package com.mibo.fishtank.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.mibo.fishtank.R;
import com.mibo.fishtank.weight.SelectTimeDialog;
import com.mibo.fishtank.weight.SelectWeekDialog;
import com.mibo.fishtank.weight.TitleBar;

import java.util.Arrays;

public class SetTimerActivity extends BaseActivity {

    private int mHour = -1;
    private int mMinute = -1;

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

        RelativeLayout timer1Layout = (RelativeLayout) findViewById(R.id.timer1_layout);
        RelativeLayout timer2Layout = (RelativeLayout) findViewById(R.id.timer2_layout);
        RelativeLayout timer3Layout = (RelativeLayout) findViewById(R.id.timer3_layout);
        RelativeLayout timer4Layout = (RelativeLayout) findViewById(R.id.timer4_layout);
        timer1Layout.setOnClickListener(new OnClickEditTimeListener());
        timer2Layout.setOnClickListener(new OnClickEditTimeListener());
        timer3Layout.setOnClickListener(new OnClickEditTimeListener());
        timer4Layout.setOnClickListener(new OnClickEditTimeListener());


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

    private class OnClickEditTimeListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            SelectTimeDialog selectTimeDialog = new SelectTimeDialog(context, new SelectTimeDialog.OnTimeSelectListener() {
                @Override
                public void onSelect(int hour, int minute) {
                    Log.d("monty", "hour:" + hour + " ,minute:" + minute);
                    mHour = hour;
                    mMinute = minute;
                    new SelectWeekDialog(context, new boolean[]{false, false, false, false, false, false, false}, new SelectWeekDialog.OnWeekSelectListener() {
                        @Override
                        public void onCheck(boolean[] value) {
                            Log.d("monty", "oncheck:" + Arrays.toString(value));
                            showActionDialog();
                        }
                    }).show();
                }
            });
            if (mHour == -1 || mMinute == -1) {
                selectTimeDialog.show();
            } else {
                selectTimeDialog.show(mHour, mMinute);
            }

        }
    }

    private void showActionDialog(){
        AlertDialog dialog = new AlertDialog.Builder(this,R.style.dialog).setTitle("请选择执行的动作").setNegativeButton("定时关闭", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setPositiveButton("定时开启", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();

    }
}
