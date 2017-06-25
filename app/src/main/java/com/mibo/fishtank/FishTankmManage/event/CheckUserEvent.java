package com.mibo.fishtank.FishTankmManage.event;

import android.os.Message;

/**
 * Created by Administrator on 2017/6/25 0025.
 */

public class CheckUserEvent {
    public Message msg;

    public CheckUserEvent(Message msg) {
        this.msg = msg;
    }
}
