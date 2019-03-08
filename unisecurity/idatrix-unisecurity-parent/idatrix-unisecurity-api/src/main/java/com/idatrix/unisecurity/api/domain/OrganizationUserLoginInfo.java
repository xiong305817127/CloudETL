package com.idatrix.unisecurity.api.domain;

import java.io.Serializable;

/**
 * 部门用户登录信息实体类
 * Created by Administrator on 2018/12/26.
 */
public class OrganizationUserLoginInfo implements Serializable {

    private String deptName; // 所属部门name

    private int count; // 所属部门下的用户登录次数

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
