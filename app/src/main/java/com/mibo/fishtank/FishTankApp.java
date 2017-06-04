package com.mibo.fishtank;

import android.app.Application;
import android.util.Log;

import com.mibo.fishtank.FishTankmManage.FishTankApiManager;
import com.mibo.fishtank.FishTankmManage.FishTankUserApiManager;
import com.tencent.android.tpush.XGIOperateCallback;
import com.tencent.android.tpush.XGPushConfig;
import com.tencent.android.tpush.XGPushManager;

import org.litepal.LitePal;

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
        // 开启logcat输出，方便debug，发布时请关闭
        XGPushConfig.enableDebug(this, true);

        XGPushManager.registerPush(this, new XGIOperateCallback() {
            @Override
            public void onSuccess(Object data, int flag) {
                Log.d("TPush", "注册成功，设备token为：" + data);
            }

            @Override
            public void onFail(Object data, int errCode, String msg) {
                Log.d("TPush", "注册失败，错误码：" + errCode + ",错误信息：" + msg);
            }
        });
        LitePal.initialize(this);
        FishTankApiManager.getInstance().init();
        FishTankUserApiManager.getInstance().init();
    }
}
