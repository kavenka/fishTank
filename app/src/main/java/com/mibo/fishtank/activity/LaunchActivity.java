package com.mibo.fishtank.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.landstek.iFishTank.IFishTankError;
import com.mibo.fishtank.FishTankmManage.FishTankUserApiManager;
import com.mibo.fishtank.FishTankmManage.event.UserLoginEvent;
import com.mibo.fishtank.R;
import com.mibo.fishtank.entity.User;
import com.mibo.fishtank.utils.Constans;
import com.mibo.fishtank.utils.NetWorkUtils;
import com.mibo.fishtank.utils.PreferencesManager;
import com.mibo.fishtank.weight.LoadingDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class LaunchActivity extends BaseActivity {


    private String name;
    private String pwd;
    private LoadingDialog loadingDialog;
    private boolean isLoginSuccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        XGPushConfig.enableDebug(this, true);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        setContentView(R.layout.launch_activity);
        initView();
        getLoginInfo();
    }

    private void initView() {
        loadingDialog = new LoadingDialog(context, "登录中...");
        Button loginBtn = (Button) findViewById(R.id.launch_login_btn);
        Button registerBtn = (Button) findViewById(R.id.launch_register_btn);
        loginBtn.setOnClickListener(new OnClickLoginBtnListener());
        registerBtn.setOnClickListener(new OnClickRegisterBtnListener());
    }

    /**
     * 获取登录信息
     */
    private void getLoginInfo() {
        PreferencesManager pm = PreferencesManager.getInstance(context);
        isLoginSuccess = pm.getBooleanValue("isLoginSuccess");
        if (isLoginSuccess) {
            name = pm.getStringValue("name");
            pwd = pm.getStringValue("pwd");
            if (NetWorkUtils.isNetworkConnected(context)) {
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(pwd)) {
                    Toast.makeText(context, R.string.please_enter_the_correct_username_and_password, Toast.LENGTH_SHORT).show();
                } else {
                    loadingDialog.show();
                    FishTankUserApiManager.getInstance().toSignIn(name, pwd);
                }
            } else {
                Toast.makeText(context, "当前网络不可用", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 用户登录回调
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserLoginEvent(UserLoginEvent event) {
        if (IFishTankError.SUCCESS == event.msg.arg2) {
            PreferencesManager pm = PreferencesManager.getInstance(context);
            pm.setBooleanValue("isLoginSuccess", true);
            Constans.CURRENT_TEL = name;
            pm.setStringValue("name", name);
            pm.setStringValue("pwd", pwd);
            pm.setStringValue("tel", name);
            User user = new User();
            user.setTel(name);
            user.saveOrUpdate();
            Toast.makeText(context, R.string.login_success, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
            loadingDialog.close();
            if (!TextUtils.isEmpty(Constans.XG_TOKEN)) {
                FishTankUserApiManager.getInstance().toRegPushInfo(Constans.XG_TOKEN);
            }
            finish();
        } else {
            loadingDialog.close();
            Toast.makeText(context, R.string.login_failure, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 登录的点击事件
     */
    private class OnClickLoginBtnListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, LoginActivity.class);
            intent.putExtra("tel", name);
            intent.putExtra("pwd", pwd);
            context.startActivity(intent);
            finish();
        }
    }

    /**
     * 注册的点击事件
     */
    private class OnClickRegisterBtnListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, RegisterActivity.class);
            context.startActivity(intent);
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
