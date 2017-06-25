package com.mibo.fishtank.FishTankmManage.event;

import android.os.Message;

/**
 * Created by Administrator on 2017/6/25 0025.
 */

public class RegisterEvent {
    public Message msg;

    public RegisterEvent(Message msg) {
        this.msg = msg;
    }
}
