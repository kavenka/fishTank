package com.mibo.fishtank.FishTankmManage;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.Arrays;

/**
 * Created by Monty on 2017/6/1.
 */

public class DeviceParams implements Parcelable {
    // 开关
    public boolean Pump; // 水泵
    public boolean OxygenPump; // 氧气泵
    public boolean Heater1;  // 加热泵1
    public boolean Heater2; // 加热泵2
    public boolean Light1; // 灯光1
    public boolean Light2; // 灯光2
    public boolean Rfu1; // 备用1
    public boolean Rfu2; // 备用1

    // 参数
    public float Ph; // PH值
    public float PhMax; // PH报警上限
    public float PhMin; // PH报警下限
    public float Temp; // 温度值
    public float TempMax; // 温度报警上限
    public float TempMin; // 温度报警下限
    public float[] PhCal; // PH校准值，数组长度为6，内容为：校准值0，实测值0，校准值1，实测值1，校准值2，实测值2
    public int ViewMode; //观赏时间 以秒为单位
    public Alarm[] Alarms; // 定时器，最多8组，每组4个，最多32个
    public String[] Tel; // 用于接受报警电话，目前为4个
    public String time; // 设备的实时时间

    @Override
    public String toString() {
        return "DeviceParams{" +
                "Pump=" + Pump +
                ", OxygenPump=" + OxygenPump +
                ", Heater1=" + Heater1 +
                ", Heater2=" + Heater2 +
                ", Light1=" + Light1 +
                ", Light2=" + Light2 +
                ", Rfu1=" + Rfu1 +
                ", Rfu2=" + Rfu2 +
                ", Ph=" + Ph +
                ", PhMax=" + PhMax +
                ", PhMin=" + PhMin +
                ", Temp=" + Temp +
                ", TempMax=" + TempMax +
                ", TempMin=" + TempMin +
                ", PhCal=" + Arrays.toString(PhCal) +
                ", ViewMode=" + ViewMode +
                ", Alarms=" + Arrays.toString(Alarms) +
                ", Tel=" + Arrays.toString(Tel) +
                ", time='" + time + '\'' +
                '}';
    }


    public static class Alarm implements Parcelable {
        public byte WeekMask;  // 需要将此字段转换成8位二进制编码，前7为为周日到周六是否选择(1为选择，0为未选择)，第8位来控制定时器开关
        public byte Hour; // 时
        public byte Min; // 分
        public byte Sec;  // 秒
        public byte SwNo; // 开关编号
        public byte OnOff; // 设备操作：true为开启，false为关闭


        private char[] parseWeekMask() {
            String s = Integer.toBinaryString((WeekMask & 0xFF) + 0x100).substring(1);
            Log.d("monty", "parseWeekMask -> 原始值 -> " + WeekMask + " , 转换成二进制后 -> " + s);
            return s.toCharArray();
        }

        public boolean[] getWeekMask() {
            boolean[] weeksBool = new boolean[7];
            char[] chars = parseWeekMask();
            for (int i = 0; i < chars.length - 1; i++) {
                weeksBool[i] = 1 == (int) chars[i];
            }
            return weeksBool;
        }

        public boolean getAlarmSwitch() {
            String s = Integer.toBinaryString((WeekMask & 0xFF) + 0x100).substring(1);
            String switchFlag = s.substring(s.length() - 1, s.length());
            return "1".equals(switchFlag);
        }

        public void setAlarmSwitch(boolean flag) {
            char[] chars = parseWeekMask();
            chars[7] = flag ? '1' : '0';
            WeekMask = (byte) Integer.parseInt(chars.toString(), 2);
        }

        public void setWeek(boolean[] flags) {
            char[] chars = parseWeekMask();
            Log.d("monty", "setWeek -> 设置前" + chars.toString());
            for (int i = 0; i < flags.length; i++) {
                chars[i] = flags[i] ? '1' : '0';
            }
            Log.d("monty", "setWeek -> 设置后" + chars.toString());
            WeekMask = (byte) Integer.parseInt(String.valueOf(chars), 2);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeByte(this.WeekMask);
            dest.writeByte(this.Hour);
            dest.writeByte(this.Min);
            dest.writeByte(this.Sec);
            dest.writeByte(this.SwNo);
            dest.writeByte(this.OnOff);
        }

        public Alarm() {
        }

        protected Alarm(Parcel in) {
            this.WeekMask = in.readByte();
            this.Hour = in.readByte();
            this.Min = in.readByte();
            this.Sec = in.readByte();
            this.SwNo = in.readByte();
            this.OnOff = in.readByte();
        }

        public static final Creator<Alarm> CREATOR = new Creator<Alarm>() {
            @Override
            public Alarm createFromParcel(Parcel source) {
                return new Alarm(source);
            }

            @Override
            public Alarm[] newArray(int size) {
                return new Alarm[size];
            }
        };

        @Override
        public String toString() {
            return "Alarm{" +
                    "WeekMask=" + WeekMask +
                    ", Hour=" + Hour +
                    ", Min=" + Min +
                    ", Sec=" + Sec +
                    ", SwNo=" + SwNo +
                    ", OnOff=" + OnOff +
                    '}';
        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.Pump ? (byte) 1 : (byte) 0);
        dest.writeByte(this.OxygenPump ? (byte) 1 : (byte) 0);
        dest.writeByte(this.Heater1 ? (byte) 1 : (byte) 0);
        dest.writeByte(this.Heater2 ? (byte) 1 : (byte) 0);
        dest.writeByte(this.Light1 ? (byte) 1 : (byte) 0);
        dest.writeByte(this.Light2 ? (byte) 1 : (byte) 0);
        dest.writeByte(this.Rfu1 ? (byte) 1 : (byte) 0);
        dest.writeByte(this.Rfu2 ? (byte) 1 : (byte) 0);
        dest.writeFloat(this.Ph);
        dest.writeFloat(this.PhMax);
        dest.writeFloat(this.PhMin);
        dest.writeFloat(this.Temp);
        dest.writeFloat(this.TempMax);
        dest.writeFloat(this.TempMin);
        dest.writeFloatArray(this.PhCal);
        dest.writeInt(this.ViewMode);
        dest.writeTypedArray(this.Alarms, flags);
        dest.writeStringArray(this.Tel);
        dest.writeString(this.time);
    }

    public DeviceParams() {
    }

    protected DeviceParams(Parcel in) {
        this.Pump = in.readByte() != 0;
        this.OxygenPump = in.readByte() != 0;
        this.Heater1 = in.readByte() != 0;
        this.Heater2 = in.readByte() != 0;
        this.Light1 = in.readByte() != 0;
        this.Light2 = in.readByte() != 0;
        this.Rfu1 = in.readByte() != 0;
        this.Rfu2 = in.readByte() != 0;
        this.Ph = in.readFloat();
        this.PhMax = in.readFloat();
        this.PhMin = in.readFloat();
        this.Temp = in.readFloat();
        this.TempMax = in.readFloat();
        this.TempMin = in.readFloat();
        this.PhCal = in.createFloatArray();
        this.ViewMode = in.readInt();
        this.Alarms = in.createTypedArray(Alarm.CREATOR);
        this.Tel = in.createStringArray();
        this.time = in.readString();
    }

    public static final Creator<DeviceParams> CREATOR = new Creator<DeviceParams>() {
        @Override
        public DeviceParams createFromParcel(Parcel source) {
            return new DeviceParams(source);
        }

        @Override
        public DeviceParams[] newArray(int size) {
            return new DeviceParams[size];
        }
    };
}
