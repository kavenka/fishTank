package com.mibo.fishtank.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.landstek.iFishTank.IFishTankError;
import com.mibo.fishtank.FishTankmManage.event.DevCfgEvent;
import com.mibo.fishtank.R;
import com.mibo.fishtank.adapter.SceneAdapter;
import com.mibo.fishtank.entity.DevCfgEntity;
import com.mibo.fishtank.entity.Scene;
import com.mibo.fishtank.utils.DataBaseManager;
import com.mibo.fishtank.weight.LoadingDialog;
import com.mibo.fishtank.weight.TitleBar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class SceneSettingActivity extends BaseActivity {
    private Context context = this;
    private List<Scene> scenes = new ArrayList<>();
    private LoadingDialog loadingDialog;
    private SceneAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        setContentView(R.layout.scene_setting_activity);
        initView();
        toGetAllScenes();
    }

    private void initView() {
        TitleBar titleBar = (TitleBar) findViewById(R.id.scene_setting_title);
        titleBar.setCenterStr(R.string.main_scene_setting);
        titleBar.setOnClickLeftListener(new OnClickLeftListener());

        loadingDialog = new LoadingDialog(context, "正在更新场景...");

        ListView sceneListView = (ListView) findViewById(R.id.scene_list_view);
        sceneListView.setOnItemClickListener(new OnClickSceneItemListener());
        adapter = new SceneAdapter(context, scenes);
        sceneListView.setAdapter(adapter);

    }

    private void toGetAllScenes() {
        scenes.clear();
        scenes.addAll(DataBaseManager.queryAllScene());
        scenes.add(new Scene());
        adapter.notifyDataSetChanged();
    }

    /**
     * 当前用户下的设备信息
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetDevCfgEvent(DevCfgEvent event) {
        loadingDialog.close();
        Message msg = event.msg;
        if (IFishTankError.SUCCESS == msg.arg2) {
            String resultJson = msg.obj.toString();
            DevCfgEntity devCfgEntity = new DevCfgEntity();
            devCfgEntity.parserEntity(resultJson);
            scenes.clear();
            scenes.addAll(devCfgEntity.scenes);
            scenes.add(new Scene());
            adapter.notifyDataSetChanged();

            DataBaseManager.saveScene(scenes);//存储场景数据库

        } else {
            Toast.makeText(context, "获取场景和设备信息失败", Toast.LENGTH_SHORT).show();
        }
    }

    private class OnClickLeftListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            finish();
        }
    }

    private class OnClickSceneItemListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (position == scenes.size() - 1) {
                Intent intent = new Intent(context, AddSceneActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(context, SceneManagerActivity.class);
                intent.putExtra("sceneName", scenes.get(position).getName());
                intent.putExtra("sceneID", scenes.get(position).getSceneID());
                startActivity(intent);
            }
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
