package com.mibo.fishtank.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mibo.fishtank.R;
import com.mibo.fishtank.weight.TitleBar;

public class SetDevicePwdActivity extends BaseActivity {

    private EditText oldPswEdit;
    private EditText newPswEdit;
    private EditText confirmNewPswEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_device_pwd_activity);
        initView();
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
//                mCloudApi.Modify(pwd, oldPwd);
            }
        }
    }
}
