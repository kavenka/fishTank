package com.mibo.fishtank.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.landstek.iFishTank.IFishTankError;
import com.mibo.fishtank.FishTankmManage.FishTankUserApiManager;
import com.mibo.fishtank.FishTankmManage.event.AddOrUpSceneEvent;
import com.mibo.fishtank.R;
import com.mibo.fishtank.weight.LoadingDialog;
import com.mibo.fishtank.weight.TitleBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class AddSceneActivity extends BaseActivity {

    private EditText sceneName;
    private Button addSceneBtn;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_scene_activity);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        initView();
    }

    private void initView() {
        TitleBar titleBar = (TitleBar) findViewById(R.id.add_scene_title);
        titleBar.setCenterStr(R.string.add_scene);
        titleBar.setOnClickLeftListener(new OnClickLeftListener());
        loadingDialog = new LoadingDialog(context, "正在添加...");


        sceneName = (EditText) findViewById(R.id.scene_name);
        addSceneBtn = (Button) findViewById(R.id.add_scene_layout);

        sceneName.addTextChangedListener(new OnTextChangeListener());
        addSceneBtn.setOnClickListener(new OnClickAddSceneListener());

    }

    /**
     * 添加场景回调
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddSceneEvent(AddOrUpSceneEvent event) {
        loadingDialog.close();
        if (IFishTankError.SUCCESS == event.msg.arg2) {
            Toast.makeText(context, "新增成功", Toast.LENGTH_SHORT).show();
            FishTankUserApiManager.getInstance().toGetDevCfg();
            finish();
        } else {
            Toast.makeText(context, "新增失败", Toast.LENGTH_SHORT).show();
        }
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
                addSceneBtn.setEnabled(true);
            } else {
                addSceneBtn.setEnabled(false);
            }
        }
    }

    private class OnClickAddSceneListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            loadingDialog.show();
            FishTankUserApiManager.getInstance().toAddScene(sceneName.getText().toString());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}

