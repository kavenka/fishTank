package com.mibo.fishtank.FishTankmManage;

import android.os.Handler;
import android.os.Message;

import com.landstek.iFishTank.CloudApi;
import com.landstek.iFishTank.LSmartLink;
import com.mibo.fishtank.FishTankmManage.event.UserLoginEvent;
import com.mibo.fishtank.FishTankmManage.event.UserLoginOutEvent;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Administrator
 * on 2017/5/29 0029.
 */

public class FishTankUserApiManager {

    private static FishTankUserApiManager fishTankUserApiManager;
    private CloudApi cloudApi;
    private LSmartLink mLSmartLink;

    public static FishTankUserApiManager getInstance() {
        if (fishTankUserApiManager == null) {
            fishTankUserApiManager = new FishTankUserApiManager();
        }
        return fishTankUserApiManager;
    }

    public void init() {
        cloudApi = new CloudApi();
        cloudApi.SetHandler(handler);
    }

    /**
     * 登录
     */
    public void toSignIn(String user, String pwd) {
        cloudApi.SignIn(user, pwd);
    }

    /**
     * 登出
     */
    public void toSignOut() {
        cloudApi.SignOut();
    }

    /**
     * 获取验证码
     */
    public void toSendSmsForVerifyCode(String tel) {
        cloudApi.SmsGetVerifyCode(tel);
    }

    /**
     * 修改用户登录密码
     */
    public void toModify(String newPwd, String oldPwd) {
        cloudApi.Modify(newPwd, oldPwd);
    }

    /**
     * 重置用户密码
     */
    public void toResetPwd(String userName, String pwd, String tel, String verifyCode) {
        cloudApi.ResetPwd(userName, pwd, tel, verifyCode);
    }

    /**
     * 注册
     */
    public void toRegister(String userName, String pwd, String tel, String verifyCode) {
        cloudApi.Register(userName, pwd, tel, verifyCode);
    }

    /**
     * 添加或更新与本用户关联的设备
     */
    public void toAddOrUpdateDev(CloudApi.DevCfg cfg) {
        cloudApi.AddOrUpdateDevCfg(cfg);
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CloudApi.MSG_WHAT_CLOUDAPI:
                    switch (msg.arg1) {
                        case CloudApi.SIGNIN://登录
                            EventBus.getDefault().postSticky(new UserLoginEvent(msg));
                            break;
                        case CloudApi.SIGNOUT://登出
                            EventBus.getDefault().postSticky(new UserLoginOutEvent(msg));
                            break;
                        case CloudApi.SMSGETVERIFYCODE://发送验证码
                            break;
                        case CloudApi.MODIFY://修改密码
                            break;
                        case CloudApi.RESETPWD://重置密码
                            break;
                        case CloudApi.REGISTER://注冊
                            break;
                        case CloudApi.ADDORUPDATEDEVCFG://添加或更新与本用户关联的设备
                            break;
                        case CloudApi.ERROR://错误
                            break;
                    }
                    break;
            }
        }
    };

}

