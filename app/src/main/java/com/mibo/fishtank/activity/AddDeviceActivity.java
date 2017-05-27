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

public class AddDeviceActivity extends BaseActivity {

    private EditText deviceEdit;
    private Button nextStepBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_device_activity);
        initView();
    }

    private void initView() {
        TitleBar titleBar = (TitleBar) findViewById(R.id.add_device_title);
        titleBar.setCenterStr(R.string.add_device);
        titleBar.setOnClickLeftListener(new OnClickLeftListener());

        deviceEdit = (EditText) findViewById(R.id.device_pwd);
        deviceEdit.addTextChangedListener(new OnDevicePwdChangeListener());

        nextStepBtn = (Button) findViewById(R.id.next_step_btn);
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
            String devicePwd = deviceEdit.getText().toString();
            Intent intent = new Intent(context,AddingDeviceToNetActivity.class);
            intent.putExtra("devicePwd", devicePwd);
            startActivity(intent);
        }
    }
    private class OnDevicePwdChangeListener implements TextWatcher {
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
                nextStepBtn.setEnabled(true);
            } else {
                nextStepBtn.setEnabled(false);
            }
        }
    }

}
