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
    public static void saveScene(List<Scene> scenes) {
        clearScene();
        DataSupport.saveAll(scenes);
    }

    /**
     * 插入新的场景
     */
    private static void clearScene() {
        DataSupport.deleteAll(Scene.class);
    }

    /**
     * 查询当前用户下的所有场景
     */
    public static List<Scene> queryAllScene() {
        return DataSupport.findAll(Scene.class);
    }

}
