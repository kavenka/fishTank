package com.mibo.fishtank.activity;

import android.os.Bundle;
import android.view.View;

import com.mibo.fishtank.R;
import com.mibo.fishtank.weight.TitleBar;

public class DocActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doc_activity);
        initView();
    }

    private void initView() {
        TitleBar titleBar = (TitleBar) findViewById(R.id.doc_activity_title_bar);
        titleBar.setCenterStr(R.string.doc_title);
        titleBar.setOnClickLeftListener(new OnClickLeftListener());
    }

    private class OnClickLeftListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            finish();
        }
    }
}
