package com.mibo.fishtank.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
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

import java.util.List;

public class SceneManagerActivity extends BaseActivity {

    private String sceneName;
    private LoadingDialog loadingDialog;
    private String sceneID;
    private boolean isLastOne;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        setContentView(R.layout.scene_manager_activity);
        getIntentData();
        initView();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        sceneName = intent.getStringExtra("sceneName");
        sceneID = intent.getStringExtra("sceneID");
        isLastOne = intent.getBooleanExtra("isLastOne", false);
    }

    private void initView() {
        TitleBar titleBar = (TitleBar) findViewById(R.id.scene_manager_title);
        titleBar.setCenterStr(R.string.scene_manager);
        titleBar.setOnClickLeftListener(new OnClickLeftListener());
        loadingDialog = new LoadingDialog(context, "正在删除场景...");

        TextView sceneNameTxt = (TextView) findViewById(R.id.scene_name);
        sceneNameTxt.setText(sceneName);

        LinearLayout deleteLinear = (LinearLayout) findViewById(R.id.delete_scene);
        deleteLinear.setOnClickListener(new OnClickDeleteSceneListener());
    }

    /**
     * 添加设备回调
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAddSceneEvent(AddOrUpSceneEvent event) {
        loadingDialog.close();
        if (IFishTankError.SUCCESS == event.msg.arg2) {
            Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
            FishTankUserApiManager.getInstance().toGetDevCfg();
            finish();
        } else {
            Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
        }
    }

    private class OnClickLeftListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            finish();
        }
    }

    private class OnClickDeleteSceneListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (isLastOne) {
                Toast.makeText(context, "至少保留一个场景!", Toast.LENGTH_SHORT).show();
                return;
            }
            loadingDialog.show();
            CloudApi.DevCfg devCfg = new CloudApi.DevCfg();
            devCfg.Type = 0;
            devCfg.Vendor = "Vendor";
            devCfg.Model = "Model";
            devCfg.Uid = "Uid";
            devCfg.User = "User";
            devCfg.Pwd = "Pwd";
            List<Scene> scenes = DataBaseManager.queryAllScene();
            for (int i = 0; i < scenes.size(); i++) {
                Scene scene = scenes.get(i);
                if (TextUtils.equals(sceneID, scene.getSceneID())) {
                    scenes.remove(i);
                    break;
                }
            }
            Gson gson = new Gson();
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
