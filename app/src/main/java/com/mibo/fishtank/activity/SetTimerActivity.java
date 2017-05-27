package com.mibo.fishtank.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.mibo.fishtank.R;
import com.mibo.fishtank.weight.SelectTimeDialog;
import com.mibo.fishtank.weight.TitleBar;

public class SetTimerActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_timer_activity);
        initView();
    }

    private void initView() {
        TitleBar titleBar = (TitleBar) findViewById(R.id.set_timer_title);
        titleBar.setCenterStr(R.string.timer_setting);
        titleBar.setRightStr("保存");
        titleBar.setOnClickRightListener(new OnClickSaveListener());
        titleBar.setOnClickLeftListener(new OnClickLeftListener());

        RelativeLayout timer1Layout = (RelativeLayout) findViewById(R.id.timer1_layout);
        RelativeLayout timer2Layout = (RelativeLayout) findViewById(R.id.timer2_layout);
        RelativeLayout timer3Layout = (RelativeLayout) findViewById(R.id.timer3_layout);
        RelativeLayout timer4Layout = (RelativeLayout) findViewById(R.id.timer4_layout);
        timer1Layout.setOnClickListener(new OnClickEditTimeListener());
        timer2Layout.setOnClickListener(new OnClickEditTimeListener());
        timer3Layout.setOnClickListener(new OnClickEditTimeListener());
        timer4Layout.setOnClickListener(new OnClickEditTimeListener());
    }

    private class OnClickLeftListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            finish();
        }
    }

    private class OnClickSaveListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            finish();
        }
    }

    private class OnClickEditTimeListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            SelectTimeDialog selectTimeDialog = new SelectTimeDialog(context);
            selectTimeDialog.show();
        }
    }

}
