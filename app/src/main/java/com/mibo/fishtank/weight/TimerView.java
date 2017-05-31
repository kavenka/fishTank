package com.mibo.fishtank.weight;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.mibo.fishtank.R;

import static com.mibo.fishtank.R.array.week;

/**
 * Created by Monty on 2017/5/31.
 */

public class TimerView extends RelativeLayout {

    private TextView tvIndex;
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
        this.tvIndex = (TextView) findViewById(R.id.tv_index);
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
        if (checkAvailability(weeksBool)) {
            this.tvWeek.setText(formatWeek(weeksBool));
            return true;
        }
        return false;

    }

    /**
     * 检查设置星期参数是否有效
     * 说明：有效->最少有一天为选中的
     *
     * @param weeksBool
     * @return
     */
    private boolean checkAvailability(boolean[] weeksBool) {
        if (weeksBool == null || weeksBool.length == 0) {
            return false;
        }
        for (boolean b : weeksBool) {
            if (b) {
                return true;
            }
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
        String Week = this.tvWeek.getText().toString();
        String[] week = Week.split(",");
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

    public void setIndexText(int index) {
        this.tvIndex.setText(index + "");
    }

    public void setSwitch(boolean isOpen) {
        this.tvSwitch.setText(isOpen ? "开启" : "关闭");
    }

    public boolean getSwitch() {
        String text = this.tvSwitch.getText().toString();
        return "开启".equals(text);
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
}
