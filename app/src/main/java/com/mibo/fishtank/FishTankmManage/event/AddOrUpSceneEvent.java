package com.mibo.fishtank.FishTankmManage.event;

import android.os.Message;

/**
 * Created by Administrator
 * on 2017/5/30 0030.
 */

public class AddOrUpSceneEvent {
    public int flag = 0;
    public Message msg;

    public AddOrUpSceneEvent(Message msg) {
        this.msg = msg;
    }

    public AddOrUpSceneEvent(Message msg, int flag) {
        this.msg = msg;
        this.flag = flag;
    }
}
