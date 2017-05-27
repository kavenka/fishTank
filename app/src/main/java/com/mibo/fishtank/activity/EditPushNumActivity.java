package com.mibo.fishtank.activity;

import android.os.Bundle;
import android.view.View;

import com.mibo.fishtank.R;
import com.mibo.fishtank.weight.TitleBar;

public class EditPushNumActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_push_num_activity);
        initView();
    }

    private void initView() {
        TitleBar titleBar = (TitleBar) findViewById(R.id.edit_push_title);
        titleBar.setCenterStr(R.string.edit_push_num);
        titleBar.setRightStr("保存");
        titleBar.setOnClickRightListener(new OnClickSaveListener());
        titleBar.setOnClickLeftListener(new OnClickLeftListener());
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
}
