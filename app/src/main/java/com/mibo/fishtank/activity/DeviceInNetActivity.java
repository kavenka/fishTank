package com.mibo.fishtank.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.landstek.iFishTank.LSmartLink;
import com.mibo.fishtank.R;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_in_net_activity);
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

        loadingDialog = new LoadingDialog(context, "设备入网中...");

        TextView wifiName = (TextView) findViewById(R.id.ssd_edit);
        wifiName.setText(mLSmartLink.GetCurrentSsid());

        wifiEdit = (EditText) findViewById(R.id.wifi_pwd);
        wifiEdit.addTextChangedListener(new OnWifiPwdChangeListener());

        nextSetpBtn = (Button) findViewById(R.id.device_next_step_btn);
        nextSetpBtn.setOnClickListener(new OnClickNextStepListener());
    }

    @Override
    public void LSmartLinkResult(boolean isConnectDevice) {
        loadingDialog.close();
        if (isConnectDevice) {
            Intent intent = new Intent(context, SearchNewDeviceActivity.class);
            startActivity(intent);
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
            loadingDialog.show();
            mLSmartLink.StartLSmartLink(wifiEdit.getText().toString());
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
