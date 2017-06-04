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

import com.landstek.iFishTank.CloudApi;
import com.landstek.iFishTank.IFishTankApi;
import com.landstek.iFishTank.IFishTankError;
import com.mibo.fishtank.FishTankmManage.FishTankApiManager;
import com.mibo.fishtank.FishTankmManage.FishTankUserApiManager;
import com.mibo.fishtank.FishTankmManage.event.AddOrUpSceneEvent;
import com.mibo.fishtank.FishTankmManage.event.GetParameterEvent;
import com.mibo.fishtank.FishTankmManage.event.LoginEvent;
import com.mibo.fishtank.R;
import com.mibo.fishtank.utils.DataBaseManager;
import com.mibo.fishtank.weight.LoadingDialog;
import com.mibo.fishtank.weight.TitleBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

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
            CloudApi.DevCfg devCfg = new CloudApi.DevCfg();
            List<String> sceneIds = DataBaseManager.queryAllDeviceScene(uid);
            sceneIds.add(sceneId);
            int size = sceneIds.size();
            StringBuilder stringBuffer = new StringBuilder();
            for (int i = 0; i < size; i++) {
                stringBuffer.append(sceneIds.get(i));
                if (size > 1 && i != size - 1) {
                    stringBuffer.append("&");
                }
            }

            devCfg.Type = 1;
            devCfg.Vendor = "1";
            devCfg.Model = model;
            devCfg.Uid = uid;
            devCfg.User = "admin";
            devCfg.Pwd = "12345678";
            devCfg.Data = "";
            devCfg.Custom = stringBuffer.toString().getBytes();

            FishTankUserApiManager.getInstance().toAddOrUpdateDev(devCfg);
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
            Toast.makeText(context, "添加成功", Toast.LENGTH_SHORT).show();
            FishTankUserApiManager.getInstance().toGetDevCfg();
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
            finish();
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
        IFishTankApi.MsgLoginCmd cmd = new IFishTankApi.MsgLoginCmd();
        cmd.Pwd = "12345678";
        cmd.User = "admin";
        FishTankApiManager.getInstance().loginDevice(uid);
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
