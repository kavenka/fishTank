package com.mibo.fishtank.FishTankmManage.event;

import android.os.Message;

/**
 * Created by Administrator on 2017/6/25 0025.
 */

public class UpdateUserPwdEvent {
    public Message msg;

    public UpdateUserPwdEvent(Message msg) {
        this.msg = msg;
    }
}
