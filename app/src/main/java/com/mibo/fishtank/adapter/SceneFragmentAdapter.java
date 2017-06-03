package com.mibo.fishtank.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mibo.fishtank.entity.Scene;
import com.mibo.fishtank.fragment.SceneFragment;

import java.util.ArrayList;

/**
 * Created by Administrator
 * on 2017/5/30 0030.
 */

public class SceneFragmentAdapter extends FragmentPagerAdapter {
    private  ArrayList<Scene> scenes;

    public SceneFragmentAdapter(FragmentManager fm, ArrayList<Scene> scenes) {
        super(fm);
        this.scenes = scenes;
    }

    @Override
    public Fragment getItem(int position) {
        SceneFragment sceneFragment = SceneFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putString("sceneId",scenes.get(position).getSceneID());
        bundle.putStringArrayList("devices",scenes.get(position).getDevices());
        sceneFragment.setArguments(bundle);
        return sceneFragment;
    }

    @Override
    public int getCount() {
        return scenes.size();
    }
}
