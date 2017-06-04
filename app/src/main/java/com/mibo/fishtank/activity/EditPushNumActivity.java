package com.mibo.fishtank.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.mibo.fishtank.FishTankmManage.DeviceParams;
import com.mibo.fishtank.FishTankmManage.DeviceParamsUtil;
import com.mibo.fishtank.FishTankmManage.FishTankApiManager;
import com.mibo.fishtank.FishTankmManage.event.SetTelEvent;
import com.mibo.fishtank.R;
import com.mibo.fishtank.weight.LoadingDialog;
import com.mibo.fishtank.weight.TitleBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class EditPushNumActivity extends BaseActivity {

    private LoadingDialog mLoadingDialog;

    private EditText etNum1;
    private EditText etNum2;
    private EditText etNum3;
    private EditText etNum4;

    private String uid;
    private DeviceParams deviceParams;

    private String[] telePhone = new String[4];

    public static Intent BuildIntent(Context context, String uid) {
        Intent intent = new Intent(context, EditPushNumActivity.class);
        intent.putExtra("uid", uid);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_push_num_activity);
        initView();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        String uid = getIntent().getStringExtra("uid");
        if (TextUtils.isEmpty(uid)) {
            Toast.makeText(this, "设备异常", Toast.LENGTH_SHORT).show();
        } else {
            this.uid = uid;
            FishTankApiManager.getInstance().getDeviceParam(uid);
        }

        setTelePhoneParams();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    private void initView() {
        mLoadingDialog = new LoadingDialog(this, "加载中...");

        TitleBar titleBar = (TitleBar) findViewById(R.id.edit_push_title);
        titleBar.setCenterStr(R.string.edit_push_num);
        titleBar.setRightStr("保存");
        titleBar.setOnClickRightListener(new OnClickSaveListener());
        titleBar.setOnClickLeftListener(new OnClickLeftListener());

        etNum1 = (EditText) findViewById(R.id.num1);
        etNum2 = (EditText) findViewById(R.id.num2);
        etNum3 = (EditText) findViewById(R.id.num3);
        etNum4 = (EditText) findViewById(R.id.num4);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    void onSetTellPhoneListener(SetTelEvent event) {
        if (event.result == 0) {
            Toast.makeText(this, "推送手机号码设置成功", Toast.LENGTH_SHORT).show();
            deviceParams.Tel = telePhone;
            DeviceParamsUtil.saveDeviceParams(this, event.uid, deviceParams);
        } else {
            Toast.makeText(this, "推送手机号码设置失败", Toast.LENGTH_SHORT).show();
        }
        mLoadingDialog.close();
    }

    public void setTelePhoneParams() {
        // 获取本地缓存数据
        deviceParams = DeviceParamsUtil.getDeviceParams(this, uid);
        String[] tels = deviceParams.Tel;

//        if (deviceParams.Tel != null) {
//            for (int i = 0; i < deviceParams.Tel.length; i++) {
//                tels.add(deviceParams.Tel[i]);
//            }
//        }
//
//        for (int i = 0; i < 4; i++) {
//
//        }

        setText(tels);

    }

    private void setText(String[] tels) {
        switch (tels.length) {
            case 1:
                this.etNum1.setText(tels[0] == null ? "" : tels[0]);
                break;
            case 2:
                this.etNum1.setText(tels[0] == null ? "" : tels[0]);
                this.etNum2.setText(tels[1] == null ? "" : tels[1]);
                break;
            case 3:
                this.etNum1.setText(tels[0] == null ? "" : tels[0]);
                this.etNum2.setText(tels[1] == null ? "" : tels[1]);
                this.etNum3.setText(tels[2] == null ? "" : tels[2]);
                break;
            case 4:
                this.etNum1.setText(tels[0] == null ? "" : tels[0]);
                this.etNum2.setText(tels[1] == null ? "" : tels[1]);
                this.etNum3.setText(tels[2] == null ? "" : tels[2]);
                this.etNum4.setText(tels[3] == null ? "" : tels[3]);
                break;
            default:

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
            mLoadingDialog.show();
            telePhone = getTelePhone();
            FishTankApiManager.getInstance().setTelParam(uid, telePhone);
        }
    }

    public String[] getTelePhone() {
        String[] tel = new String[4];
        tel[0] = checkPhoneNumber(etNum1.getText().toString());
        tel[1] = checkPhoneNumber(etNum2.getText().toString());
        tel[2] = checkPhoneNumber(etNum3.getText().toString());
        tel[3] = checkPhoneNumber(etNum4.getText().toString());
        return tel;
    }

    public String checkPhoneNumber(String phone) {
        if (TextUtils.isEmpty(phone)) {
            return "";
        }
        return phone;
    }
}
