package com.mibo.fishtank.entity;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

/**
 * Created by Administrator
 * on 2017/5/25 0025.
 */

public class User extends DataSupport {
    private int id;
    private String userName;
    @Column(unique = true, defaultValue = "unknown")
    private String tel;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
