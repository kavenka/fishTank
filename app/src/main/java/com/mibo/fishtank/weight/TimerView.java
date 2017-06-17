package com.mibo.fishtank.weight;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.mibo.fishtank.FishTankmManage.DeviceParamsUtil;
import com.mibo.fishtank.R;

import static com.mibo.fishtank.R.array.week;

/**
 * Created by Monty on 2017/5/31.
 */

public class TimerView extends RelativeLayout {

    private ImageView imageView;
    private TextView tvTime;
    private TextView tvWeek;
    private TextView tvSwitch;
    private ToggleButton toggleButton;

    private String[] weeks = getResources().getStringArray(week);

    public TimerView(Context context) {
        this(context, null);
    }

    public TimerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.timer_layout, this, true);
        this.imageView = (ImageView) findViewById(R.id.tv_index);
        this.tvTime = (TextView) findViewById(R.id.tv_time);
        this.tvSwitch = (TextView) findViewById(R.id.tv_switch);
        this.tvWeek = (TextView) findViewById(R.id.tv_week);
        this.toggleButton = (ToggleButton) findViewById(R.id.togglebutton);
    }

    public void setTime(int hour, int minute) {
        this.tvTime.setText(formatTime2String(hour, minute));
    }

    private String formatTime2String(int hour, int minute) {
        StringBuffer sb = new StringBuffer();
        if (hour < 10) {
            sb.append("0" + hour);
        } else {
            sb.append(hour + "");
        }
        sb.append(":");
        if (minute < 10) {
            sb.append("0" + minute);
        } else {
            sb.append(minute + "");
        }
        Log.d("monty", "formatTime2String : " + sb.toString());
        return sb.toString();
    }

    public int[] getTime() {
        String text = this.tvTime.getText().toString();
        int[] time = new int[2];
        String[] split = text.split(":");
        for (int i = 0; i < split.length; i++) {
            time[i] = Integer.parseInt(split[i]);
        }
        return time;
    }

    public boolean setWeek(boolean[] weeksBool) {
        if (DeviceParamsUtil.checkAvailability(weeksBool)) {
            this.tvWeek.setText(formatWeek(weeksBool));
            return true;
        }
        return false;

    }


    private String formatWeek(boolean[] weeksBool) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < weeksBool.length; i++) {
            if (weeksBool[i]) {
                sb.append(weeks[i] + ",");
            }
        }
        return sb.substring(0, sb.lastIndexOf(","));
    }

    public boolean[] getWeek() {
        String text = this.tvWeek.getText().toString();

        String[] week = new String[7];
        try {
            week = text.split(",");
        } catch (Exception e) {
            e.printStackTrace();
        }
        boolean[] weeksBool = new boolean[7];
        for (int i = 0; i < weeks.length; i++) {
            for (int j = 0; j < week.length; j++) {
                if (weeks[i].equals(week[j])) {
                    weeksBool[i] = true;
                    break;
                } else {
                    weeksBool[i] = false;
                }
            }
        }
        return weeksBool;
    }

    public void setSwitch(boolean onOff) {
        this.toggleButton.setChecked(onOff);
    }

    public boolean getSwitch() {
        return this.toggleButton.isChecked();
    }

    public void setOnOff(boolean isOpen) {
        this.tvSwitch.setText(isOpen ? "开启" : "关闭");
    }

    public void setOnToggleChangeListener(final OnToggleChangeListener onToggleChangeListener) {
        this.toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                onToggleChangeListener.onToggleChanged(TimerView.this, isChecked);
            }
        });
    }

    public interface OnToggleChangeListener {
        void onToggleChanged(TimerView v, boolean isChecked);
    }

    public void setOnToggleClickListner(final OnToggleClickListener onToggleClickListner) {
        this.toggleButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onToggleClickListner.onToggleClicked(TimerView.this);
            }
        });
    }

    public interface OnToggleClickListener {

        void onToggleClicked(TimerView v);
    }
}
