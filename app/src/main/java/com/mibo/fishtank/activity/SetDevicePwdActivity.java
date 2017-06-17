package com.mibo.fishtank.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.landstek.iFishTank.IFishTankError;
import com.mibo.fishtank.FishTankmManage.FishTankApiManager;
import com.mibo.fishtank.FishTankmManage.FishTankUserApiManager;
import com.mibo.fishtank.FishTankmManage.event.AddOrUpSceneEvent;
import com.mibo.fishtank.FishTankmManage.event.LoginEvent;
import com.mibo.fishtank.FishTankmManage.event.SetDevicePwdEvent;
import com.mibo.fishtank.R;
import com.mibo.fishtank.entity.Device;
import com.mibo.fishtank.utils.Constans;
import com.mibo.fishtank.utils.DataBaseManager;
import com.mibo.fishtank.weight.LoadingDialog;
import com.mibo.fishtank.weight.TitleBar;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class SetDevicePwdActivity extends BaseActivity {

    private EditText oldPswEdit;
    private EditText newPswEdit;
    private EditText confirmNewPswEdit;

    private String newPwd;
    private LoadingDialog loadingDialog;

    public static Intent BuildIntent(Context context, String uid) {
        Intent intent = new Intent(context, SetDevicePwdActivity.class);
        intent.putExtra("uid", uid);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_device_pwd_activity);
        initView();

        if (TextUtils.isEmpty(Constans.DEVICE_UID)) {
            Toast.makeText(this, "设备异常", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        if (!EventBus.getDefault().isRegistered(this)) {
//            EventBus.getDefault().register(this);
//        }
//    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        if (EventBus.getDefault().isRegistered(this)) {
//            EventBus.getDefault().unregister(this);
//        }
//    }

    private void initView() {
        TitleBar titleBar = (TitleBar) findViewById(R.id.set_pwd_activity_title);
        titleBar.setCenterStr(R.string.user_pwd_set);
        titleBar.setOnClickLeftListener(new OnClickLeftListener());

        loadingDialog = new LoadingDialog(context, "设置密码中");

        oldPswEdit = (EditText) findViewById(R.id.old_psw_edit);
        newPswEdit = (EditText) findViewById(R.id.new_psw_edit);
        confirmNewPswEdit = (EditText) findViewById(R.id.confirm_new_psw_edit);
        Button confirmBtn = (Button) findViewById(R.id.set_password_confirm_btn);
        confirmBtn.setOnClickListener(new OnClickConfirmListener());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSetDevicePwdListener(SetDevicePwdEvent event) {
        if (event.result == 0) {
            FishTankApiManager.getInstance().loginDevice(Constans.DEVICE_UID, newPwd);
        } else {
            if(TextUtils.isEmpty(event.msg)){
                Toast.makeText(this, "密码设置失败", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, event.msg, Toast.LENGTH_SHORT).show();
            }
            loadingDialog.close();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginLstener(LoginEvent event) {
        if (event.loginSuccess) {
            // 用新密码登录到设备
            FishTankUserApiManager.getInstance().toUpdateDevice(Constans.DEVICE_UID, newPwd);
        } else {
            Toast.makeText(this, "新密码登录失败", Toast.LENGTH_SHORT).show();
            loadingDialog.close();
        }
    }

    /**
     * 更新新密码到设备
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddSceneEvent(AddOrUpSceneEvent event) {
        if (IFishTankError.SUCCESS == event.msg.arg2) {
            DataBaseManager.updateDevicePwd(Constans.DEVICE_UID,newPwd);
            Toast.makeText(this, "密码设置成功", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(context, "更新设备密码失败", Toast.LENGTH_SHORT).show();
        }
        loadingDialog.close();
    }


    private class OnClickLeftListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            finish();
        }
    }

    private class OnClickConfirmListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String oldPwd = oldPswEdit.getText().toString();
            String pwd = newPswEdit.getText().toString();
            String confirmPwd = confirmNewPswEdit.getText().toString();

            if (TextUtils.isEmpty(oldPwd) || TextUtils.isEmpty(pwd) || TextUtils.isEmpty(confirmPwd)) {
                Toast.makeText(SetDevicePwdActivity.this, R.string.input_right_pwd, Toast.LENGTH_SHORT).show();
                return;
            }
            if (!pwd.equals(confirmPwd)) {
                Toast.makeText(SetDevicePwdActivity.this, "两次密码输入不一致,请重新输入!", Toast.LENGTH_SHORT).show();
                return;
            }

            Device device = DataBaseManager.queryDevice(Constans.DEVICE_UID);
            if (!oldPwd.equals(device.getDevPwd())) {
                Toast.makeText(SetDevicePwdActivity.this, "原密码不正确,请重新输入!", Toast.LENGTH_SHORT).show();
                return;
            }
            loadingDialog.show();
            newPwd = confirmPwd;
            FishTankApiManager.getInstance().setDevicePwd(Constans.DEVICE_UID, confirmPwd);
        }
    }
}
