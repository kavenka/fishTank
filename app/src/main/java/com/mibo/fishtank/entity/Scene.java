package com.mibo.fishtank.entity;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator
 * on 2017/5/25 0025.
 */

public class Scene extends DataSupport {
    @Column(unique = true)
    private String sceneName;

    private List<Device> devices = new ArrayList<>();

    public String getSceneName() {
        return sceneName;
    }

    public void setSceneName(String sceneName) {
        this.sceneName = sceneName;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }
}
