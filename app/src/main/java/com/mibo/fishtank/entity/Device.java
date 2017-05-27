package com.mibo.fishtank.entity;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator
 * on 2017/5/25 0025.
 */

public class Device extends DataSupport {

    private String deviceName;

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
}
