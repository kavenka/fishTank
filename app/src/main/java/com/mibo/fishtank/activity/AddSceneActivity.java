package com.mibo.fishtank.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.landstek.iFishTank.CloudApi;
import com.landstek.iFishTank.IFishTankError;
import com.mibo.fishtank.FishTankmManage.FishTankUserApiManager;
import com.mibo.fishtank.FishTankmManage.event.AddOrUpSceneEvent;
import com.mibo.fishtank.R;
import com.mibo.fishtank.entity.Scene;
import com.mibo.fishtank.utils.DataBaseManager;
import com.mibo.fishtank.weight.LoadingDialog;
import com.mibo.fishtank.weight.TitleBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

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
     * 添加设备回调
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
            String name = sceneName.getText().toString();
            CloudApi.DevCfg devCfg = new CloudApi.DevCfg();
            devCfg.Type = 0;
            devCfg.Vendor = "Vendor";
            devCfg.Model = "Model";
            devCfg.Uid = "Uid";
            devCfg.User = "User";
            devCfg.Pwd = "Pwd";
            Scene scene = new Scene();
            scene.setName(name);
            long time = System.currentTimeMillis() / 1000;//获取系统时间的10位的时间戳
            String str = String.valueOf(time);
            scene.setSceneID(str);
            Gson gson = new Gson();
            ArrayList<Scene> scenes = new ArrayList<>();
            List<Scene> dataScenes = DataBaseManager.queryAllScene();
            scenes.addAll(dataScenes);
            scenes.add(scene);
            String scenesArrayStr = gson.toJson(scenes);
            devCfg.Data = Base64.encodeToString(scenesArrayStr.getBytes(), Base64.DEFAULT);

            FishTankUserApiManager.getInstance().toAddOrUpdateDev(devCfg);
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

