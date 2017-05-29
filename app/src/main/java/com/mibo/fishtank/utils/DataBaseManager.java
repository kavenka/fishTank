package com.mibo.fishtank.utils;

import com.mibo.fishtank.entity.Scene;
import com.mibo.fishtank.entity.User;

import org.litepal.crud.DataSupport;

import java.util.List;

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
     * 插入新的场景
     */
    public static boolean saveScene(String sceneName, String tel) {
        User user = DataSupport.where("tel = ?", tel).findFirst(User.class);
        Scene scene = new Scene();
        scene.setSceneName(sceneName);
        scene.save();
        user.getScenes().add(scene);
        return user.save();
    }

    /**
     * 查询当前用户下的所有场景
     */
    public static List<Scene> queryAllSceneByTel(String tel) {
        int id = DataSupport.where("tel = ?", tel).findFirst(User.class).getId();
        return DataSupport.where("user_id = ?", String.valueOf(id)).find(Scene.class);
    }

    /**
     * 查询当前索连接的设备
     */
    public static void queryAllSceneByTel(String tel, String sceneName) {
        int id = DataSupport.where("tel = ?", tel).findFirst(User.class).getId();
        Scene scene = DataSupport.where("user_id = ?", String.valueOf(id), "scenename = ?", sceneName).findFirst(Scene.class);
//        scene.getSceneName()
    }
}
