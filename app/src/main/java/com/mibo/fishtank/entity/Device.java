package com.mibo.fishtank.entity;

import android.util.Base64;

import com.mibo.fishtank.utils.DataBaseManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;

/**
 * Created by Administrator
 * on 2017/5/25 0025.
 */

public class Device extends DataSupport {
    private String Model;
    private String SWVer;
    private String HWVer;
    private String Uid;
    private String Ip;
    private int Port;
    private String User;
    private String DevPwd;

    private ArrayList<String> sceneIds = new ArrayList<>();

    public void parserEntity(JSONObject object) {
        try {
            User = object.getString("User");
            Uid = object.getString("DevUid");
            DevPwd = object.getString("DevPwd");
            String custom = object.getString("Custom");
            String sceneArrayStr = new String(Base64.decode(custom, Base64.DEFAULT));
            String[] strings = sceneArrayStr.split("&");
            for (int i = 0; i < strings.length; i++) {
                sceneIds.add(strings[i]);
            }
            for (int i = 0; i < sceneIds.size(); i++) {
                String s = sceneIds.get(i);
                DataBaseManager.saveDeviceToScene(s, Uid);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public String getHWVer() {
        return HWVer;
    }

    public void setHWVer(String HWVer) {
        this.HWVer = HWVer;
    }

    public String getIp() {
        return Ip;
    }

    public void setIp(String ip) {
        Ip = ip;
    }

    public String getModel() {
        return Model;
    }

    public void setModel(String model) {
        Model = model;
    }

    public int getPort() {
        return Port;
    }

    public void setPort(int port) {
        Port = port;
    }

    public String getSWVer() {
        return SWVer;
    }

    public void setSWVer(String SWVer) {
        this.SWVer = SWVer;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public ArrayList<String> getSceneIds() {
        return sceneIds;
    }

    public void setSceneIds(ArrayList<String> sceneIds) {
        this.sceneIds = sceneIds;
    }
}
