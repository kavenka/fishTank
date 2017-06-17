package com.mibo.fishtank.FishTankmManage.event;

/**
 * Created by Monty on 2017/5/29.
 */

public class LoginEvent {
    public String uid;
    public int result;
    public boolean loginSuccess;
    public String msg;

    public LoginEvent(String uid, int result) {
        this.uid = uid;
        this.result = result;
        this.loginSuccess = result == 0;
    }
    public LoginEvent(String uid, int result,String msg) {
        this.uid = uid;
        this.result = result;
        this.loginSuccess = result == 0;
        this.msg = msg;
    }
}
