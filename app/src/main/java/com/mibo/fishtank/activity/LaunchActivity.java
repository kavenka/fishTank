package com.mibo.fishtank.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.mibo.fishtank.R;
import com.mibo.fishtank.utils.PreferencesManager;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;

public class LaunchActivity extends BaseActivity {


    private String name;
    private String pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 开启logcat输出，方便debug，发布时请关闭
        XGPushConfig.enableDebug(this, true);
        // 如果需要知道注册是否成功，请使用registerPush(getApplicationContext(), XGIOperateCallback)带callback版本
// 如果需要绑定账号，请使用registerPush(getApplicationContext(),account)版本
// 具体可参考详细的开发指南
// 传递的参数为ApplicationContext
        Context context = getApplicationContext();
        XGPushManager.registerPush(context);
        setContentView(R.layout.launch_activity);
        initView();
        getLoginInfo();
    }

    private void initView() {
        Button loginBtn = (Button) findViewById(R.id.launch_login_btn);
        Button registerBtn = (Button) findViewById(R.id.launch_register_btn);
        loginBtn.setOnClickListener(new OnClickLoginBtnListener());
        registerBtn.setOnClickListener(new OnClickRegisterBtnListener());
    }

    /**
     * 获取登陆信息
     */
    private void getLoginInfo() {
        PreferencesManager pm = PreferencesManager.getInstance(context);
        boolean isLoginSuccess = pm.getBooleanValue("isLoginSuccess");
        if (isLoginSuccess) {
            name = pm.getStringValue("name");
            pwd = pm.getStringValue("pwd");
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
}
