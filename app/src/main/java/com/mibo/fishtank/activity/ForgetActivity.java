package com.mibo.fishtank.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.landstek.iFishTank.CloudApi;
import com.landstek.iFishTank.IFishTankError;
import com.mibo.fishtank.R;
import com.mibo.fishtank.utils.NetWorkUtils;
import com.mibo.fishtank.weight.TitleBar;

import org.json.JSONException;
import org.json.JSONObject;

public class ForgetActivity extends BaseActivity {
    private Context context = this;
    private Button verifyCodeBtn;
    private TimeCount timeCount;
    private EditText phoneEditTxt;
    private EditText verifyCodeEdit;
    private CloudApi mCloudApi;
    private String verifyCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_activity);
        initView();
        initSDK();
    }

    private void initSDK() {
        mCloudApi = new CloudApi();
        mCloudApi.SetHandler(mHandler);
    }

    private void initView() {
        TitleBar titleBar = (TitleBar) findViewById(R.id.forget_activity_title);
        titleBar.setCenterStr(R.string.forget_txt);
        titleBar.setOnClickLeftListener(new OnClickLeftListener());

        phoneEditTxt = (EditText) findViewById(R.id.forget_phone_num);
        verifyCodeEdit = (EditText) findViewById(R.id.forget_verification_code);
        Button nextStepBtn = (Button) findViewById(R.id.forget_next_step_btn);
        verifyCodeBtn = (Button) findViewById(R.id.forget_get_code_btn);
        timeCount = new TimeCount(60000, 1000);
        verifyCodeBtn.setOnClickListener(new OnClickVerifyCodeListener());
        nextStepBtn.setOnClickListener(new OnClickNextStepListener());
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
            if (NetWorkUtils.isNetworkConnected(context)) {
                String telNum = phoneEditTxt.getText().toString();
                String codeNum = verifyCodeEdit.getText().toString();
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
            } else {
                Toast.makeText(context, "当前网络不可用", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class OnClickVerifyCodeListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (NetWorkUtils.isNetworkConnected(context)) {
                String tel = phoneEditTxt.getText().toString();
                if (!TextUtils.isEmpty(tel)) {
                    timeCount.start();
                    mCloudApi.SmsGetVerifyCode(tel);
                } else {
                    Toast.makeText(context, R.string.input_right_phone_num, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, "当前网络不可用", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {// 计时完毕
            verifyCodeBtn.setText("发送验证码");
            verifyCodeBtn.setEnabled(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程
            verifyCodeBtn.setEnabled(false);//防止重复点击
            verifyCodeBtn.setText(millisUntilFinished / 1000 + " 秒");
        }
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CloudApi.MSG_WHAT_CLOUDAPI:
                    switch (msg.arg1) {
                        case CloudApi.SMSGETVERIFYCODE: {
                            if (IFishTankError.SUCCESS == msg.arg2) {
                                String data = msg.obj.toString();
                                Log.i("TAG", "Rsp=" + data);
                                try {
                                    JSONObject jsonObject = new JSONObject(data);
                                    verifyCode = jsonObject.getString("VerifyCode");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        break;
                        case CloudApi.ERROR:
                            Toast.makeText(context, R.string.send_code_failed, Toast.LENGTH_SHORT).show();
                            break;
                    }
                    break;
            }
        }
    };
}
