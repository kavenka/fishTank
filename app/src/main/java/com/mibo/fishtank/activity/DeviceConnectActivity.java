package com.mibo.fishtank.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.mibo.fishtank.R;
import com.mibo.fishtank.weight.TitleBar;

/**
 * 设备入网标识语
 */
public class DeviceConnectActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_connect_activity);
        initView();
    }

    private void initView() {
        TitleBar titleBar = (TitleBar) findViewById(R.id.device_connect_title);
        titleBar.setCenterStr(R.string.device_connect);
        titleBar.setOnClickLeftListener(new OnClickLeftListener());


        Button nextStepBtn = (Button) findViewById(R.id.next_step_btn);
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
            Intent intent = new Intent(context, DeviceInNetActivity.class);
            startActivity(intent);
        }
    }
}
