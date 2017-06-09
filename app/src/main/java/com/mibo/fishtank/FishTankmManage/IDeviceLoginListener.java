package com.mibo.fishtank.FishTankmManage;

/**
 * Created by Monty on 2017/5/29.
 */
public interface IDeviceLoginListener {

    void onLoginSeccess();
    void onLoginFailure(String msg);

}
