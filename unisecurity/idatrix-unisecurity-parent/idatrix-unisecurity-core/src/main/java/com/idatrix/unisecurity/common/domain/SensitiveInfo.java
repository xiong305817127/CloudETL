package com.idatrix.unisecurity.common.domain;

import io.swagger.annotations.ApiModel;

import java.io.Serializable;

@ApiModel(description="脱敏规则DTO")
public class SensitiveInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;

    private String name; //

    private String isFixedLength; //是否长度固定 Y/N

    private int begin; // 开始位置

    private int end; // 结束位置

    private String symbol;// 脱敏字符 ”*“

    private String originalInfo; // 	敏感词样例

    private String sentiveInfo;// 	脱敏后信息

    private String creater;

    private String deptName;

    private String createTime;

    private String updateTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIsFixedLength() {
        return isFixedLength;
    }

    public void setIsFixedLength(String isFixedLength) {
        this.isFixedLength = isFixedLength;
    }

    public int getBegin() {
        return begin;
    }

    public void setBegin(int begin) {
        this.begin = begin;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getOriginalInfo() {
        return originalInfo;
    }

    public void setOriginalInfo(String originalInfo) {
        this.originalInfo = originalInfo;
    }

    public String getSentiveInfo() {
        return sentiveInfo;
    }

    public void setSentiveInfo(String sentiveInfo) {
        this.sentiveInfo = sentiveInfo;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

}
