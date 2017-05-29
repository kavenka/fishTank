package com.mibo.fishtank.FishTankmManage.event;

import android.os.Message;

/**
 * Created by Administrator
 * on 2017/5/30 0030.
 */

public class UserLoginOutEvent {
    public Message msg;

    public UserLoginOutEvent(Message msg) {
        this.msg = msg;
    }
}
