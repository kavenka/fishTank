package com.mibo.fishtank.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

import com.mibo.fishtank.utils.StatusBarCompat;

/**
 * Created by Administrator
 * on 2017/5/3 0003.
 */

public class BaseActivity extends AppCompatActivity {
    public Activity context = this;

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        StatusBarCompat.compat(this, Color.RED);
    }

    /**
     * 设置为横屏
     */
    @Override
    protected void onResume() {
        if(getRequestedOrientation()!= ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        super.onResume();
    }
}
