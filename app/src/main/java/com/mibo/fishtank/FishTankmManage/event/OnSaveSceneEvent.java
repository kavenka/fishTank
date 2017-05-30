package com.mibo.fishtank.FishTankmManage.event;

import com.mibo.fishtank.entity.Scene;

import java.util.ArrayList;

/**
 * Created by Administrator
 * on 2017/5/30 0030.
 */

public class OnSaveSceneEvent {
    public ArrayList<Scene> scenes;

    public OnSaveSceneEvent(ArrayList<Scene> scenes) {
        this.scenes = scenes;
    }
}
