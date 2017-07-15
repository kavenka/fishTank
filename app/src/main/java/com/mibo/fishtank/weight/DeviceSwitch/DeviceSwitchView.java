package com.mibo.fishtank.weight.DeviceSwitch;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.DrawableRes;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mibo.fishtank.FishTankmManage.SharedPreferencesUtil;
import com.mibo.fishtank.R;

/**
 * Created by Monty on 2017/5/29.
 */

public class DeviceSwitchView extends LinearLayout {
    private final Context context;
    private TextView mSwitchTitle;
    private TextView mSwitchParameter;
    private ImageView mSwitchIcon;

    private boolean isOpen;

    private boolean switchTitleEditEnable = false;

    private OnSwitchClickListener mOnSwitchClickListener;

    public DeviceSwitchView(Context context) {
        this(context, null);
    }

    public DeviceSwitchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DeviceSwitchView(final Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        LinearLayout rootView = (LinearLayout) LinearLayout.inflate(context, R.layout.device_switch_layout, null);
        mSwitchIcon = (ImageView) rootView.findViewById(R.id.switch_icon);
        mSwitchTitle = (TextView) rootView.findViewById(R.id.switch_title);
        mSwitchParameter = (TextView) rootView.findViewById(R.id.switch_parameter);

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnSwitchClickListener.onSwitch(v, !isOpen);
            }
        });


        addView(rootView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        closeSwitch();
    }

    public void setSwitchTitleEditEnable(boolean enable) {
        if (enable) {
            this.setOnLongClickListener(new OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("修改开关名称");
                    View view = LayoutInflater.from(context).inflate(R.layout.dialog_layout, null);
                    final EditText inputSwitchName = (EditText) view.findViewById(R.id.kaiguang_name);
                    inputSwitchName.setText(mSwitchTitle.getText());
                    builder.setView(view);//设置login_layout为对话提示框
                    //设置正面按钮，并做事件处理
                    builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String switchName = inputSwitchName.getText().toString();
                            if (TextUtils.isEmpty(switchName)) {
                                Toast.makeText(getContext(), "请输入开关名称", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            mSwitchTitle.setText(switchName);
                            SharedPreferencesUtil.putString(getContext(), DeviceSwitchView.this.getId() + "", inputSwitchName.getText().toString());
                        }
                    });
                    builder.show();//显示Dialog对话框
                    return false;
                }
            });
        }
    }

    public void setSwitchIcon(@DrawableRes int res) {
        mSwitchIcon.setImageResource(res);
        invalidate();
    }


    public void setSwitchTitle(String title) {
        mSwitchTitle.setText(title);
        invalidate();
    }

    public void openSwitch() {
        isOpen = true;
        mSwitchParameter.setText("已开启");
        mSwitchParameter.setAlpha(1f);
        invalidate();
    }

    public void closeSwitch() {
        isOpen = false;
        mSwitchParameter.setText("已关闭");
        mSwitchParameter.setAlpha(0.3f);
        invalidate();
    }

    public void setSwitch(boolean s) {
        if (s) {
            openSwitch();
        } else {
            closeSwitch();
        }
    }

    public void setSwitch() {
        if (isOpen) {
            closeSwitch();
        } else {
            openSwitch();
        }
    }


    public void setOnSwitchClickListener(OnSwitchClickListener onSwitchClickListener) {
        this.mOnSwitchClickListener = onSwitchClickListener;
    }


    public interface OnSwitchClickListener {

        void onSwitch(View v, boolean isOpen);
    }


}
