package com.ys.idatrix.metacube.api.beans.dataswap;

/**
 * @ClassName: AuthorizedFlowType
 * @Description: 元数据表赋权限流程操作类型
 * @Author: ZhouJian
 * @Date: 2018/8/10
 */
public enum AuthorizedFlowType {

    SUBSCRIBED(0, " 共享交换订阅表格授权"),

    PUBLISHED(1, " 共享交换发布资源后对表格授权");

    private int value;

    private String despZH;

    AuthorizedFlowType(int value, String zh){
        this.value = value;
        this.despZH = zh;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getDespZH() {
        return despZH;
    }

    public void setDespZH(String despZH) {
        this.despZH = despZH;
    }
}
