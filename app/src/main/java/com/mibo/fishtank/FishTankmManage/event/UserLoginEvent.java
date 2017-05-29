package com.mibo.fishtank.FishTankmManage.event;

import android.os.Message;

/**
 * Created by Administrator
 * on 2017/5/29 0029.
 */

public class UserLoginEvent {
    public Message msg;

    public UserLoginEvent(Message msg) {
        this.msg = msg;
    }
}
