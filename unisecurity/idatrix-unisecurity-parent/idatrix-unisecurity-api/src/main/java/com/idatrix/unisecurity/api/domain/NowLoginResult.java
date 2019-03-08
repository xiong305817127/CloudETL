package com.idatrix.unisecurity.api.domain;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/12/27.
 */
public class NowLoginResult implements Serializable {

    // 今日一共登录次数
    private Integer nowLoginCount;

    // 今日登录的用户数
    private Integer nowLoginUserCount;

    // 今日登录的组织数
    private Integer nowLoginDeptCount;

    // 所有的登录次数
    private Integer allLoginCount;

    public NowLoginResult() {
    }

    public NowLoginResult(Integer nowLoginCount, Integer nowLoginUserCount, Integer nowLoginDeptCount, Integer allLoginCount) {
        this.nowLoginCount = nowLoginCount;
        this.nowLoginUserCount = nowLoginUserCount;
        this.nowLoginDeptCount = nowLoginDeptCount;
        this.allLoginCount = allLoginCount;
    }

    public Integer getNowLoginCount() {
        return nowLoginCount;
    }

    public void setNowLoginCount(Integer nowLoginCount) {
        this.nowLoginCount = nowLoginCount;
    }

    public Integer getNowLoginUserCount() {
        return nowLoginUserCount;
    }

    public void setNowLoginUserCount(Integer nowLoginUserCount) {
        this.nowLoginUserCount = nowLoginUserCount;
    }

    public Integer getNowLoginDeptCount() {
        return nowLoginDeptCount;
    }

    public void setNowLoginDeptCount(Integer nowLoginDeptCount) {
        this.nowLoginDeptCount = nowLoginDeptCount;
    }

    public Integer getAllLoginCount() {
        return allLoginCount;
    }

    public void setAllLoginCount(Integer allLoginCount) {
        this.allLoginCount = allLoginCount;
    }
}
