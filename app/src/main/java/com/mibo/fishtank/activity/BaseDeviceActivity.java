package com.mibo.fishtank.activity;

import android.util.Log;
import android.widget.Toast;

import com.mibo.fishtank.FishTankmManage.FishTankApiManager;
import com.mibo.fishtank.FishTankmManage.event.LoginEvent;
import com.mibo.fishtank.entity.Device;
import com.mibo.fishtank.utils.Constans;
import com.mibo.fishtank.utils.DataBaseManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 有关操作设备的页面每次onStart()时都调一次登录
 */
public abstract class BaseDeviceActivity extends BaseActivity implements com.mibo.fishtank.FishTankmManage.IDeviceLoginListener {

    @Override
    public void onLoginSeccess() {

    }

    @Override
    public void onLoginFailure(String msg) {
        Toast.makeText(this, "登录失败", Toast.LENGTH_SHORT).show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLoginLstener(LoginEvent event) {
        if (event.loginSuccess) {
            onLoginSeccess();
        } else {
            onLoginFailure("Device login failure! errorCode :"+event.result);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("monty", "onStart");
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        Device device = DataBaseManager.queryDevice(Constans.DEVICE_UID);

        if (null != device) {
            FishTankApiManager.getInstance().loginDevice(device.getUid(), device.getDevPwd());
        }else{
            Toast.makeText(this, "获取设备帐号信息失败", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
