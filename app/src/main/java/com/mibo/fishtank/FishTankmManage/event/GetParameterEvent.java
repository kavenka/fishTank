package com.mibo.fishtank.FishTankmManage.event;

import com.landstek.iFishTank.IFishTankApi;

/**
 * Created by Monty on 2017/5/29.
 */

public class GetParameterEvent {
    public String uid;
    public int result;
    public IFishTankApi.MsgGetParamRsp msgGetParamRsp;

    public GetParameterEvent(String uid, int result, IFishTankApi.MsgGetParamRsp msgGetParamRsp) {
        this.uid = uid;
        this.result = result;
        this.msgGetParamRsp = msgGetParamRsp;
    }
}
