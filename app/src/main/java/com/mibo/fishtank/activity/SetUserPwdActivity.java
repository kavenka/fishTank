package com.mibo.fishtank.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.landstek.iFishTank.IFishTankError;
import com.mibo.fishtank.FishTankmManage.FishTankUserApiManager;
import com.mibo.fishtank.FishTankmManage.event.ModifyEvent;
import com.mibo.fishtank.R;
import com.mibo.fishtank.utils.PreferencesManager;
import com.mibo.fishtank.weight.TitleBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class SetUserPwdActivity extends BaseActivity {

    private EditText oldPswEdit;
    private EditText newPswEdit;
    private EditText confirmNewPswEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        setContentView(R.layout.set_user_pwd_activity);
        initView();
    }

    private void initView() {
        TitleBar titleBar = (TitleBar) findViewById(R.id.set_pwd_activity_title);
        titleBar.setCenterStr("用户密码设置");
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

    /**
     * 修改密码回调
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onModifyEvent(ModifyEvent event) {
        if (IFishTankError.SUCCESS == event.msg.arg2) {
            Toast.makeText(context, "修改成功", Toast.LENGTH_SHORT).show();
            PreferencesManager pm = PreferencesManager.getInstance(context);
            String pwd = confirmNewPswEdit.getText().toString();
            pm.setStringValue("pwd", pwd);
            finish();
        } else {
            Toast.makeText(context, "修改失败", Toast.LENGTH_SHORT).show();
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
                FishTankUserApiManager.getInstance().toModify(pwd, oldPwd);
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
