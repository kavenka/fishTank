package com.mibo.fishtank.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.mibo.fishtank.FishTankmManage.FishTankApiManager;
import com.mibo.fishtank.R;
import com.mibo.fishtank.utils.Constans;
import com.mibo.fishtank.weight.TitleBar;

public class NeedPushToPhoneActivity extends BaseActivity {

    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_need_push_to_phone);
        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        initView();
    }

    private void initView() {
        TitleBar titleBar = (TitleBar) findViewById(R.id.to_set_push_phone_title);
        titleBar.setCenterStr("设置推送号码");
        titleBar.setOnClickLeftListener(new OnClickLeftListener());

        Button setPhoneBtn = (Button) findViewById(R.id.to_set_push_phone_btn);
        Button notSetPhoneBtn = (Button) findViewById(R.id.not_push_phone_btn);
        setPhoneBtn.setOnClickListener(new OnClickSetPhoneListener());
        notSetPhoneBtn.setOnClickListener(new OnClickNotSetListener());
    }


    private class OnClickLeftListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private class OnClickSetPhoneListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String[] tel = new String[4];
            tel[0] = Constans.CURRENT_TEL;
            FishTankApiManager.getInstance().setTelParam(uid, tel);
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private class OnClickNotSetListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
