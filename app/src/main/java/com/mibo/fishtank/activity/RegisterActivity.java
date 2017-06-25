package com.mibo.fishtank.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.landstek.iFishTank.IFishTankError;
import com.mibo.fishtank.FishTankmManage.FishTankUserApiManager;
import com.mibo.fishtank.FishTankmManage.event.CheckUserEvent;
import com.mibo.fishtank.FishTankmManage.event.SendVerifyCodeEvent;
import com.mibo.fishtank.R;
import com.mibo.fishtank.utils.NetWorkUtils;
import com.mibo.fishtank.weight.TitleBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends BaseActivity {
    private Context context = this;
    private EditText phoneEditTxt;
    private EditText codeEditTxt;
    private String verifyCode;
    private Button sendCodeBtn;

    private TimeCount timeCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        setContentView(R.layout.register_activity);
        initView();

    }

    private void initView() {
        TitleBar titleBar = (TitleBar) findViewById(R.id.register_activity_title);
        titleBar.setCenterStr(R.string.register_txt);
        titleBar.setOnClickLeftListener(new OnClickLeftListener());

        phoneEditTxt = (EditText) findViewById(R.id.register_phone_num);
        codeEditTxt = (EditText) findViewById(R.id.register_verification_code);
        timeCount = new TimeCount(60000, 1000);
        sendCodeBtn = (Button) findViewById(R.id.register_get_code_btn);
        Button nextStepBtn = (Button) findViewById(R.id.register_next_step_btn);

        sendCodeBtn.setOnClickListener(new OnClickSendCodeBtnListener());
        nextStepBtn.setOnClickListener(new OnClickNextStepBtnListener());
    }


    /**
     * 检测用户是否已经注册回调
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCheckUserEvent(CheckUserEvent event) {
        if (IFishTankError.SUCCESS == event.msg.arg2) {
            if (NetWorkUtils.isNetworkConnected(context)) {
                String tel = phoneEditTxt.getText().toString();
                if (!TextUtils.isEmpty(tel)) {
                    timeCount.start();
                    FishTankUserApiManager.getInstance().toSendSmsForVerifyCode(tel);
                } else {
                    Toast.makeText(context, R.string.input_right_phone_num, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "当前网络不可用", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "用户名已注册", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 验证码回调
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVerifyCoideEvent(SendVerifyCodeEvent event) {
        if (IFishTankError.SUCCESS == event.msg.arg2) {
            String data = event.msg.obj.toString();
            Log.i("TAG", "Rsp=" + data);
            try {
                JSONObject jsonObject = new JSONObject(data);
                verifyCode = jsonObject.getString("VerifyCode");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, "获取验证码失败", Toast.LENGTH_SHORT).show();
        }
    }
    /**
     * 点击发送验证码按钮事件
     */
    private class OnClickSendCodeBtnListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (NetWorkUtils.isNetworkConnected(context)) {
                String tel = phoneEditTxt.getText().toString();
                if (!TextUtils.isEmpty(tel)) {
                    FishTankUserApiManager.getInstance().toCheckUser(tel);
                } else {
                    Toast.makeText(context, R.string.input_right_phone_num, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "当前网络不可用", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class OnClickNextStepBtnListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            String telNum = phoneEditTxt.getText().toString();
            String codeNum = codeEditTxt.getText().toString();
            if (TextUtils.isEmpty(telNum) || TextUtils.isEmpty(codeNum) || TextUtils.isEmpty(verifyCode) || (!verifyCode.equals(codeNum))) {
                Toast.makeText(context, R.string.input_right_code, Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(context, ConfirmPasswordActivity.class);
                intent.putExtra("userName", telNum);
                intent.putExtra("verifyCode", verifyCode);
                intent.putExtra("tel", telNum);
                intent.putExtra("isRegister", true);
                startActivity(intent);
                finish();
            }
        }
    }

    private class OnClickLeftListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            finish();
        }
    }

    private class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {// 计时完毕
            sendCodeBtn.setText(R.string.register_code);
            sendCodeBtn.setEnabled(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程
            sendCodeBtn.setEnabled(false);//防止重复点击
            sendCodeBtn.setText(millisUntilFinished / 1000 + " 秒");
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
