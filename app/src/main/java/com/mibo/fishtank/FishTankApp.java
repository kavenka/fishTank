package com.mibo.fishtank;

import android.app.Application;

import org.litepal.LitePal;

/**
 * Created by Administrator
 * on 2017/5/25 0025.
 */

public class FishTankApp extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
    }
}
