package com.idatrix.resource.catalog.po;

import java.util.Date;

/**
 * 政务信息资源统计表
 * @Author: Wangbin
 * @Date: 2018/5/23
 */
public class ResourceStatisticsPO {

    /*主键*/
    private Long id;

    /*订阅次数*/
    private int subCount;

    /*浏览次数*/
    private int visitCount;

    /*数据总量，文件类型计算计算而文件个数*/
    private Long dataCount;

    /*交换数据是总量*/
    private Long shareDataCount;

    /*数据更新时间*/
    private Date dataUpdateTime;

    private String creator;

    private Date createTime;

    private String modifier;

    private Date modifyTime;

    public ResourceStatisticsPO(){
        this.subCount = 0;
        this.visitCount = 0;
        this.shareDataCount = 0L;
        this.dataCount = 0L;
    }

    public Date getDataUpdateTime() {
        return dataUpdateTime;
    }

    public void setDataUpdateTime(Date dataUpdateTime) {
        this.dataUpdateTime = dataUpdateTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getSubCount() {
        return subCount;
    }

    public void setSubCount(int subCount) {
        this.subCount = subCount;
    }

    public int getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(int visitCount) {
        this.visitCount = visitCount;
    }

    public Long getDataCount() {
        return dataCount;
    }

    public void setDataCount(Long dataCount) {
        this.dataCount = dataCount;
    }

    public Long getShareDataCount() {
        return shareDataCount;
    }

    public void setShareDataCount(Long shareDataCount) {
        this.shareDataCount = shareDataCount;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }
}
