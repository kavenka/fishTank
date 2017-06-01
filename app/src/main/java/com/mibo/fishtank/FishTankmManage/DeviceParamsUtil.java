package com.mibo.fishtank.FishTankmManage;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.landstek.iFishTank.IFishTankApi;

/**
 * Created by Monty on 2017/5/31.
 */

public class DeviceParamsUtil {

    /**
     * 通过设备uid获取设备参数
     * @param uid
     * @return
     */
    public static DeviceParams getDeviceParams(Context context,String uid){
        String json = SharedPreferencesUtil.getString(context,uid);
        if(TextUtils.isEmpty(json)){
            return null;
        }
        Log.d("monty", "DeviceParamsUtil -> getDeviceParams -> json : " + json);
        return DeviceParamsUtil.parseJson2DeviceParams(json);
    }
    /**
     * 保存设备参数到SharedPreferences
     * @param uid
     * @param msgGetParamRsp
     */
    public static boolean saveDeviceParams(Context context, String uid, IFishTankApi.MsgGetParamRsp msgGetParamRsp) {
        DeviceParams deviceParams = DeviceParamsUtil.parseMsgGetParamRep2DeviceParams(msgGetParamRsp);
        return DeviceParamsUtil.saveDeviceParams(context,uid, deviceParams);
    }

    public static boolean saveDeviceParams(Context context, String uid, DeviceParams deviceParams) {
        String json = DeviceParamsUtil.parseDeviceParams2Json(deviceParams);
        return SharedPreferencesUtil.putString(context, uid, json);
    }

    public static String parseDeviceParams2Json(DeviceParams deviceParams) {
        Gson g = new Gson();
        String json = g.toJson(deviceParams);
        Log.d("monty", "DeviceParamsUtil -> parseDeviceParams2Json -> json : " + json);
        return json;
    }

    public static DeviceParams parseJson2DeviceParams(String json) {
        Gson g = new Gson();
        DeviceParams deviceParams = g.fromJson(json, DeviceParams.class);
        Log.d("monty", "DeviceParamsUtil -> parseJson2DeviceParams -> DeviceParams : " + deviceParams.toString());
        return deviceParams;
    }


    /**
     * 封装DeviceParams
     *
     * @param msgGetParamRsp
     * @return
     */
    public static DeviceParams parseMsgGetParamRep2DeviceParams(IFishTankApi.MsgGetParamRsp msgGetParamRsp) {
        DeviceParams deviceParams = new DeviceParams();

        deviceParams.Pump = msgGetParamRsp.Pump;
        deviceParams.OxygenPump = msgGetParamRsp.OxygenPump;
        deviceParams.Light1 = msgGetParamRsp.Light1;
        deviceParams.Light2 = msgGetParamRsp.Light2;
        deviceParams.Heater1 = msgGetParamRsp.Heater1;
        deviceParams.Heater2 = msgGetParamRsp.Heater2;
        deviceParams.Rfu1 = msgGetParamRsp.Rfu1;
        deviceParams.Rfu2 = msgGetParamRsp.Rfu2;

        deviceParams.Ph = msgGetParamRsp.Ph;
        deviceParams.PhMax = msgGetParamRsp.PhMax;
        deviceParams.PhMin = msgGetParamRsp.PhMin;
        deviceParams.Temp = msgGetParamRsp.Temp;
        deviceParams.TempMax = msgGetParamRsp.TempMax;
        deviceParams.TempMin = msgGetParamRsp.TempMin;
        deviceParams.PhCal = msgGetParamRsp.PhCal;
        deviceParams.ViewMode = msgGetParamRsp.ViewMode;

        IFishTankApi.Alarm[] alarms = msgGetParamRsp.Alarms;
        DeviceParams.Alarm[] devAlarms = new DeviceParams.Alarm[alarms.length];
        for (int i = 0; i < alarms.length; i++) {
            IFishTankApi.Alarm alarm = alarms[i];
            DeviceParams.Alarm devAlarm = new DeviceParams.Alarm();
//            this.WeekMask, this.Hour, this.Min, this.Sec, this.SwNo, (byte)(this.OnOff?1:0)
            devAlarm.WeekMask = alarm.toBytes()[0];
            devAlarm.Hour = alarm.toBytes()[1];
            devAlarm.Min = alarm.toBytes()[2];
            devAlarm.Sec = alarm.toBytes()[3];
            devAlarm.SwNo = alarm.toBytes()[4];
            devAlarm.OnOff = alarm.toBytes()[5];
            devAlarms[i] = devAlarm;
        }
        deviceParams.Alarms = devAlarms;
        deviceParams.Tel = msgGetParamRsp.Tel;
        deviceParams.time = msgGetParamRsp.Time.toString();

        return deviceParams;
    }

}
