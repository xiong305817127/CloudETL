package com.idatrix.unisecurity.api.domain;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/12/26.
 */
public class LoginDateInfo implements Serializable {

    private String loginDate;

    private int loginCount;

    private int loginUserCount;

    private int loginDeptCount;

    public LoginDateInfo() {

    }

    public LoginDateInfo(String loginDate, int loginCount, int loginUserCount, int loginDeptCount) {
        this.loginDate = loginDate;
        this.loginCount = loginCount;
        this.loginUserCount = loginUserCount;
        this.loginDeptCount = loginDeptCount;
    }

    public String getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(String loginDate) {
        this.loginDate = loginDate;
    }

    public int getLoginCount() {
        return loginCount;
    }

    public void setLoginCount(int loginCount) {
        this.loginCount = loginCount;
    }

    public int getLoginUserCount() {
        return loginUserCount;
    }

    public void setLoginUserCount(int loginUserCount) {
        this.loginUserCount = loginUserCount;
    }

    public int getLoginDeptCount() {
        return loginDeptCount;
    }

    public void setLoginDeptCount(int loginDeptCount) {
        this.loginDeptCount = loginDeptCount;
    }
}
