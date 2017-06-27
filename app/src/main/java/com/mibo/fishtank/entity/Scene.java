package com.mibo.fishtank.entity;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;

/**
 * Created by Administrator
 * on 2017/5/25 0025.
 */

public class Scene extends DataSupport {
    private String sceneID;//场景
    private String name;//场景名字
    private ArrayList<String> devices=new ArrayList<>();//设备的Uid集合


    public void parserEntity(JSONObject object) {
        try {
            sceneID = object.getString("sceneID");
            name = object.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSceneID() {
        return sceneID;
    }

    public void setSceneID(String sceneID) {
        this.sceneID = sceneID;
    }

    public ArrayList<String> getDevices() {
        return devices;
    }

    public void setDevices(ArrayList<String> devices) {
        this.devices = devices;
    }
}
