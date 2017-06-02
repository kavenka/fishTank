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
import com.mibo.fishtank.FishTankmManage.event.SetParamsEvent;
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
    void onSetTellPhoneListener(SetParamsEvent event) {
        if (event.result == 0) {
            finish();
        } else {
            Toast.makeText(this, "推送手机号码设置失败", Toast.LENGTH_SHORT).show();
        }
        mLoadingDialog.close();
    }

    public void setTelePhoneParams() {
        // 获取本地缓存数据
        deviceParams = DeviceParamsUtil.getDeviceParams(this, uid);
        String[] tel = deviceParams.Tel;
        if (tel == null || tel.length == 0) {
            return;
        }

        this.etNum1.setText(tel[0] == null ? "" : tel[0]);
        this.etNum2.setText(tel[1] == null ? "" : tel[1]);
        this.etNum3.setText(tel[2] == null ? "" : tel[2]);
        this.etNum4.setText(tel[3] == null ? "" : tel[3]);

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

            FishTankApiManager.getInstance().setTelParam(uid,
                    checkPhoneNumber(etNum1.getText().toString())
                    , checkPhoneNumber(etNum2.getText().toString())
                    , checkPhoneNumber(etNum3.getText().toString())
                    , checkPhoneNumber(etNum4.getText().toString()));
        }
    }

    public String checkPhoneNumber(String phone) {
        if (TextUtils.isEmpty(phone)) {
            return "";
        }
        return phone;
    }
}
