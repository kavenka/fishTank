package com.mibo.fishtank.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.landstek.iFishTank.CloudApi;
import com.landstek.iFishTank.IFishTankError;
import com.mibo.fishtank.R;
import com.mibo.fishtank.utils.PreferencesManager;
import com.mibo.fishtank.weight.TitleBar;

public class SetUserPwdActivity extends BaseActivity {

    private EditText oldPswEdit;
    private EditText newPswEdit;
    private EditText confirmNewPswEdit;
    private CloudApi mCloudApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_user_pwd_activity);
        initView();
        initSDK();
    }

    private void initView() {
        TitleBar titleBar = (TitleBar) findViewById(R.id.set_pwd_activity_title);
        titleBar.setCenterStr(R.string.user_pwd_set);
        titleBar.setOnClickLeftListener(new OnClickLeftListener());

        oldPswEdit = (EditText) findViewById(R.id.old_psw_edit);
        newPswEdit = (EditText) findViewById(R.id.new_psw_edit);
        confirmNewPswEdit = (EditText) findViewById(R.id.confirm_new_psw_edit);
        Button confirmBtn = (Button) findViewById(R.id.set_password_confirm_btn);
        confirmBtn.setOnClickListener(new OnClickConfirmListener());
    }

    private void initSDK() {
        mCloudApi = new CloudApi();
        mCloudApi.SetHandler(mHandler);
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
            if (TextUtils.isEmpty(oldPwd) || TextUtils.isEmpty(pwd) || !TextUtils.equals(pwd, confirmPwd)) {
                Toast.makeText(context, R.string.input_right_pwd, Toast.LENGTH_SHORT).show();
            } else {
                mCloudApi.Modify(pwd, oldPwd);
            }
        }
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CloudApi.MSG_WHAT_CLOUDAPI:
                    switch (msg.arg1) {
                        case CloudApi.MODIFY: {
                            if (IFishTankError.SUCCESS == msg.arg2) {
                                Toast.makeText(context, R.string.register_succese, Toast.LENGTH_SHORT).show();
                                PreferencesManager pm = PreferencesManager.getInstance(context);
                                String pwd = confirmNewPswEdit.getText().toString();
                                pm.setStringValue("pwd", pwd);
                                finish();
                            } else {
                                Toast.makeText(context, R.string.reset_psw_failed, Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;
                        case CloudApi.ERROR:
                            Toast.makeText(context, R.string.reset_psw_failed, Toast.LENGTH_SHORT).show();
                            break;
                    }
                    break;
            }
        }
    };
}
