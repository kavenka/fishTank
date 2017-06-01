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

public class RegisterActivity extends BaseActivity {
    private Context context = this;
    private EditText phoneEditTxt;
    private EditText codeEditTxt;
    private CloudApi mCloudApi;
    private String verifyCode;
    private Button sendCodeBtn;

    private TimeCount timeCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        initSDK();
        initView();

    }

    private void initSDK() {
        mCloudApi = new CloudApi();
        mCloudApi.SetHandler(mHandler);
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
     * 点击发送验证码按钮事件
     */
    private class OnClickSendCodeBtnListener implements View.OnClickListener {

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
