package com.mibo.fishtank.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mibo.fishtank.R;
import com.mibo.fishtank.utils.Constans;
import com.mibo.fishtank.utils.DataBaseManager;
import com.mibo.fishtank.weight.TitleBar;

public class AddSceneActivity extends BaseActivity {

    private EditText sceneName;
    private LinearLayout addSceneLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_scene_activity);
        initView();
    }

    private void initView() {
        TitleBar titleBar = (TitleBar) findViewById(R.id.add_scene_title);
        titleBar.setCenterStr(R.string.add_scene);
        titleBar.setOnClickLeftListener(new OnClickLeftListener());


        sceneName = (EditText) findViewById(R.id.scene_name);
        addSceneLayout = (LinearLayout) findViewById(R.id.add_scene_layout);

        sceneName.addTextChangedListener(new OnTextChangeListener());
        addSceneLayout.setOnClickListener(new OnClickAddSceneListener());

    }

    private class OnClickLeftListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            finish();
        }
    }

    private class OnTextChangeListener implements TextWatcher {

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
                addSceneLayout.setEnabled(true);
            } else {
                addSceneLayout.setEnabled(false);
            }
        }
    }

    private class OnClickAddSceneListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String name = sceneName.getText().toString();
            boolean isSuccess = DataBaseManager.saveScene(name, Constans.CURRENT_TEL);
            if (isSuccess) {
                Toast.makeText(context, "新增成功", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra("sceneName", name);
                setResult(123,intent);
                finish();
            } else {
                Toast.makeText(context, "新增失败", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

