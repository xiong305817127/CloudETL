package com.idatrix.resource.subscribe.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * 订阅处理状态
 */
public enum SubscribeStatusEnum {

    NOT_SURE(0, "not_sure", "不确定"),
    WAIT_APPROVE(1, "wait_approve", "待审批"),
    SUCCESS(2, "success", "订阅成功"),
    FAILED(3, "failed", "已拒绝"),
    STOP(4, "stop", "订阅终止");

    private int index;
    private String status;
    private String statusZH;

    SubscribeStatusEnum(int index, String status, String statusZH){
        this.index = index;
        this.status = status;
        this.statusZH = statusZH;
    }

    public static String getStatusZH(String status){
        for(SubscribeStatusEnum enumValue:values()){
            if(StringUtils.equals(enumValue.getStatus(), status)){
                return enumValue.getStatusZH();
            }
        }
        return  NOT_SURE.getStatusZH();
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusZH() {
        return statusZH;
    }

    public void setStatusZH(String statusZH) {
        this.statusZH = statusZH;
    }
}
