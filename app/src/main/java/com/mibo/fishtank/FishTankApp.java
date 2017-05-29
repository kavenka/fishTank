package com.mibo.fishtank;

import android.app.Application;

import com.mibo.fishtank.FishTankmManage.FishTankApiManager;

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
        LitePal.initialize(this);
        FishTankApiManager.getInstance().init();
    }
}
