package com.mibo.fishtank.FishTankmManage;

import android.util.Log;

import com.landstek.iFishTank.IFishTankApi;
import com.mibo.fishtank.FishTankApp;
import com.mibo.fishtank.FishTankmManage.event.GetParameterEvent;
import com.mibo.fishtank.FishTankmManage.event.LoginEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;

/**
 * Created by Monty on 2017/5/29.
 */

public class FishTankApiManager implements IFishTankApi.IFishTankApiInterface {

    private static FishTankApiManager fishTankApiManager;
    private IFishTankApi mFishTankApi;

    public static FishTankApiManager getInstance() {
        if (fishTankApiManager == null) {
            fishTankApiManager = new FishTankApiManager();
        }
        return fishTankApiManager;
    }

    public void init() {
        mFishTankApi = new IFishTankApi(FishTankApp.getInstance());
        mFishTankApi.SetIFishTankApiInterface(this);
    }

    /**
     * 登录设备
     */
    public void loginDevice(String uid) {
        Log.d("monty", "FishTankApiManager -> loginDevice -> uid:" + uid);
        IFishTankApi.MsgLoginCmd msgLoginCmd = new IFishTankApi.MsgLoginCmd();
        msgLoginCmd.User = "admin";
        msgLoginCmd.Pwd = "12345678";
        int loginResult = mFishTankApi.FtLogin(uid, msgLoginCmd);
        Log.d("monty", "FishTankApiManager -> loginResult:" + loginResult);
    }

    /**
     * 获取设备信息
     *
     * @param uid
     */
    public void getDeviceParam(String uid) {
        Log.d("monty", "FishTankApiManager -> getDeviceParam -> uid:" + uid);

        int getParamResult = mFishTankApi.FtGetParam(uid);
        Log.d("monty", "FishTankApiManager -> getParamResult:" + getParamResult);

    }

    @Override
    public void Event(int i, Object o) {
//                Log.d("monty", "IFishTankApiInterface -> Event -> i=" + i + (" , o=" + o == null ? "" : o.toString()));
        Log.d("monty", "IFishTankApiInterface -> Event -> i=" + i);

        switch (i) {
            case IFishTankApi.EVT_ID_SERVER_CONNECTED:
                Log.i("monty", "已经连接到远程服务器");
                break;
            case IFishTankApi.EVT_ID_SERVER_CONNECT_LOST:
                Log.i("monty", "远程服务器连接断开了");
                break;
        }
    }

    @Override
    public void ScanDevRsp(IFishTankApi.MsgScanDevRsp msgScanDevRsp) {
        Log.d("monty", "IFishTankApiInterface -> ScanDevRsp -> thread=" + Thread.currentThread().getName());
        Log.d("monty", "IFishTankApiInterface -> ScanDevRsp ->"
                + " msgScanDevRsp.Uid=" + msgScanDevRsp.Uid
                + " , msgScanDevRsp.Ip=" + msgScanDevRsp.Ip
                + " , msgScanDevRsp.Port=" + msgScanDevRsp.Port
                + " , msgScanDevRsp.HWVer=" + msgScanDevRsp.HWVer
                + " , msgScanDevRsp.Model=" + msgScanDevRsp.Model
                + " , msgScanDevRsp.SWVer=" + msgScanDevRsp.SWVer);
    }

    @Override
    public void FtLoginRsp(String uid, int result) {
        LoginEvent loginEvent = new LoginEvent();
        loginEvent.loginSuccess = true;
        EventBus.getDefault().post(loginEvent);
        Log.d("monty", "IFishTankApiInterface -> FtLoginRsp -> thread=" + Thread.currentThread().getName());
        Log.d("monty", "IFishTankApiInterface -> FtLoginRsp -> uid=" + uid + " , result=" + result);

    }

    @Override
    public void FtSetParamRsp(String uid, int result) {
        Log.d("monty", "IFishTankApiInterface -> FtSetParamRsp -> thread=" + Thread.currentThread().getName());
        Log.d("monty", "IFishTankApiInterface -> FtSetParamRsp -> uid=" + uid + " , result=" + result);
    }

    @Override
    public void FtGetParamRsp(String uid, int result, IFishTankApi.MsgGetParamRsp msgGetParamRsp) {
        GetParameterEvent getParameterEvent = new GetParameterEvent();
        getParameterEvent.uid = uid;
        getParameterEvent.result = result;
        getParameterEvent.msgGetParamRsp = msgGetParamRsp;
        EventBus.getDefault().post(getParameterEvent);

        Log.d("monty", "IFishTankApiInterface -> FtGetParamRsp -> thread=" + Thread.currentThread().getName());
        Log.d("monty", "IFishTankApiInterface -> FtGetParamRsp -> uid=" + uid + " , result=" + result);
        Log.d("monty", "IFishTankApiInterface -> FtGetParamRsp -> MsgGetParamRsp{" +
                "Pump=" + msgGetParamRsp.Pump +
                ", OxygenPump=" + msgGetParamRsp.OxygenPump +
                ", Heater1=" + msgGetParamRsp.Heater1 +
                ", Heater2=" + msgGetParamRsp.Heater2 +
                ", Light1=" + msgGetParamRsp.Light1 +
                ", Light2=" + msgGetParamRsp.Light2 +
                ", Rfu1=" + msgGetParamRsp.Rfu1 +
                ", Rfu2=" + msgGetParamRsp.Rfu2 +
                ", Ph=" + msgGetParamRsp.Ph +
                ", PhMax=" + msgGetParamRsp.PhMax +
                ", PhMin=" + msgGetParamRsp.PhMin +
                ", PhCal=" + msgGetParamRsp.PhCal +
                ", Temp=" + msgGetParamRsp.Temp +
                ", TempMax=" + msgGetParamRsp.TempMax +
                ", TempMin=" + msgGetParamRsp.TempMin +
                ", ViewMode=" + msgGetParamRsp.ViewMode +
                ", Alarms=" + Arrays.toString(msgGetParamRsp.Alarms) +
                ", Tel=" + msgGetParamRsp.Tel +
                '}');

    }


}
