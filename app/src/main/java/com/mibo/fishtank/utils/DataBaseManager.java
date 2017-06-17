package com.mibo.fishtank.utils;

import com.mibo.fishtank.entity.Device;
import com.mibo.fishtank.entity.Scene;
import com.mibo.fishtank.entity.User;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import static org.litepal.crud.DataSupport.findAll;

/**
 * Created by Administrator
 * on 2017/5/25 0025.
 */

public class DataBaseManager {
    private DataBaseManager manager = new DataBaseManager();

    public DataBaseManager getInstance() {
        return manager;
    }

    /**
     * 插入新的用户
     */
    public static void saveUser(String name, String tel) {
        User user = new User();
        user.setUserName(name);
        user.setTel(tel);
        user.saveOrUpdate();
    }

    /**
     * 搜索用户信息
     */
    public static User queryUser(String tel) {
        return DataSupport.where("tel = ?", tel).findFirst(User.class);
    }

    /**
     * 插入新的场景
     */
    public static void saveScene(List<Scene> scenes) {
        clearScene();
        DataSupport.saveAll(scenes);
    }


    /**
     * 删除所有场景
     */
    private static void clearScene() {
        DataSupport.deleteAll(Scene.class);
    }

    /**
     * 查询当前用户下的所有场景
     */
    public static List<Scene> queryAllScene() {
        return findAll(Scene.class);
    }

    /**
     * 查询当前设备连接的场景
     */
    public static List<String> queryAllDeviceScene(String uid) {
        Device device = DataSupport.where("uid = ?", uid).findFirst(Device.class);
        if (device == null) {
            return new ArrayList<>();
        }
        return device.getSceneIds();
    }

    /**
     * 插入新的设备
     */
    public static void saveDeviceToScene(String sceneId, String deviceUid) {
        Scene scene = DataSupport.where("sceneid = ?", sceneId).findFirst(Scene.class);
        if (scene != null) {
            ArrayList<String> devices = scene.getDevices();
            devices.add(deviceUid);
            scene.setDevices(devices);
            scene.save();
        }
    }

    /**
     * 搜索设备
     */
    public static ArrayList<Device> queryAllDevice(ArrayList<String> devices) {
        ArrayList<Device> list = new ArrayList<>();
        int size = devices.size();
        for (int i = 0; i < size; i++) {
            String s = devices.get(i);
            Device device = DataSupport.where("uid = ?", s).findFirst(Device.class);
            list.add(device);
        }
        return list;
    }

    public static void updateDevicePwd(String uid, String pwd) {
        Device device = queryDevice(uid);
        device.setDevPwd(pwd);
        device.update(device.getBaseObjId());
    }

    /**
     * 搜索单个设备
     */
    public static Device queryDevice(String deviceUid) {
        return DataSupport.where("uid = ?", deviceUid).findFirst(Device.class);
    }

    /**
     * 删除所有设备
     */
    private static void clearDevice() {
        DataSupport.deleteAll(Device.class);
    }

    /**
     * 获取当个场景下的所有设备
     */
    public static ArrayList<String> queryAllDeviceByOneScene(String sceneID) {
        Scene scene = DataSupport.where("sceneid = ?", sceneID).findFirst(Scene.class);
        return scene.getDevices();
    }

}
