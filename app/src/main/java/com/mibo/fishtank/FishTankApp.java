package com.mibo.fishtank;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.mibo.fishtank.FishTankmManage.FishTankApiManager;
import com.mibo.fishtank.FishTankmManage.FishTankUserApiManager;
import com.mibo.fishtank.utils.Constans;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;

import org.litepal.LitePal;

import java.util.List;

/**
 * Created by Administrator
 * on 2017/5/25 0025.
 */

public class FishTankApp extends Application {
    private static FishTankApp mFishTankApp;

    public static FishTankApp getInstance() {
        return mFishTankApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mFishTankApp = this;
        Log.d("monty", "FishTankApp：onCreate mFishTankApp:" + mFishTankApp.hashCode());

        String processName = getProcessName(this, android.os.Process.myPid());
        Log.d("monty", "processName:" + processName);
        if (processName != null) {
            boolean defaultProcess = processName.equals(FishTankApp.this.getPackageName());
            if (defaultProcess) {
                //必要的初始化资源操作
                FishTankApiManager.getInstance().init();
                FishTankUserApiManager.getInstance().init();
                LitePal.initialize(this);
            }
        }

        // 开启logcat输出，方便debug，发布时请关闭
        XGPushConfig.enableDebug(this, true);

        XGPushManager.registerPush(this, new XGIOperateCallback() {
            @Override
            public void onSuccess(Object data, int flag) {
                Log.d("TPush", "注册成功，设备token为：" + data);
                Constans.XG_TOKEN = data + "";
            }

            @Override
            public void onFail(Object data, int errCode, String msg) {
                Log.d("TPush", "注册失败，错误码：" + errCode + ",错误信息：" + msg);
            }
        });
    }

    public static String getProcessName(Context cxt, int pid) {
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }
}
