package com.mibo.fishtank.FishTankmManage.event;

/**
 * Created by Monty on 2017/5/30.
 */

public class SetParamsEvent {
    public String uid;
    public int result;
    public String msg;

    public SetParamsEvent(String uid, int result) {
        this.uid = uid;
        this.result = result;
    }
    public SetParamsEvent(String uid, int result,String msg) {
        this.uid = uid;
        this.result = result;
        this.msg = msg;
    }
}
