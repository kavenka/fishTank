package com.mibo.fishtank.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.mibo.fishtank.R;
import com.mibo.fishtank.adapter.SceneAdapter;
import com.mibo.fishtank.entity.Scene;
import com.mibo.fishtank.utils.Constans;
import com.mibo.fishtank.utils.DataBaseManager;
import com.mibo.fishtank.weight.TitleBar;

import java.util.List;

public class SceneSettingActivity extends BaseActivity {
    private Context context = this;
    private List<Scene> scenes;
    private SceneAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scene_setting_activity);
        initView();
    }

    private void initView() {
        TitleBar titleBar = (TitleBar) findViewById(R.id.scene_setting_title);
        titleBar.setCenterStr(R.string.main_scene_setting);
        titleBar.setOnClickLeftListener(new OnClickLeftListener());

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.scene_recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        scenes = DataBaseManager.queryAllSceneByTel(Constans.CURRENT_TEL);
        adapter = new SceneAdapter(context, scenes, new OnClickSceneItemListener());
        recyclerView.setAdapter(adapter);
        LinearLayout addNewSceneLayout = (LinearLayout) findViewById(R.id.add_new_scene_layout);
        addNewSceneLayout.setOnClickListener(new OnClickAddSceneListener());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case 123:
                String sceneName = data.getStringExtra("sceneName");
                if (sceneName != null) {
                    Scene scene = new Scene();
                    scene.setSceneName(sceneName);
                    scenes.add(scene);
                    adapter.notifyDataSetChanged();
                }
                break;
        }
    }

    private class OnClickLeftListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            finish();
        }
    }

    private class OnClickSceneItemListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            int pos = (int) v.getTag();
            Intent intent = new Intent(context, SceneManagerActivity.class);
            intent.putExtra("sceneName", scenes.get(pos).getSceneName());
            startActivity(intent);
        }
    }

    private class OnClickAddSceneListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, AddSceneActivity.class);
            startActivityForResult(intent, 110);
        }
    }

}
