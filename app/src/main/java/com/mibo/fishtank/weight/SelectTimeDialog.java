package com.mibo.fishtank.weight;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.mibo.fishtank.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.mibo.fishtank.R.style.dialog;
import static java.lang.Integer.parseInt;

/**
 * Created by Administrator
 * on 2017/5/23 0023.
 */

public class SelectTimeDialog extends Dialog {

    private TextView btnConfirm;
    private PickerView pvHour;
    private PickerView pvMinute;

    private OnTimeSelectListener mOnTimeSelectListener;

    public SelectTimeDialog(Context context, OnTimeSelectListener onTimeSelectListener) {
        super(context, dialog);

        this.mOnTimeSelectListener = onTimeSelectListener;
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_time_dialog_layout);

        initView();
    }


    private void initView() {
        pvHour = (PickerView) findViewById(R.id.minute_pv);
        pvMinute = (PickerView) findViewById(R.id.second_pv);
        final List<String> hours = new ArrayList<String>();
        List<String> minutes = new ArrayList<String>();
        for (int i = 0; i < 24; i++) {
            hours.add(i < 10 ? "0" + i : "" + i);
        }
        for (int i = 0; i < 60; i++) {
            minutes.add(i < 10 ? "0" + i : "" + i);
        }
        pvHour.setData(hours);

        pvMinute.setData(minutes);

        btnConfirm = (TextView) findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = -1;
                int minute = -1;

                try {
                    hour = parseInt(pvHour.getCurrentSelect());
                    minute = Integer.parseInt(pvMinute.getCurrentSelect());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                mOnTimeSelectListener.onSelect(hour, minute);
                dismiss();
            }
        });
    }

    public interface OnTimeSelectListener {
        void onSelect(int hour, int minute);
    }

    public void show(@NonNull int hour, @NonNull int minute) {
        super.show();
        if (hour > 24 || hour < 0 || minute > 60 || minute < 0) {
            new IllegalArgumentException("参数错误");
        }
        this.pvHour.setSelected(hour);
        this.pvMinute.setSelected(minute);

        Window window = this.getWindow();
        window.setGravity(Gravity.CENTER); //可设置dialog的位置
        window.getDecorView().setPadding(0, 0, 0, 0); //消除边距

        WindowManager.LayoutParams lp = window.getAttributes();
        int width = window.getWindowManager().getDefaultDisplay().getWidth();
        lp.width = width - (int)(width*0.3);  // 宽度等于屏幕宽度减去屏幕宽度的30%
        window.setAttributes(lp);
    }

    @Override
    public void show() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minute = Calendar.getInstance().get(Calendar.MINUTE);
        show(hour, minute);
    }
}
