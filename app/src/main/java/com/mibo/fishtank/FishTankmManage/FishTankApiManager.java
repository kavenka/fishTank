package com.mibo.fishtank.FishTankmManage;

import android.util.Log;

import com.landstek.iFishTank.IFishTankApi;
import com.mibo.fishtank.FishTankApp;
import com.mibo.fishtank.FishTankmManage.event.GetParameterEvent;
import com.mibo.fishtank.FishTankmManage.event.LoginEvent;
import com.mibo.fishtank.FishTankmManage.event.ScanDevEvent;
import com.mibo.fishtank.FishTankmManage.event.SetDevicePwdEvent;
import com.mibo.fishtank.FishTankmManage.event.SetParamsEvent;
import com.mibo.fishtank.FishTankmManage.event.SetPhAndTempEvent;
import com.mibo.fishtank.FishTankmManage.event.SetTelEvent;
import com.mibo.fishtank.FishTankmManage.event.SetTimerEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import static com.mibo.fishtank.FishTankmManage.FishTankApiManager.OperationType.SET_DEFAULT;
import static com.mibo.fishtank.FishTankmManage.FishTankApiManager.OperationType.SET_DEVICEPWD;
import static com.mibo.fishtank.FishTankmManage.FishTankApiManager.OperationType.SET_PHANDTEMPPARAMS;
import static com.mibo.fishtank.FishTankmManage.FishTankApiManager.OperationType.SET_TELPARAM;
import static com.mibo.fishtank.FishTankmManage.FishTankApiManager.OperationType.SET_TIMERPARAMS;

/**
 * Created by Monty on 2017/5/29.
 */

public final class FishTankApiManager implements IFishTankApi.IFishTankApiInterface {

    private static FishTankApiManager fishTankApiManager;
    private IFishTankApi mFishTankApi;

    private long interval = 10 * 1000;

    private OperationType operationType = SET_DEFAULT;

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

    public enum OperationType {
        SET_PHANDTEMPPARAMS, SET_TIMERPARAMS, SET_TELPARAM, SET_DEVICEPWD, SET_DEFAULT
    }

    private Timer loginTimer = null;

    /**
     * 设备登录
     *
     * @param uid
     */
    public void loginDevice(final String uid, String pwd) {
        loginTimer = new Timer();
        loginTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                EventBus.getDefault().post(new LoginEvent(uid, -1, "登录超时"));
            }
        }, interval);
        Log.d("monty", "FishTankApiManager -> loginDevice -> uid:" + uid + ",user:admin" + ",pwd:" + pwd);
        IFishTankApi.MsgLoginCmd msgLoginCmd = new IFishTankApi.MsgLoginCmd();
        msgLoginCmd.User = "admin";
        msgLoginCmd.Pwd = pwd;
        int loginResult = mFishTankApi.FtLogin(uid, msgLoginCmd);
        Log.d("monty", "FishTankApiManager -> loginResult:" + loginResult);
    }

    private Timer getDeviceParamTimer = null;

    /**
     * 获取设备参数
     *
     * @param uid
     */
    public void getDeviceParam(final String uid) {
        getDeviceParamTimer = new Timer();
        getDeviceParamTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                EventBus.getDefault().post(new GetParameterEvent(uid, -1, null));
            }
        }, interval);
        int getParamResult = mFishTankApi.FtGetParam(uid);
        Log.d("monty", "FishTankApiManager -> getDeviceParam -> getParamResult:" + getParamResult);
    }

    /**
     * 获取PH实测值，此接口需要定时调用，一秒钟调一次
     *
     * @param uid
     */
    public void getPhParam(String uid) {
        IFishTankApi.MsgGetParamCmd msgGetParamCmd = new IFishTankApi.MsgGetParamCmd();
        msgGetParamCmd.Ph = true;
        mFishTankApi.FtGetParam(uid, msgGetParamCmd);
    }

    /**
     * 设置温度和ph值
     *
     * @param uid
     * @param deviceParams
     */
    public void setPhAndTempParams(String uid, DeviceParams deviceParams) {
        IFishTankApi.MsgSetParamCmd msgSetParamCmd = new IFishTankApi.MsgSetParamCmd();
        msgSetParamCmd.PhMin = deviceParams.PhMin;
        msgSetParamCmd.PhMax = deviceParams.PhMax;
        msgSetParamCmd.TempMin = deviceParams.TempMin;
        msgSetParamCmd.TempMax = deviceParams.TempMax;

        operationType = SET_PHANDTEMPPARAMS;
        setDeviceParam(uid, msgSetParamCmd);

    }

    /**
     * 校准ph值
     *
     * @param uid
     * @param phCal
     */
    public void setPhCal(String uid, Float[] phCal) {
        IFishTankApi.MsgSetParamCmd msgSetParamCmd = new IFishTankApi.MsgSetParamCmd();
        msgSetParamCmd.PhCal = phCal;
        operationType = SET_DEFAULT;
        setDeviceParam(uid, msgSetParamCmd);
    }

    /**
     * 设置定时器
     *
     * @param uid
     * @param alarm
     */
    public void setTimerParams(String uid, DeviceParams.Alarm[] alarm) {
        IFishTankApi.MsgSetParamCmd msgSetParamCmd = new IFishTankApi.MsgSetParamCmd();
        if (msgSetParamCmd.Alarms == null) {
            msgSetParamCmd.Alarms = new IFishTankApi.Alarm[alarm.length];
        }
        for (int i = 0; i < alarm.length; i++) {
            IFishTankApi.Alarm iAlarm = new IFishTankApi.Alarm();
            iAlarm.fromBytes(alarm[i].toBytes());
            msgSetParamCmd.Alarms[i] = iAlarm;
        }

        operationType = SET_TIMERPARAMS;
        setDeviceParam(uid, msgSetParamCmd);

    }

    private Timer setDeviceTimer = null;

    /**
     * 设置设备参数
     *
     * @param uid
     * @param msgSetParamCmd
     */
    public void setDeviceParam(final String uid, IFishTankApi.MsgSetParamCmd msgSetParamCmd) {
        setDeviceTimer = new Timer();
        setDeviceTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (operationType == SET_PHANDTEMPPARAMS) {
                    EventBus.getDefault().post(new SetPhAndTempEvent(uid, -1, "通信超时"));
                } else if (operationType == SET_DEVICEPWD) {
                    EventBus.getDefault().post(new SetDevicePwdEvent(uid, -1, "通信超时"));
                } else if (operationType == SET_TELPARAM) {
                    EventBus.getDefault().post(new SetTelEvent(uid, -1, "通信超时"));
                } else if (operationType == SET_TIMERPARAMS) {
                    EventBus.getDefault().post(new SetTimerEvent(uid, -1, "通信超时"));
                } else {
                    EventBus.getDefault().post(new SetParamsEvent(uid, -1, "通信超时"));
                }
            }
        }, interval);
        int setParamResult = mFishTankApi.FtSetParam(uid, msgSetParamCmd);
        Log.d("monty", "FishTankApiManager -> setDeviceParam -> setParamResult:" + setParamResult);
    }

    /**
     * 设置推送手机
     *
     * @param uid
     * @param telParams 手机号
     */
    public void setTelParam(String uid, String[] telParams) {
        IFishTankApi.MsgSetParamCmd msgSetParamCmd = new IFishTankApi.MsgSetParamCmd();
        if (telParams.length > 5) {
            new IllegalArgumentException("手机号码最多只能设置4个");
        }
        msgSetParamCmd.Tel = telParams;

        operationType = SET_TELPARAM;
        setDeviceParam(uid, msgSetParamCmd);
    }

    /**
     * 设置设备密码
     *
     * @param uid
     * @param pwd
     */
    public void setDevicePwd(String uid, String pwd) {
        IFishTankApi.MsgSetParamCmd msgSetParamCmd = new IFishTankApi.MsgSetParamCmd();
        msgSetParamCmd.Pwd = pwd;
        operationType = SET_DEVICEPWD;
        setDeviceParam(uid, msgSetParamCmd);
    }


    //
//    public void setTimerParams(int switchNumber,){
//
//    }

    /**
     * 扫描设备
     */
    public void toScanDev() {
        mFishTankApi.ScanDev("");
    }

    @Override
    public void Event(int i, Object o) {
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
        Log.d("monty", "IFishTankApiInterface -> ScanDevRsp ->"
                + " msgScanDevRsp.Uid=" + msgScanDevRsp.Uid
                + " , msgScanDevRsp.Ip=" + msgScanDevRsp.Ip
                + " , msgScanDevRsp.Port=" + msgScanDevRsp.Port
                + " , msgScanDevRsp.HWVer=" + msgScanDevRsp.HWVer
                + " , msgScanDevRsp.Model=" + msgScanDevRsp.Model
                + " , msgScanDevRsp.SWVer=" + msgScanDevRsp.SWVer);
        EventBus.getDefault().post(new ScanDevEvent(msgScanDevRsp));
    }

    @Override
    public void FtLoginRsp(String uid, int result) {
        if (loginTimer != null) loginTimer.cancel();
        LoginEvent loginEvent = new LoginEvent(uid, result);
        EventBus.getDefault().post(loginEvent);
        Log.d("monty", "IFishTankApiInterface -> FtLoginRsp -> uid=" + uid + " , result=" + result);

    }

    @Override
    public void FtSetParamRsp(String uid, int result) {
        if (setDeviceTimer != null) setDeviceTimer.cancel();
        Log.d("monty", "IFishTankApiInterface -> FtSetParamRsp -> uid=" + uid + " , result=" + result);
        if (operationType == SET_PHANDTEMPPARAMS) {
            EventBus.getDefault().post(new SetPhAndTempEvent(uid, result));
        } else if (operationType == SET_DEVICEPWD) {
            EventBus.getDefault().post(new SetDevicePwdEvent(uid, result));
        } else if (operationType == SET_TELPARAM) {
            EventBus.getDefault().post(new SetTelEvent(uid, result));
        } else if (operationType == SET_TIMERPARAMS) {
            EventBus.getDefault().post(new SetTimerEvent(uid, result));
        } else {
            EventBus.getDefault().post(new SetParamsEvent(uid, result));
        }
    }

    @Override
    public void FtGetParamRsp(String uid, int result, IFishTankApi.MsgGetParamRsp msgGetParamRsp) {
        if (getDeviceParamTimer != null) getDeviceParamTimer.cancel();
        GetParameterEvent getParameterEvent = new GetParameterEvent(uid, result, msgGetParamRsp);
        EventBus.getDefault().post(getParameterEvent);

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
                ", PhCal=" + Arrays.toString(msgGetParamRsp.PhCal) +
                ", Temp=" + msgGetParamRsp.Temp +
                ", TempMax=" + msgGetParamRsp.TempMax +
                ", TempMin=" + msgGetParamRsp.TempMin +
                ", ViewMode=" + msgGetParamRsp.ViewMode +
                ", Alarms=" + formatAlarms(msgGetParamRsp.Alarms) +
                ", Tel=" + Arrays.toString(msgGetParamRsp.Tel) +
                '}');


    }


    public String formatAlarms(IFishTankApi.Alarm[] alarms) {

        if (alarms == null || alarms.length == 0) {
            return "{}";
        }
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        for (IFishTankApi.Alarm alarm : alarms) {
            byte[] bytes = alarm.toBytes();
            sb.append("{");
//            // 此处将weekmacks转换成二进制
//            byte weekMaks = bytes[0];
//            String s = Integer.toBinaryString((weekMaks & 0xFF) + 0x100).substring(1);
//            sb.append("week:");
//            sb.append(s + ",");
            for (int i = 0; i < bytes.length; i++) {
                sb.append((int) bytes[i] + ",");
            }

            sb.append("}");
        }
        sb.append("}");
        return sb.toString();
    }

}
