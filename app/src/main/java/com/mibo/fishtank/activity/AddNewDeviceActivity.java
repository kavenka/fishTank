package com.mibo.fishtank.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mibo.fishtank.R;
import com.mibo.fishtank.weight.TitleBar;

public class AddNewDeviceActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_new_device_activity);
        initView();
    }

    private void initView() {
        TitleBar titleBar = (TitleBar) findViewById(R.id.add_new_device_title);
        titleBar.setCenterStr(R.string.add_new_device);
        titleBar.setOnClickLeftListener(new OnClickLeftListener());

        LinearLayout paiChaLayout = (LinearLayout) findViewById(R.id.add_paicha_layout);
        LinearLayout tanTouLayout = (LinearLayout) findViewById(R.id.add_tantou_layout);
        LinearLayout wenDuLayout = (LinearLayout) findViewById(R.id.add_wenduji_layout);
        LinearLayout phLayout = (LinearLayout) findViewById(R.id.add_Ph_layout);
        paiChaLayout.setOnClickListener(new OnClickParChaListener());
        tanTouLayout.setOnClickListener(new OnClickTanTouListener());
        wenDuLayout.setOnClickListener(new OnClickWenDuListener());
        phLayout.setOnClickListener(new OnClickPHListener());
    }

    private class OnClickLeftListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            finish();
        }
    }

    private class OnClickParChaListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, DeviceConnectActivity.class);
            startActivity(intent);
        }
    }

    private class OnClickTanTouListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Toast.makeText(context, "敬请期待", Toast.LENGTH_SHORT).show();
        }
    }

    private class OnClickWenDuListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Toast.makeText(context, "敬请期待", Toast.LENGTH_SHORT).show();
        }
    }

    private class OnClickPHListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Toast.makeText(context, "敬请期待", Toast.LENGTH_SHORT).show();
        }
    }
}
