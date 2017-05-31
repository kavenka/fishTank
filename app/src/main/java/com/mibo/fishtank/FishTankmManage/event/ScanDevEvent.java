package com.mibo.fishtank.FishTankmManage.event;

import com.landstek.iFishTank.IFishTankApi;

/**
 * Created by Administrator
 * on 2017/6/1 0001.
 */

public class ScanDevEvent {
    public IFishTankApi.MsgScanDevRsp msgScanDevRsp;

    public ScanDevEvent(IFishTankApi.MsgScanDevRsp msgScanDevRsp) {
        this.msgScanDevRsp = msgScanDevRsp;
    }
}
