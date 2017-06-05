package com.mibo.fishtank.FishTankmManage;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.landstek.iFishTank.CloudApi;
import com.landstek.iFishTank.LSmartLink;
import com.mibo.fishtank.FishTankmManage.event.AddOrUpSceneEvent;
import com.mibo.fishtank.FishTankmManage.event.DevCfgEvent;
import com.mibo.fishtank.FishTankmManage.event.UserLoginEvent;
import com.mibo.fishtank.FishTankmManage.event.UserLoginOutEvent;
import com.mibo.fishtank.entity.Device;
import com.mibo.fishtank.entity.Scene;
import com.mibo.fishtank.utils.DataBaseManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

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
     * 获取当前用户下的设备参数
     */
    public void toGetDevCfg() {
        cloudApi.GetDevCfg();
    }

    @NonNull
    private CloudApi.DevCfg getDevCfg() {
        CloudApi.DevCfg devCfg = new CloudApi.DevCfg();
        devCfg.Type = 0;
        devCfg.Vendor = "Vendor";
        devCfg.Model = "Model";
        devCfg.Uid = "Uid";
        devCfg.User = "User";
        devCfg.Pwd = "Pwd";
        return devCfg;
    }

    /**
     * 更新场景
     */
    public void toAddScene(String sceneName) {
        CloudApi.DevCfg devCfg = getDevCfg();
        Scene scene = new Scene();
        scene.setName(sceneName);
        long time = System.currentTimeMillis() / 1000;//获取系统时间的10位的时间戳
        String str = String.valueOf(time);
        scene.setSceneID(str);
        Gson gson = new Gson();
        ArrayList<Scene> scenes = new ArrayList<>();
        List<Scene> dataScenes = DataBaseManager.queryAllScene();
        scenes.addAll(dataScenes);
        scenes.add(scene);
        String scenesArrayStr = gson.toJson(scenes);
        devCfg.Data = Base64.encodeToString(scenesArrayStr.getBytes(), Base64.DEFAULT);
        cloudApi.AddOrUpdateDevCfg(devCfg);
    }

    /**
     * 删除场景
     */
    public void toDeleteScene(String sceneID) {
        CloudApi.DevCfg devCfg = getDevCfg();
        List<Scene> scenes = DataBaseManager.queryAllScene();
        for (int i = 0; i < scenes.size(); i++) {
            Scene scene = scenes.get(i);
            if (TextUtils.equals(sceneID, scene.getSceneID())) {
                scenes.remove(i);
                break;
            }
        }
        Gson gson = new Gson();
        String scenesArrayStr = gson.toJson(scenes);
        devCfg.Data = Base64.encodeToString(scenesArrayStr.getBytes(), Base64.DEFAULT);
        cloudApi.AddOrUpdateDevCfg(devCfg);
    }

    /**
     * 增加设备
     */
    public void toAddDevice(String uid, String sceneId, String model) {
        List<String> sceneIds = DataBaseManager.queryAllDeviceScene(uid);
        sceneIds.add(sceneId);
        int size = sceneIds.size();
        StringBuilder stringBuffer = new StringBuilder();
        for (int i = 0; i < size; i++) {
            stringBuffer.append(sceneIds.get(i));
            if (size > 1 && i != size - 1) {
                stringBuffer.append("&");
            }
        }
        CloudApi.DevCfg devCfg = new CloudApi.DevCfg();
        devCfg.Type = 1;
        devCfg.Vendor = "1";
        devCfg.Model = model;
        devCfg.Uid = uid;
        devCfg.User = "admin";
        devCfg.Pwd = "12345678";
        devCfg.Data = "";
        devCfg.Custom = stringBuffer.toString().getBytes();
        cloudApi.AddOrUpdateDevCfg(devCfg);
    }

    /**
     * 更新设备
     *
     * @param uid
     */
    public void toUpdateDevice(String uid, String pwd) {
        List<String> sceneIds = DataBaseManager.queryAllDeviceScene(uid);
        Device device = DataBaseManager.queryDevice(uid);
        int size = sceneIds.size();
        StringBuilder stringBuffer = new StringBuilder();
        for (int i = 0; i < size; i++) {
            stringBuffer.append(sceneIds.get(i));
            if (size > 1 && i != size - 1) {
                stringBuffer.append("&");
            }
        }
        CloudApi.DevCfg devCfg = new CloudApi.DevCfg();
        devCfg.Type = 1;
        devCfg.Vendor = "1";
        devCfg.Model = device.getModel();
        devCfg.Uid = uid;
        devCfg.User = "admin";
        devCfg.Pwd = pwd;
        devCfg.Data = "";
        devCfg.Custom = stringBuffer.toString().getBytes();
        cloudApi.AddOrUpdateDevCfg(devCfg);
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
                        case CloudApi.GETDEVCFG://获取当前用户下的设备参数
                            EventBus.getDefault().post(new DevCfgEvent(msg));
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
                            Log.d("monty", "FishTankUserApiManager：toUpdateDevice -> msg:" + msg.toString());
                            EventBus.getDefault().postSticky(new AddOrUpSceneEvent(msg));
                            break;
                        case CloudApi.ERROR://错误
                            break;
                    }
                    break;
            }
        }
    };

}

