package com.mibo.fishtank.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.landstek.iFishTank.IFishTankApi;
import com.landstek.iFishTank.IFishTankError;
import com.mibo.fishtank.FishTankmManage.FishTankApiManager;
import com.mibo.fishtank.FishTankmManage.FishTankUserApiManager;
import com.mibo.fishtank.FishTankmManage.event.AddOrUpSceneEvent;
import com.mibo.fishtank.FishTankmManage.event.ChangeMainEvent;
import com.mibo.fishtank.FishTankmManage.event.GetParameterEvent;
import com.mibo.fishtank.FishTankmManage.event.LoginEvent;
import com.mibo.fishtank.R;
import com.mibo.fishtank.weight.LoadingDialog;
import com.mibo.fishtank.weight.TitleBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 配置设备密码
 */
public class AddDeviceActivity extends BaseActivity {

    private EditText deviceEdit;
    private Button nextStepBtn;
    private String uid;
    private String model;
    private LoadingDialog loadingDialog;
    private String sceneId;
    private boolean isChangeSceneSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        setContentView(R.layout.add_device_activity);
        getIntentData();
        initView();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        uid = intent.getStringExtra("Uid");
        model = intent.getStringExtra("Model");
        sceneId = intent.getStringExtra("sceneId");
    }


    private void initView() {
        TitleBar titleBar = (TitleBar) findViewById(R.id.add_device_title);
        titleBar.setCenterStr(R.string.add_device);
        titleBar.setOnClickLeftListener(new OnClickLeftListener());

        loadingDialog = new LoadingDialog(context, "正在添加...");

        deviceEdit = (EditText) findViewById(R.id.device_pwd);
        deviceEdit.addTextChangedListener(new OnDevicePwdChangeListener());

        nextStepBtn = (Button) findViewById(R.id.next_step_btn);
        nextStepBtn.setOnClickListener(new OnClickNextStepListener());
    }

    /**
     * 登录设备回调
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginLstener(LoginEvent event) {
        if (event.loginSuccess) {
            FishTankApiManager.getInstance().getDeviceParam(uid);
        } else {
            Toast.makeText(this, "登录失败", Toast.LENGTH_SHORT).show();
            loadingDialog.close();
        }
    }

    /**
     * 获取设备参数回调
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetParameterLstener(GetParameterEvent event) {
        IFishTankApi.MsgGetParamRsp msgGetParamRsp = event.msgGetParamRsp;
        String uid = event.uid;
        int result = event.result;
        if (result == 0) {
            FishTankUserApiManager.getInstance().toAddDevice(uid, sceneId, model);
        } else {
            Toast.makeText(this, "获取失败", Toast.LENGTH_SHORT).show();
        }
        loadingDialog.close();
    }

    /**
     * 添加设备回调
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddSceneEvent(AddOrUpSceneEvent event) {
        loadingDialog.close();
        if (IFishTankError.SUCCESS == event.msg.arg2) {
            if (isChangeSceneSuccess) {
                EventBus.getDefault().postSticky(new ChangeMainEvent());
                Toast.makeText(context, "添加成功", Toast.LENGTH_SHORT).show();
                FishTankUserApiManager.getInstance().toGetDevCfg();
                Intent intent = new Intent(context, NeedPushToPhoneActivity.class);
                intent.putExtra("uid", uid);
                startActivity(intent);
                finish();
                return;
            } else {
                FishTankUserApiManager.getInstance().toAddDevice(uid, sceneId);
            }
            isChangeSceneSuccess = true;
        } else {
            Toast.makeText(context, "添加失败", Toast.LENGTH_SHORT).show();
        }
    }

    private class OnClickLeftListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            finish();
        }
    }

    private class OnClickNextStepListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            loadingDialog.show();
            String devicePwd = deviceEdit.getText().toString();
            if (TextUtils.isEmpty(devicePwd)) {
                Toast.makeText(context, "密码错误,请重新输入", Toast.LENGTH_SHORT).show();
            } else {
                toLoginDevice();
            }
        }
    }

    private void toLoginDevice() {
        FishTankApiManager.getInstance().loginDevice(uid, deviceEdit.getText().toString());
    }

    private class OnDevicePwdChangeListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String str = s.toString();
            if (str.length() > 0) {
                nextStepBtn.setEnabled(true);
            } else {
                nextStepBtn.setEnabled(false);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

}
