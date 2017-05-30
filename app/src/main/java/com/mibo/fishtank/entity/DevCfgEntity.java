package com.mibo.fishtank.entity;

import android.text.TextUtils;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Administrator
 * on 2017/5/30 0030.
 */

public class DevCfgEntity {

    public ArrayList<Scene> scenes;
    public ArrayList<Device> devices;

    public void parserEntity(String jsonObjectStr) {
        try {
            JSONObject jsonObject = new JSONObject(jsonObjectStr);
            scenes = new ArrayList<>();
            devices = new ArrayList<>();
            JSONArray sceneAndDeviceArray = jsonObject.getJSONArray("Data");
            int length = sceneAndDeviceArray.length();
            for (int i = 0; i < length; i++) {
                JSONObject sceneOrDeviceObj = sceneAndDeviceArray.getJSONObject(i);
                String devType = sceneOrDeviceObj.getString("DevType");
                if (TextUtils.equals(devType, "0")) {//场景的解析
                    String devData = sceneOrDeviceObj.getString("DevData");
                    String sceneArrayStr = new String(Base64.decode(devData, Base64.DEFAULT));
                    JSONArray jsonArray = new JSONArray(sceneArrayStr);
                    for (int j = 0; j < length; i++) {
                        JSONObject sceneNameObj = jsonArray.getJSONObject(i);
                        Scene scene = new Scene();
                        scene.parserEntity(sceneNameObj);
                        scenes.add(scene);
                    }
                } else if (TextUtils.equals(devType, "1")){//设备的解析

                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
