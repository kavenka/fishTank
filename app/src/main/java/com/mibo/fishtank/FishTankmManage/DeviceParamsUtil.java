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
     * 缓存设备参数
     * @param uid
     * @param msgGetParamRsp
     */
    public static boolean saveDeviceParams(Context context, String uid, IFishTankApi.MsgGetParamRsp msgGetParamRsp) {
        DeviceParams deviceParams = DeviceParamsUtil.parseMsgGetParamRep2DeviceParams(msgGetParamRsp);
        return DeviceParamsUtil.saveDeviceParams(context,uid, deviceParams);
    }

    /**
     * 缓存设备参数
     * @param context
     * @param uid
     * @param deviceParams
     * @return
     */
    public static boolean saveDeviceParams(Context context, String uid, DeviceParams deviceParams) {
        String json = DeviceParamsUtil.parseDeviceParams2Json(deviceParams);
        return SharedPreferencesUtil.putString(context, uid, json);
    }

    /**
     * 转换DeviceParams对象为json字符串
     * @param deviceParams
     * @return
     */
    public static String parseDeviceParams2Json(DeviceParams deviceParams) {
        Gson g = new Gson();
        String json = g.toJson(deviceParams);
//        Log.d("monty", "DeviceParamsUtil -> parseDeviceParams2Json -> json : " + json);
        return json;
    }

    /**
     * 解析json转换成DeviceParams对象
     * @param json
     * @return
     */
    public static DeviceParams parseJson2DeviceParams(String json) {
        Gson g = new Gson();
        DeviceParams deviceParams = g.fromJson(json, DeviceParams.class);
//        Log.d("monty", "DeviceParamsUtil -> parseJson2DeviceParams -> DeviceParams : " + deviceParams.toString());
        return deviceParams;
    }

    /**
     * 根据定时器编号在32组定时数据中查找四组定时器参数，并返回四组参数位于32组数据中的下标
     * @return
     */
    public static int[] getTimerParamsIndexFromSwitchNo(int switchNo, DeviceParams.Alarm[] alarms){
        int[] indexes = new int[4]; // 目前需求只需要设置四个开关

        int count=0;
        for (int i = 0; i < alarms.length; i++) {
            if((byte)switchNo == alarms[i].SwNo){
                if(count == 4){ // 已经找到4组就不再找了
                    break;
                }
                indexes[count] = i;
                count++;
            }
        }
        return indexes;
    }

    /**
     * 检查设置星期参数是否有效
     * 说明：有效->最少有一天为选中的
     *
     * @param weeksBool
     * @return
     */
    public static boolean checkAvailability(boolean[] weeksBool) {
        if (weeksBool == null || weeksBool.length == 0) {
            return false;
        }
        for (boolean b : weeksBool) {
            if (b) {
                return true;
            }
        }
        return false;
    }


    /**
     * 将MsgGetParamRsp封装成DeviceParams对象
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
        //当最大ph值不符合正常范围时，默认设置为14
        if(msgGetParamRsp.PhMax==0||msgGetParamRsp.PhMax<=msgGetParamRsp.PhMin){
            deviceParams.PhMax = 14;
        }else{
            deviceParams.PhMax = msgGetParamRsp.PhMax;
        }
        deviceParams.PhMin = msgGetParamRsp.PhMin;
        deviceParams.Temp = msgGetParamRsp.Temp;
        //当最大Temp值不符合正常范围时，默认设置为40
        if(msgGetParamRsp.TempMax==0||msgGetParamRsp.TempMax<=msgGetParamRsp.TempMin){
            deviceParams.TempMax = 40;
        }else{
            deviceParams.TempMax = msgGetParamRsp.TempMax;
        }
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
