package com.mibo.fishtank.FishTankmManage.event;

/**
 * Created by Monty on 2017/6/4.
 */

public class SetTimerEvent extends SetParamsEvent{
    public SetTimerEvent(String uid, int result) {
        super(uid, result);
    }
    public SetTimerEvent(String uid, int result, String msg) {
        super(uid,result,msg);
    }
}
