package com.mibo.fishtank.entity;

import org.litepal.crud.DataSupport;

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
}
