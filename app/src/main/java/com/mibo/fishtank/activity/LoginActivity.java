package com.mibo.fishtank.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.landstek.iFishTank.CloudApi;
import com.landstek.iFishTank.IFishTankError;
import com.mibo.fishtank.FishTankmManage.FishTankUserApiManager;
import com.mibo.fishtank.FishTankmManage.event.UserLoginEvent;
import com.mibo.fishtank.R;
import com.mibo.fishtank.utils.Constans;
import com.mibo.fishtank.utils.DataBaseManager;
import com.mibo.fishtank.utils.NetWorkUtils;
import com.mibo.fishtank.utils.PreferencesManager;
import com.mibo.fishtank.weight.LoadingDialog;
import com.mibo.fishtank.weight.TitleBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class LoginActivity extends BaseActivity {
    private boolean isExit = false;
    private CheckBox isReadDoc;
    private CloudApi mCloudApi;
    private EditText nameEditTxt;
    private EditText pwdEditTxt;
    private Button loginBtn;
    private String tel;
    private String pwd;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        initSDK();
        getIntentData();
        initView();
    }

    /**
     * 初始化SDK
     */
    private void initSDK() {
        mCloudApi = new CloudApi();
        mCloudApi.SetHandler(mHandler);
    }


    private void initView() {
        TitleBar titleBar = (TitleBar) findViewById(R.id.login_title);
        titleBar.setLeftImgRes(R.drawable.back);
        titleBar.setCenterStr(R.string.login_txt);
        titleBar.setOnClickListener(new OnClickLeftListener());

        loadingDialog = new LoadingDialog(context, "登录中...");

        nameEditTxt = (EditText) findViewById(R.id.login_name);
        pwdEditTxt = (EditText) findViewById(R.id.register_phone_num);
        if (tel != null) {
            nameEditTxt.setText(tel);
        }
        if (pwd != null) {
            pwdEditTxt.setText(pwd);
        }
        isReadDoc = (CheckBox) findViewById(R.id.login_check_box);
        isReadDoc.setChecked(true);
        TextView docTxt = (TextView) findViewById(R.id.login_doc);
        loginBtn = (Button) findViewById(R.id.login_btn);
        TextView registerTxt = (TextView) findViewById(R.id.login_register_txt);
        TextView forgetTxt = (TextView) findViewById(R.id.login_forget_txt);

        isReadDoc.setOnClickListener(new OnClickCheckBoxListener());
        docTxt.setOnClickListener(new OnClickDocListener());
        loginBtn.setOnClickListener(new OnClickLoginListener());
        registerTxt.setOnClickListener(new OnClickRegisterListener());
        forgetTxt.setOnClickListener(new OnClickForgetListener());
    }

    private void getIntentData() {
        Intent intent = getIntent();
        tel = intent.getStringExtra("tel");
        pwd = intent.getStringExtra("pwd");
    }


    private class OnClickLeftListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            finish();
        }
    }

    /**
     * 登陆的点击事件
     */
    private class OnClickLoginListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (NetWorkUtils.isNetworkConnected(context)) {
                String name = nameEditTxt.getText().toString();
                String pwd = pwdEditTxt.getText().toString();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserLoginEvent(UserLoginEvent event) {
        if (IFishTankError.SUCCESS == event.msg.arg2) {
            PreferencesManager pm = PreferencesManager.getInstance(context);
            pm.setBooleanValue("isLoginSuccess", true);
            String name = nameEditTxt.getText().toString();
            Constans.CURRENT_TEL = name;
            pm.setStringValue("name", name);
            pm.setStringValue("pwd", pwdEditTxt.getText().toString());
            DataBaseManager.saveUser(name, name);
            Toast.makeText(context, R.string.login_success, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
            loadingDialog.close();
            finish();
        } else {
            loadingDialog.close();
            Toast.makeText(context, R.string.login_failure, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * checkBox的点击事件
     */
    private class OnClickCheckBoxListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            boolean isChecked = isReadDoc.isChecked();
            loginBtn.setEnabled(isChecked);
        }
    }

    /**
     * 使用条款的点击事件
     */
    private class OnClickDocListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, DocActivity.class);
            context.startActivity(intent);
        }
    }

    /**
     * 注册的点击事件
     */
    private class OnClickRegisterListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, RegisterActivity.class);
            context.startActivity(intent);
        }
    }

    /**
     * 忘记密码的点击事件
     */
    private class OnClickForgetListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, ForgetActivity.class);
            context.startActivity(intent);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), R.string.press_again_finish, Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            finish();
            System.exit(0);
        }
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    isExit = false;
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
