package com.mibo.fishtank.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.landstek.iFishTank.IFishTankError;
import com.mibo.fishtank.FishTankmManage.FishTankUserApiManager;
import com.mibo.fishtank.FishTankmManage.event.RegisterEvent;
import com.mibo.fishtank.FishTankmManage.event.UpdateUserPwdEvent;
import com.mibo.fishtank.R;
import com.mibo.fishtank.utils.NetWorkUtils;
import com.mibo.fishtank.weight.TitleBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ConfirmPasswordActivity extends BaseActivity {
    private Context context = this;
    private EditText passwordEditTxt;
    private EditText confirmPasswordEditTxt;
    private String userName;
    private String tel;
    private String verifyCode;
    private boolean isRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        setContentView(R.layout.confirm_password_activity);
        getIntentData();
        initView();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        userName = intent.getStringExtra("userName");
        tel = intent.getStringExtra("tel");
        verifyCode = intent.getStringExtra("verifyCode");
        Log.i("xiao", "getIntentData: " + verifyCode);
        isRegister = intent.getBooleanExtra("isRegister", true);
    }

    private void initView() {
        TitleBar titleBar = (TitleBar) findViewById(R.id.confirm_activity_title);
        titleBar.setCenterStr(R.string.input_password);
        titleBar.setOnClickLeftListener(new OnClickLeftListener());

        passwordEditTxt = (EditText) findViewById(R.id.confirm_edit);
        confirmPasswordEditTxt = (EditText) findViewById(R.id.confirm_again_edit);
        Button submitBtn = (Button) findViewById(R.id.password_confirm_btn);
        submitBtn.setOnClickListener(new OnClickSubmitListener());
    }

    private class OnClickLeftListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            finish();
        }
    }

    /**
     * 注册用户回调
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRegisterEvent(RegisterEvent event) {
        if (IFishTankError.SUCCESS == event.msg.arg2) {
            Toast.makeText(context, R.string.register_succese, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, LoginActivity.class);
            intent.putExtra("tel", tel);
            intent.putExtra("pwd", passwordEditTxt.getText().toString());
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(context, R.string.register_failed, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 修改密码回调
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUpdataPwdEvent(UpdateUserPwdEvent event) {
        if (IFishTankError.SUCCESS == event.msg.arg2) {
            Toast.makeText(context, R.string.reset_psw_success, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, LoginActivity.class);
            intent.putExtra("tel", tel);
            intent.putExtra("pwd", passwordEditTxt.getText().toString());
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(context, R.string.reset_psw_failed, Toast.LENGTH_SHORT).show();
        }
    }

    private class OnClickSubmitListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (NetWorkUtils.isNetworkConnected(context)) {
                String passWord = passwordEditTxt.getText().toString();
                String confirmPassWord = confirmPasswordEditTxt.getText().toString();
                if (TextUtils.equals(passWord, confirmPassWord)) {
                    if (isRegister) {
                        FishTankUserApiManager.getInstance().toRegister(userName, passWord, tel, verifyCode);
                    } else {
                        FishTankUserApiManager.getInstance().toResetPwd(userName, passWord, tel, verifyCode);
                    }
                } else {
                    Toast.makeText(context, R.string.password_not_same, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "当前网络不可用", Toast.LENGTH_SHORT).show();
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
