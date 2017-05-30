package com.mibo.fishtank.entity;

/**
 * Created by Monty on 2017/5/29.
 */

public class DeviceSwitch {
    public String name;
    public boolean isOpen;

    public DeviceSwitch(String name, boolean isOpen) {
        this.name = name;
        this.isOpen = isOpen;
    }
}
