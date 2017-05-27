package com.mibo.fishtank.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.mibo.fishtank.R;
import com.mibo.fishtank.weight.TitleBar;

public class SceneManagerActivity extends BaseActivity {

    private String sceneName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scene_manager_activity);
        Intent intent = getIntent();
        sceneName = intent.getStringExtra("sceneName");
        initView();
    }

    private void initView() {
        TitleBar titleBar = (TitleBar) findViewById(R.id.scene_manager_title);
        titleBar.setCenterStr(R.string.scene_manager);
        titleBar.setOnClickLeftListener(new OnClickLeftListener());

//        DataBaseManager.queryAllSceneByTel()

    }

    private class OnClickLeftListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            finish();
        }
    }
}
