package com.mibo.fishtank.weight.DeviceSwitch;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mibo.fishtank.R;

/**
 * Created by Monty on 2017/5/29.
 */

public class DeviceSwitchView extends LinearLayout {
    private TextView mSwitchTitle;
    private TextView mSwitchParameter;
    private ImageView mSwitchIcon;

    private boolean isOpen;

    private OnSwitchClickListener mOnSwitchClickListener;

    public DeviceSwitchView(Context context) {
        this(context, null);
    }

    public DeviceSwitchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DeviceSwitchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LinearLayout rootView = (LinearLayout) LinearLayout.inflate(context, R.layout.device_switch_layout, null);
        mSwitchIcon = (ImageView) rootView.findViewById(R.id.switch_icon);
        mSwitchTitle = (TextView) rootView.findViewById(R.id.switch_title);
        mSwitchParameter = (TextView) rootView.findViewById(R.id.switch_parameter);

        rootView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnSwitchClickListener.onSwitch(isOpen);
            }
        });

    }

    public void openSwitch() {
        isOpen = true;
        mSwitchParameter.setText("已开启");
        mSwitchParameter.setAlpha(0.2f);
    }

    public void closeSwitch() {
        isOpen = false;
        mSwitchParameter.setText("已关闭");
        mSwitchParameter.setAlpha(1f);
    }

    public void setSwitch(boolean s) {
        if (s) {
            openSwitch();
        } else {
            closeSwitch();
        }
    }


    public void setOnSwitchClickListener(OnSwitchClickListener onSwitchClickListener) {
        this.mOnSwitchClickListener = onSwitchClickListener;
    }


    interface OnSwitchClickListener {

        void onSwitch(boolean isOpen);
    }


}
