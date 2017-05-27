package com.mibo.fishtank.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mibo.fishtank.R;
import com.mibo.fishtank.weight.TitleBar;

public class DeviceInNetActivity extends BaseActivity {
    private EditText ssdEdit;
    private EditText wifiEdit;
    private boolean isSSDInput = false;
    private boolean isWifiInput = false;
    private Button nextSetpBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_in_net_activity);
        initView();
    }

    private void initView() {
        TitleBar titleBar = (TitleBar) findViewById(R.id.device_in_net_title);
        titleBar.setCenterStr(R.string.device_in_net);
        titleBar.setOnClickLeftListener(new OnClickLeftListener());

        ssdEdit = (EditText) findViewById(R.id.ssd_edit);
        wifiEdit = (EditText) findViewById(R.id.wifi_pwd);
        ssdEdit.addTextChangedListener(new OnSSDTextChangeListener());
        wifiEdit.addTextChangedListener(new OnWifiTextChangeListener());

        nextSetpBtn = (Button) findViewById(R.id.device_next_step_btn);
        nextSetpBtn.setOnClickListener(new OnClickNextStepListener());
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
            Intent intent = new Intent(context, AddDeviceActivity.class);
            intent.putExtra("ssd", ssdEdit.getText().toString());
            intent.putExtra("wifi", wifiEdit.getText().toString());
            startActivity(intent);
        }
    }

    private class OnSSDTextChangeListener implements TextWatcher {
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
                isSSDInput = true;
                if (isWifiInput) {
                    nextSetpBtn.setEnabled(true);
                }
            } else {
                nextSetpBtn.setEnabled(false);
                isSSDInput = false;
            }
        }
    }

    private class OnWifiTextChangeListener implements TextWatcher {
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
                isWifiInput = true;
                if (isSSDInput) {
                    nextSetpBtn.setEnabled(true);
                }
            } else {
                nextSetpBtn.setEnabled(false);
                isWifiInput = false;
            }
        }
    }
}
