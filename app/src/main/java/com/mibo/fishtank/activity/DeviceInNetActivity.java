package com.mibo.fishtank.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.landstek.iFishTank.LSmartLink;
import com.mibo.fishtank.R;
import com.mibo.fishtank.utils.PreferencesManager;
import com.mibo.fishtank.weight.LoadingDialog;
import com.mibo.fishtank.weight.TitleBar;

/**
 * 获取wifi的帐号和密码
 */
public class DeviceInNetActivity extends BaseActivity implements LSmartLink.LSmartLinkInterface {
    private EditText wifiEdit;
    private Button nextSetpBtn;

    private LSmartLink mLSmartLink;
    private LoadingDialog loadingDialog;
    private TextView wifiName;
    private String sceneId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_in_net_activity);
        sceneId = getIntent().getStringExtra("sceneId");
        initSDK();
        initView();
    }

    private void initSDK() {
        mLSmartLink = new LSmartLink(this);
        mLSmartLink.SetInterface(this);
    }


    private void initView() {
        TitleBar titleBar = (TitleBar) findViewById(R.id.device_in_net_title);
        titleBar.setCenterStr(R.string.device_in_net);
        titleBar.setOnClickLeftListener(new OnClickLeftListener());
        titleBar.setRightStr("设备列表");
        titleBar.setOnClickRightListener(new OnClickRightListener());

        loadingDialog = new LoadingDialog(context, "设备入网中...");

        wifiName = (TextView) findViewById(R.id.ssd_edit);
        wifiName.setText(mLSmartLink.GetCurrentSsid());

        wifiEdit = (EditText) findViewById(R.id.wifi_pwd);
        wifiEdit.addTextChangedListener(new OnWifiPwdChangeListener());

        String wifiPwd = PreferencesManager.getInstance(context).getStringValue("wifiName.getText().toString()");
        if (!TextUtils.isEmpty(wifiPwd)) {
            wifiEdit.setText(wifiPwd);
        }

        nextSetpBtn = (Button) findViewById(R.id.device_next_step_btn);
        nextSetpBtn.setOnClickListener(new OnClickNextStepListener());
    }

    @Override
    public void LSmartLinkResult(boolean isConnectDevice) {
        loadingDialog.close();
        if (isConnectDevice) {
            PreferencesManager.getInstance(context).setStringValue(wifiName.getText().toString(), wifiEdit.getText().toString());
            Intent intent = new Intent(context, SearchNewDeviceActivity.class);
            intent.putExtra("sceneId", sceneId);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(context, "wifi密码错误，请重新输入", Toast.LENGTH_SHORT).show();
        }
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
            if (!TextUtils.isEmpty(wifiEdit.getText().toString())) {
                loadingDialog.show();
                mLSmartLink.StartLSmartLink(wifiEdit.getText().toString());
            } else {
                Toast.makeText(context, "wifi密码错误，请重新输入", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class OnClickRightListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, SearchNewDeviceActivity.class);
            intent.putExtra("sceneId", sceneId);
            startActivity(intent);
            finish();
        }
    }

    private class OnWifiPwdChangeListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String str = s.toString();
            if (str.length() > 0) {
                nextSetpBtn.setEnabled(true);
            } else {
                nextSetpBtn.setEnabled(false);
            }
        }
    }
}
