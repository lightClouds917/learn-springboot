package com.java4all.entity;

/**
 * Author: yunqing
 * Date: 2018/7/12
 * Description:
 */
public class User {
    private long id;

    private String userName;

    private String realName;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

}
